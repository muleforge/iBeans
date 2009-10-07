/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.internal.client;

import org.mule.api.MuleContext;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.api.endpoint.ImmutableEndpoint;
import org.mule.api.routing.InterfaceBinding;
import org.mule.api.routing.filter.Filter;
import org.mule.api.service.Service;
import org.mule.api.transformer.Transformer;
import org.mule.api.transformer.TransformerException;
import org.mule.api.transport.PropertyScope;
import org.mule.config.ExceptionHelper;
import org.mule.config.i18n.CoreMessages;
import org.mule.ibeans.IBeansException;
import org.mule.ibeans.api.client.CallException;
import org.mule.ibeans.api.client.Return;
import org.mule.ibeans.api.client.params.InvocationContext;
import org.mule.module.xml.transformer.XmlPrettyPrinter;
import org.mule.routing.filters.ExpressionFilter;
import org.mule.transport.NullPayload;
import org.mule.util.StringMessageUtils;
import org.mule.util.TemplateParser;

import java.beans.ExceptionListener;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.Iterator;
import java.util.Map;

import javax.xml.xpath.XPathConstants;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;

/**
 * The proxy handler responsible for making calls on behalf of the the IntegrationBean.  This handler maintains any state
 * and parses any parameter annotations before making the call.
 */
public class IntegrationBeanInvocationHandler implements InvocationHandler
{
    protected static Log logger = LogFactory.getLog(IntegrationBeanInvocationHandler.class);

    protected MuleContext muleContext;

    protected TemplateAnnotationHandler templateHandler;
    protected CallAnnotationHandler callHandler;

    protected ExceptionListener exceptionListener;

    protected IBeanParamsHelper helper;

    protected TemplateParser parser = TemplateParser.createCurlyBracesStyleParser();

    public IntegrationBeanInvocationHandler(Class iface, Service service, MuleContext muleContext)
    {
        if (muleContext == null)
        {
            throw new IllegalArgumentException(CoreMessages.objectIsNull("MuleContext").toString());
        }

        if (service == null)
        {
            throw new IllegalArgumentException(CoreMessages.objectIsNull("Service").toString());
        }

        if (iface == null)
        {
            throw new IllegalArgumentException(CoreMessages.objectIsNull("IBean Interface").toString());
        }

        this.muleContext = muleContext;
        helper = new IBeanParamsHelper(muleContext, iface);
        templateHandler = new TemplateAnnotationHandler(muleContext);
        callHandler = new CallAnnotationHandler(muleContext, service, helper);
    }

    public void addRouter(InterfaceBinding router)
    {
        callHandler.addRouterForInterface(router);

    }

    public TemplateAnnotationHandler getTemplateHandler()
    {
        return templateHandler;
    }

    public CallAnnotationHandler getCallHandler()
    {
        return callHandler;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
    {
        if (method.getName().equals("toString"))
        {
            return toString();
        }
        else if (method.getName().equals("hashCode"))
        {
            return hashCode();
        }
        else if (method.getName().equals("equals"))
        {
            return equals(args[0]);
        }
        else if (method.getName().equals("setExceptionListener"))
        {
            exceptionListener = (ExceptionListener) args[0];
            return null;
        }

        InvocationContext ctx = helper.createInvocationContext(method, args);
        //Not keen on proprty munging here be the endpoint sometimes has default props
        //that we should make available to the context
        ImmutableEndpoint endpoint = getCallHandler().getEndpointForMethod(method);
        if (endpoint != null && endpoint.getProperties().size() > 0)
        {
            for (Iterator iterator = endpoint.getProperties().entrySet().iterator(); iterator.hasNext();)
            {
                Map.Entry entry = (Map.Entry) iterator.next();
                ctx.getPropertyParams().put(entry.getKey().toString(), entry.getValue());
            }
        }

        if (ctx.isStateCall())
        {
            //State methods return null
            return null;
        }
        MuleMessage message = helper.createMessage(ctx);

        MuleMessage result;
        String scheme;

        if (logger.isTraceEnabled())
        {
            try
            {
                logger.trace("Message Before invoking " + method + ": \n"
                        + StringMessageUtils.truncate(StringMessageUtils.toString(message.getPayload()), 2000, false));
                logger.trace("Message Headers: \n" + StringMessageUtils.headersToString(message));
            }
            catch (Exception e)
            {
                // ignore
            }
        }


        if (getTemplateHandler() != null && getTemplateHandler().isMatch(method))
        {
            scheme = getTemplateHandler().getScheme(method);
            result = getTemplateHandler().invoke(proxy, method, args, message);
        }
        else
        {
            scheme = getCallHandler().getScheme(method);

            result = getCallHandler().invoke(proxy, method, args, message);
        }

        if (logger.isTraceEnabled())
        {
            try
            {
                logger.trace("Message After invoking " + method + ": \n"
                        + StringMessageUtils.truncate(StringMessageUtils.toString(message.getPayload()), 2000, false));
                logger.trace("Message Headers: \n" + StringMessageUtils.headersToString(result));
            }
            catch (Exception e)
            {
                // ignore
            }
        }


        Object finalResult = null;
        if (result != null)
        {
            if (result.getExceptionPayload() != null)
            {
                Throwable t = result.getExceptionPayload().getRootException();
                if (exceptionListener != null)
                {
                    if (Exception.class.isAssignableFrom(t.getClass()))
                    {
                        exceptionListener.exceptionThrown((Exception) t);
                    }
                    else
                    {
                        exceptionListener.exceptionThrown(new Exception(t));
                    }
                }
                else
                {
                    t = createCallException(result, t, scheme);
                    throw t;
                }
            }
            else if (result.getPayload() instanceof NullPayload || method.getReturnType().equals(Void.TYPE))
            {
                return null;
            }
            else if (method.getAnnotation(Return.class) != null)
            {
                String returnExpression = method.getAnnotation(Return.class).value();

                finalResult = handlerReturnAnnotation(returnExpression, result, ctx);

                if (!ctx.getReturnType().isInstance(finalResult))
                {
                    Transformer transformer = muleContext.getRegistry().lookupTransformer(finalResult.getClass(), ctx.getReturnType());
                    finalResult = transformer.transform(finalResult);
                }
            }
            else
            {
                Class retType = ctx.getReturnType();
                if (retType.equals(MuleMessage.class))
                {
                    finalResult = result;
                }
                else
                {
                    try
                    {
                        finalResult = ctx.getIBeansContext().transform(result, retType);
                    }
                    catch (TransformerException e)
                    {
                        Exception ex = createCallException(result, e, scheme);
                        if (exceptionListener != null)
                        {
                            exceptionListener.exceptionThrown(ex);
                        }
                        else
                        {
                            throw ex;
                        }
                    }
                }
            }

            if (isErrorReply(method, finalResult, result))
            {
                //TODO URGENT remove add dependency to Xml
                String msg;
                if (result.getPayload() instanceof Document)
                {
                    msg = (String) new XmlPrettyPrinter().transform(result.getPayload());
                }
                else
                {
                    msg = result.getPayloadAsString();
                }
                Exception e = createCallException(result, new IBeansException(msg), scheme);
                if (exceptionListener != null)
                {
                    exceptionListener.exceptionThrown(e);
                }
                else
                {
                    throw e;
                }
            }
            return finalResult;

        }
        return null;
    }

    protected Object handlerReturnAnnotation(String expr, MuleMessage message, InvocationContext ctx)
    {
        if (parser.isContainsTemplate(expr))
        {
            expr = parser.parse(ctx.getUriParams(), expr);
            expr = parser.parse(ctx.getHeaderParams(), expr);
            expr = parser.parse(ctx.getPropertyParams(), expr);
        }

        if (Boolean.class.equals(ctx.getReturnType()) || boolean.class.equals(ctx.getReturnType()))
        {
            ExpressionFilter filter = new ExpressionFilter(expr);
            filter.setMuleContext(muleContext);
            return filter.accept(message);
        }
        else
        {
            return muleContext.getExpressionManager().evaluate(expr, message);
        }
    }

    protected boolean isErrorReply(Method method, Object finalResult, MuleMessage message)
    {
        if (finalResult == null)
        {
            return false;
        }
        MuleMessage test = message;
        if (finalResult instanceof MuleMessage)
        {
            test = (MuleMessage) finalResult;
        }
        else
        {
            test.setPayload(finalResult);
        }
        String mime = getMimeForMessage(test);
        Filter f = helper.getMethodErrorFilters().get(method);
        if (f == null)
        {
            f = helper.getErrorFilters().get(mime);
        }

        if (f == null)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("No matching error filter for Mime Type: " + mime);
            }
            return false;
        }
        else
        {
            //TODO Urgent, fix this
            test.setProperty("xpath.return", XPathConstants.BOOLEAN, PropertyScope.INVOCATION);
            return f.accept(test);
        }
    }

    protected String getMimeForMessage(MuleMessage message)
    {
        String mime = (String) message.getProperty("Content-Type");
        if (mime == null)
        {
            mime = "*";
        }
        else if (mime.indexOf(";") > -1)
        {
            mime = mime.substring(0, mime.indexOf(";"));
        }
        return mime;
    }


    protected CallException createCallException(MuleMessage message, Throwable t, String protocol)
    {
        if (t instanceof InvocationTargetException || t instanceof UndeclaredThrowableException)
        {
            t = t.getCause();
        }
        String mime = getMimeForMessage(message);
        ErrorExpressionFilter f = helper.getErrorFilters().get(mime);
        Object errorCode = null;
        if (f != null && f.getErrorCodeExpr() != null)
        {
            errorCode = muleContext.getExpressionManager().evaluate(f.getErrorCodeExpr(), f.getEvaluator(), message, false);
        }
        Throwable root = ExceptionHelper.getRootException(t);
        MuleException muleException = ExceptionHelper.getRootMuleException(t);
        if (errorCode == null)
        {
            String statusCodeName = ExceptionHelper.getErrorCodePropertyName(protocol);
            errorCode = message.getProperty(statusCodeName);
        }

        if (errorCode != null)
        {
            errorCode = errorCode.toString();
        }
        CallException ce = new CallException(t.getMessage(), (String) errorCode, root);
        for (Iterator iterator = message.getPropertyNames().iterator(); iterator.hasNext();)
        {
            String name = (String) iterator.next();
            ce.getInfo().put(name, message.getProperty(name));
        }
        if (muleException != null)
        {
            ce.getInfo().putAll(muleException.getInfo());
        }
        try
        {
            ce.getInfo().put("response.payload", message.getPayloadAsString());
        }
        catch (Exception e1)
        {
            ce.getInfo().put("exception.handler.error", e1.getMessage());
        }
        return ce;
    }

    public String toString()
    {
        final StringBuffer sb = new StringBuffer();
        sb.append("IBean");
        sb.append("{Calls: ").append(getCallHandler());
        sb.append("}, {Templates: ").append(getTemplateHandler());
        sb.append('}');
        return sb.toString();
    }
}
