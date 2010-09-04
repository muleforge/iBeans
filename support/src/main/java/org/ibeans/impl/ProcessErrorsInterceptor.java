/*
 * $Id:  $
 * -------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.ibeans.impl;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ibeans.api.AbstractCallInterceptor;
import org.ibeans.api.CallException;
import org.ibeans.api.IBeansException;
import org.ibeans.api.InvocationContext;
import org.ibeans.api.Response;
import org.ibeans.impl.support.util.Utils;
import org.ibeans.spi.ErrorFilter;
import org.ibeans.spi.Filter;
import org.w3c.dom.Document;

/**
 * TODO
 */
final class ProcessErrorsInterceptor extends AbstractCallInterceptor
{
    /**
     * logger used by this class
     */
    protected transient final Log logger = LogFactory.getLog(ProcessErrorsInterceptor.class);

    public void afterCall(InvocationContext invocationContext) throws Exception
    {
        Response response = invocationContext.getResponse();

        if (isErrorReply(invocationContext))
        {
            String msg;
            if (response.getPayload() instanceof Document)
            {

                msg = prettyPrint((Document) response.getPayload());
            }
            else
            {
                msg = response.getPayload().toString();
            }
            Exception e = createCallException(invocationContext, new IBeansException(msg));
            if (invocationContext.getExceptionListener() != null)
            {
                invocationContext.getExceptionListener().exceptionThrown(e);
            }
            else
            {
                throw e;
            }
        }
    }

    private String prettyPrint(Document document) throws IOException
    {
        StringWriter stringWriter = new StringWriter();
        XMLSerializer serializer = new XMLSerializer(stringWriter, new OutputFormat(com.sun.org.apache.xml.internal.serialize.Method.XML, "UTF-8", true));
        serializer.serialize(document);
        return stringWriter.toString();
    }

    protected boolean isErrorReply(InvocationContext context) throws IBeansException
    {
        if(context.getMethod().getName().startsWith("ibean"))
        {
            return false;
        }
        Response response = context.getResponse();
        Object finalResult = context.getResult();
        if (finalResult == null || finalResult.equals(""))
        {
            return false;
        }
//        if (finalResult instanceof MuleMessage)
//        {
//            test = (MuleMessage) finalResult;
//        }
//        else
//        {
//            test.setPayload(finalResult);
//        }
//
        String mime = response.getMimeType();
        Filter f = context.getIBeanDefaultConfig().getMethodLevelErrorFilters().get(context.getMethod());
        if (f == null)
        {
            f = context.getIBeanDefaultConfig().getErrorFilters().get(mime);
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
            return f.accept(response);
        }
    }

    public static CallException createCallException(InvocationContext context, Throwable t)
    {
        if (t instanceof InvocationTargetException || t instanceof UndeclaredThrowableException)
        {
            t = t.getCause();
        }
        ErrorFilter filter = null;
        Response response = context.getResponse();
        if (response != null)
        {
            String mime = response.getMimeType();
            filter = context.getIBeanDefaultConfig().getErrorFilters().get(mime);
        }


        Object errorCode = null;

        Throwable root = t;
        while (root.getCause() != null)
        {
            root = root.getCause();
        }

        if (filter != null)
        {

            if (filter.getErrorCodeExpression() != null)
            {
                errorCode = context.getExpressionParser().evaluate(filter.getType(), filter.getErrorCodeExpression(), response);
                //if errorCode is non-numeric, return the http status code instead
                if (errorCode != null && !Utils.isNumeric(errorCode.toString()))
                {
                    errorCode = null;
                }
            }
        }

        if (errorCode == null && response != null)
        {
            String statusCodeName = "http.status";
            errorCode = response.getHeader(statusCodeName);
        }

        if (errorCode != null)
        {
            errorCode = errorCode.toString();
        }
        CallException ce = new CallException(t.getMessage(), (String) errorCode, root);

        if (response != null)
        {
            for (String name : response.getHeaderNames())
            {
                ce.getInfo().put(name, response.getHeader(name));
            }

            try
            {
                ce.getInfo().put("response.payload", response.getPayload());
            }
            catch (Exception e1)
            {
                ce.getInfo().put("exception.handler.error", e1.getMessage());
            }
        }

        return ce;
    }
}
