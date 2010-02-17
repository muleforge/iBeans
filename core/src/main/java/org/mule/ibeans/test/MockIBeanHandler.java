/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.test;

import org.mule.DefaultMuleMessage;
import org.mule.api.MuleContext;
import org.mule.api.MuleMessage;
import org.mule.api.service.Service;
import org.mule.api.transformer.DataType;
import org.mule.ibeans.IBeansException;
import org.mule.ibeans.api.client.Return;
import org.mule.ibeans.api.client.Template;
import org.mule.ibeans.api.client.params.InvocationContext;
import org.mule.ibeans.internal.client.CallAnnotationHandler;
import org.mule.ibeans.internal.client.IBeanParamsHelper;
import org.mule.ibeans.internal.client.IntegrationBeanInvocationHandler;
import org.mule.ibeans.internal.client.TemplateAnnotationHandler;
import org.mule.model.seda.SedaService;
import org.mule.transformer.types.DataTypeFactory;
import org.mule.utils.AnnotationMetaData;
import org.mule.utils.AnnotationUtils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.activation.DataSource;

/**
 * The proxy handler used to handle calls made to an iBean proxy generated using the {@link org.mule.ibeans.api.client.MockIntegrationBean}annotation.
 */
public class MockIBeanHandler extends IntegrationBeanInvocationHandler implements MockIBean
{
    private InvocationContext invocationContext;
    protected Object mock;
    protected MockMessageCallback callback;


    public MockIBeanHandler(Class iface, MuleContext muleContext, Object mock) throws IBeansException
    {
        super(iface, new SedaService(), muleContext);
        this.mock = mock;

        //We need to initialise the TempalteHandler ourselves since the real implementation does this when
        //Scaning the class for call annotations
        Map<String, String> evals = new HashMap<String, String>();
        List<AnnotationMetaData> annos = AnnotationUtils.getMethodAnnotations(iface, Template.class);
        for (AnnotationMetaData anno : annos)
        {
            evals.put(anno.getMember().toString(), ((Template) anno.getAnnotation()).value());
        }
        templateHandler.setEvals(evals);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
    {
        //This work around ensures that the mime type for the last invocation does not get used
        //for a template invocation. Need to figure out why this is needed
        if (method.isAnnotationPresent(Template.class))
        {
            helper.setInvocationReturnType(new DataTypeFactory().createFromReturnType(method));
        }
        //Special handling of methods with an ibean prefix, these are called by the the IBeansTestSupport
        //To pass in additional information from the testcase
        if (method.getName().startsWith("ibean"))
        {
            return method.invoke(this, args);
        }
        return super.invoke(proxy, method, args);
    }

    /**
     * @param message
     * @return
     */
    @Override
    protected String getMimeForMessage(MuleMessage message)
    {
        if (helper.getInvocationReturnType() != null)
        {
            return helper.getInvocationReturnType().getMimeType();
        }
        else if (helper.getReturnType() != null)
        {
            return helper.getReturnType().getMimeType();
        }
        return null;
    }

    @Override
    protected CallAnnotationHandler createCallHandler(MuleContext muleContext, Service service, IBeanParamsHelper helper)
    {
        return new MockCallHandler(muleContext, service, helper);
    }

    public void ibeanSetMimeType(String mime)
    {
        if (helper.getReturnType() != null)
        {
            helper.setInvocationReturnType(new DataTypeFactory().create(helper.getReturnType().getType(), mime));
        }
    }

    public DataType ibeanReturnType()
    {
        if (invocationContext == null || invocationContext.getReturnType().getType().getName().equals("void"))
        {
            return helper.getReturnType();
        }
        else
        {
            return invocationContext.getReturnType();
        }
    }

    public Object ibeanUriParam(String name)
    {
        if (invocationContext == null)
        {
            return helper.getDefaultUriParams().get(name);
        }
        else
        {
            return invocationContext.getUriParams().get(name);
        }
    }

    public Object ibeanHeaderParam(String name)
    {
        if (invocationContext == null)
        {
            return helper.getDefaultHeaderParams().get(name);
        }
        else
        {
            return invocationContext.getHeaderParams().get(name);
        }
    }

    public Object ibeanPropertyParam(String name)
    {
        if (invocationContext == null)
        {
            return helper.getDefaultPropertyParams().get(name);
        }
        else
        {
            return invocationContext.getPropertyParams().get(name);
        }
    }

    public Object ibeanPayloadParam(String name)
    {
        if (invocationContext == null)
        {
            return helper.getDefaultPayloadParams().get(name);
        }
        else
        {
            return invocationContext.getRequestPayloadParams().get(name);
        }
    }

    public List<Object> ibeanPayloads()
    {
        if (invocationContext == null)
        {
            return null;
        }
        else
        {
            return invocationContext.getRequestPayloads();
        }
    }

    public Set<DataSource> ibeanAttachments()
    {
        if (invocationContext == null)
        {
            return null;
        }
        else
        {
            return invocationContext.getRequestAttachments();
        }
    }


    public void ibeanSetMessageCallback(MockMessageCallback callback)
    {
        this.callback = callback;
    }

    public TemplateAnnotationHandler getTemplateHandler()
    {
        return templateHandler;
    }

    @Override
    protected boolean isErrorReply(Method method, Object finalResult, MuleMessage message)
    {
        //IF a Return annotation is used we need to avoid applying the error Filter since the return annotation
        //Will return a new result and probably not of the same mime type as the response from the service call.
        //We need to jump through this hoop because the Mockito mock with check return type as soon as the method
        //is called which does not allow us to evaluate the @Return expression to obtain the real method result.
        //When not running as a mock this is handled because the type checking is not done until all annotations
        //have been precessed for the request/response
        if (method.isAnnotationPresent(Return.class))
        {
            return false;
        }
        else
        {
            try
            {
                //We might get an exception here since the ErrorFilter may get invoked on a transformed object
                //that is no longer of type expected by the filter.  This happens only with Mocks because the result
                //from the Mock method call is evaluated earlier or because the mime type is set on the mock and does
                //not get cleared before the next method call.
                return super.isErrorReply(method, finalResult, message);
            }
            catch (Exception e)
            {
                return false;
            }
        }
    }

    protected class MockCallHandler extends CallAnnotationHandler
    {
        public MockCallHandler(MuleContext muleContext, Service service, IBeanParamsHelper helper)
        {
            super(muleContext, service, helper);
        }

        public MuleMessage invoke(InvocationContext ctx, MuleMessage message) throws Exception
        {
            Object object = ctx.getMethod().invoke(mock, ctx.getArgs());

            MuleMessage result = new DefaultMuleMessage(object, message, muleContext);
            if(callback!=null)
            {
                try
                {
                    callback.onMessage(result);
                }
                finally
                {
                    //Only run it once
                    callback = null;
                }

            }
            return result;
        }

        public String getScheme(Method method)
        {
            //Default to http, should not make a difference for a Mock iBean
            return "http";
        }
    }
}
