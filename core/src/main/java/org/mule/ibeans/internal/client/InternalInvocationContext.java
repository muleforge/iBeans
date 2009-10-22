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
import org.mule.api.MuleMessage;
import org.mule.api.transport.PropertyScope;
import org.mule.ibeans.IBeansContext;
import org.mule.ibeans.api.client.Call;
import org.mule.ibeans.api.client.CallInterceptor;
import org.mule.ibeans.api.client.State;
import org.mule.ibeans.api.client.Template;
import org.mule.ibeans.api.client.params.InvocationContext;
import org.mule.ibeans.internal.util.UriParamFilter;
import org.mule.impl.endpoint.AnnotatedEndpointData;
import org.mule.util.ObjectUtils;
import org.mule.util.PropertiesUtils;
import org.mule.util.TemplateParser;

import java.beans.ExceptionListener;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import javax.activation.DataSource;

/**
 * Holds the current state of an iBean at the point a method invocation was made.
 * This object is used by the {@link org.mule.ibeans.api.client.params.ParamFactory}
 * to pass the current state into the factory.
 */
public class InternalInvocationContext implements InvocationContext
{

    Map<String, Object> headerParams = new TreeMap<String, Object>();
    Map<String, Object> payloadParams = new TreeMap<String, Object>();
    Map<String, Object> uriParams = new TreeMap<String, Object>();
    Map<String, Object> propertyParams = new TreeMap<String, Object>();
    List<Object> payloads = new ArrayList<Object>();
    List<DataSource> attachments = new ArrayList<DataSource>();
    Method method;
    Call call;
    Template template;
    Boolean stateCall;
    Class returnType;
    IBeansContext iBeansContext;

    Object proxy;
    Object[] args;

    ExceptionListener exceptionListener;
    CallInterceptorChain interceptorChain;
    MuleMessage requestMuleMessage;
    MuleMessage responseMuleMessage;
    Object result;
    Throwable exception;

    public InternalInvocationContext(Object proxy,
                             Method method,
                             Object[] args,
                             MuleContext muleContext,
                             ExceptionListener exceptionListener,
                             List<CallInterceptor> interceptors) throws Exception
    {
        this.interceptorChain = new CallInterceptorChain(interceptors);
        this.method = method;
        this.args = args;
        this.proxy = proxy;
        this.exceptionListener = exceptionListener;

        call = method.getAnnotation(Call.class);
        template = method.getAnnotation(Template.class);
        iBeansContext = muleContext.getRegistry().lookupObject(IBeansContext.class);
        if (call == null)
        {
            // Template method
            return;
        }

        // Add non-variablized parameters to the uriParams
        final String fullUri = call.uri();
        String uri = fullUri.substring(fullUri.indexOf('?') + 1);

        Properties queryParams = PropertiesUtils.getPropertiesFromQueryString(uri);
        for (Iterator<Object> iterator = queryParams.keySet().iterator(); iterator.hasNext();)
        {
            String key = (String) iterator.next();
            if (!getUriParams().containsKey(key))
            {
                getUriParams().put(key, queryParams.getProperty(key));
            }

        }
        // finally, add the endpoint properties to the propertyParams
        if (call.properties().length > 0)
        {
            this.propertyParams.putAll(AnnotatedEndpointData.convert(call.properties()));
        }

    }

    /* (non-Javadoc)
     * @see org.mule.ibeans.api.client.params.InvocationContextInterface#getIBeansContext()
     */
    public IBeansContext getIBeansContext()
    {
        return iBeansContext;
    }

    /* (non-Javadoc)
     * @see org.mule.ibeans.api.client.params.InvocationContextInterface#getUriParams()
     */
    public Map<String, Object> getUriParams()
    {
        return uriParams;
    }

    /* (non-Javadoc)
     * @see org.mule.ibeans.api.client.params.InvocationContextInterface#getHeaderParams()
     */
    public Map<String, Object> getHeaderParams()
    {
        return headerParams;
    }

    /* (non-Javadoc)
     * @see org.mule.ibeans.api.client.params.InvocationContextInterface#getPropertyParams()
     */
    public Map<String, Object> getPropertyParams()
    {
        return propertyParams;
    }

    /* (non-Javadoc)
     * @see org.mule.ibeans.api.client.params.InvocationContextInterface#getParam(java.lang.String)
     */
    public Object getParam(String key)
    {
        Object value = uriParams.get(key);
        if (value == null)
        {
            value = headerParams.get(key);
            if (value == null)
            {
                value = propertyParams.get(key);
            }
        }
        return value;

    }

    /* (non-Javadoc)
     * @see org.mule.ibeans.api.client.params.InvocationContextInterface#getMethod()
     */
    public Method getMethod()
    {
        return method;
    }

    /* (non-Javadoc)
     * @see org.mule.ibeans.api.client.params.InvocationContextInterface#keysToLowerCase(java.util.Map)
     */
    public Map<String, Object> keysToLowerCase(Map<String, Object> params)
    {
        Map<String, Object> newMap = new TreeMap<String, Object>();
        for (Map.Entry<String, Object> entry : params.entrySet())
        {
            newMap.put(entry.getKey().toLowerCase(), entry.getValue());
        }
        return newMap;
    }

    /* (non-Javadoc)
     * @see org.mule.ibeans.api.client.params.InvocationContextInterface#keysToUpperCase(java.util.Map)
     */
    public Map<String, Object> keysToUpperCase(Map<String, Object> params)
    {
        Map<String, Object> newMap = new TreeMap<String, Object>();
        for (Map.Entry<String, Object> entry : params.entrySet())
        {
            newMap.put(entry.getKey().toUpperCase(), entry.getValue());
        }
        return newMap;
    }

    /* (non-Javadoc)
     * @see org.mule.ibeans.api.client.params.InvocationContextInterface#getParsedCallUri()
     */
    public String getParsedCallUri() throws URISyntaxException
    {
        if (call == null)
        {
            return null;
        }
        String parsedUri = TemplateParser.createCurlyBracesStyleParser().parse(uriParams, call.uri());

        return parsedUri;
    }

    /* (non-Javadoc)
     * @see org.mule.ibeans.api.client.params.InvocationContextInterface#removeQueryParameters(java.lang.String)
     */
    public String removeQueryParameters(String... uriParamNames) throws URISyntaxException
    {
        String uriString = getParsedCallUri();
        if (uriString == null)
        {
            return null;
        }
        UriParamFilter filter = new UriParamFilter();
        for (int i = 0; i < uriParamNames.length; i++)
        {
            String uriParamName = uriParamNames[i];
            uriString = filter.filterParamsByValue(uriString, uriParamName);
        }
        return uriString;
    }

    /* (non-Javadoc)
     * @see org.mule.ibeans.api.client.params.InvocationContextInterface#isCallMethod()
     */
    public boolean isCallMethod()
    {
        return call != null;
    }

    /* (non-Javadoc)
     * @see org.mule.ibeans.api.client.params.InvocationContextInterface#isTemplateMethod()
     */
    public boolean isTemplateMethod()
    {
        return template != null;
    }

    /* (non-Javadoc)
     * @see org.mule.ibeans.api.client.params.InvocationContextInterface#getAttachments()
     */
    public List<DataSource> getAttachments()
    {
        return attachments;
    }

    /* (non-Javadoc)
     * @see org.mule.ibeans.api.client.params.InvocationContextInterface#getReturnType()
     */
    public Class getReturnType()
    {
        return returnType;
    }

    /* (non-Javadoc)
     * @see org.mule.ibeans.api.client.params.InvocationContextInterface#isStateCall()
     */
    public boolean isStateCall()
    {
        if (stateCall == null)
        {
            stateCall = method.getAnnotation(State.class) != null;
        }
        return stateCall;
    }

    /* (non-Javadoc)
     * @see org.mule.ibeans.api.client.params.InvocationContextInterface#getPayloadParams()
     */
    public Map<String, Object> getPayloadParams()
    {
        return payloadParams;
    }

    /* (non-Javadoc)
     * @see org.mule.ibeans.api.client.params.InvocationContextInterface#getPayloads()
     */
    public List<Object> getPayloads()
    {
        return payloads;
    }

    /* (non-Javadoc)
     * @see org.mule.ibeans.api.client.params.InvocationContextInterface#setPayloads(java.util.List)
     */
    public void setPayloads(List<Object> payloads)
    {
        this.payloads = payloads;
    }

    /* (non-Javadoc)
     * @see org.mule.ibeans.api.client.params.InvocationContextInterface#proceed()
     */
    public void proceed()
    {
        interceptorChain.proceed();
    }

    /* (non-Javadoc)
     * @see org.mule.ibeans.api.client.params.InvocationContextInterface#exceptionThrown()
     */
    public boolean exceptionThrown()
    {
        return exception != null;
    }

    /* (non-Javadoc)
     * @see org.mule.ibeans.api.client.params.InvocationContextInterface#throwException()
     */
    public void throwException() throws Throwable
    {
        throw exception;
    }

    class CallInterceptorChain
    {
        private List<CallInterceptor> interceptors;
        private int cursor;

        public CallInterceptorChain(List interceptorList)
        {
            interceptors = interceptorList;
        }

        void proceed()
        {
            if (interceptors.size() > cursor)
            {
                CallInterceptor interceptor = interceptors.get(cursor++);
                try
                {
                    interceptor.intercept(InternalInvocationContext.this);
                }
                catch (Throwable e)
                {
                    InternalInvocationContext.this.exception = e;
                }
            }
        }
    }

    /* (non-Javadoc)
     * @see org.mule.ibeans.api.client.params.InvocationContextInterface#getExceptionListener()
     */
    public ExceptionListener getExceptionListener()
    {
        return exceptionListener;
    }

    /* (non-Javadoc)
     * @see org.mule.ibeans.api.client.params.InvocationContextInterface#setExceptionListener(java.beans.ExceptionListener)
     */
    public void setExceptionListener(ExceptionListener exceptionListener)
    {
        this.exceptionListener = exceptionListener;
    }

    /* (non-Javadoc)
     * @see org.mule.ibeans.api.client.params.InvocationContextInterface#getRequestMuleMessage()
     */
    public MuleMessage getRequestMuleMessage()
    {
        return requestMuleMessage;
    }

    /* (non-Javadoc)
     * @see org.mule.ibeans.api.client.params.InvocationContextInterface#setRequestMuleMessage(org.mule.api.MuleMessage)
     */
    public void setRequestMuleMessage(MuleMessage requestMuleMessage)
    {
        this.requestMuleMessage = requestMuleMessage;
    }

    /* (non-Javadoc)
     * @see org.mule.ibeans.api.client.params.InvocationContextInterface#getResultMuleMessage()
     */
    public MuleMessage getResponseMuleMessage()
    {
        return responseMuleMessage;
    }

    /* (non-Javadoc)
     * @see org.mule.ibeans.api.client.params.InvocationContextInterface#setResultMuleMessage(org.mule.api.MuleMessage)
     */
    public void setResponseMuleMessage(MuleMessage resultMuleMessage)
    {
        this.responseMuleMessage = resultMuleMessage;
    }

    /* (non-Javadoc)
     * @see org.mule.ibeans.api.client.params.InvocationContextInterface#getResult()
     */
    public Object getResult()
    {
        return result;
    }

    /* (non-Javadoc)
     * @see org.mule.ibeans.api.client.params.InvocationContextInterface#setResult(java.lang.Object)
     */
    public void setResult(Object result)
    {
        this.result = result;
    }

    /* (non-Javadoc)
     * @see org.mule.ibeans.api.client.params.InvocationContextInterface#getArgs()
     */
    public Object[] getArgs()
    {
        return args;
    }

    public Object getProxy()
    {
        return proxy;
    }

    public void addRequestAttachment(DataSource attachment)
    {
        attachments.add(attachment);
    }

    public void addRequestHeaderParam(String name, Object value)
    {
        headerParams.put(name, value);
    }

    public List<DataSource> getRequestAttachments()
    {
        return attachments;
    }

    public Map<String, Object> getRequestHeaderParams()
    {
        return headerParams;
    }

    public Object getResponseHeaderParam(String name)
    {
        if (responseMuleMessage != null)
        {
            return responseMuleMessage.getProperty(name, PropertyScope.INBOUND);
        }
        else
        {
            return null;
        }
    }

    public Map<String, Object> getRequestPayloadParams()
    {
        return getPayloadParams();
    }

    public List<Object> getRequestPayloads()
    {
        return getPayloads();
    }

    public void setRequestPayloads(List<Object> payloads)
    {
        setPayloads(payloads);
    }

    public boolean getBooleanPropertyParam(String name, boolean defaultValue)
    {
        return ObjectUtils.getBoolean(getPropertyParams().get(name), defaultValue);
    }

    public String getStringPropertyParam(String name, String defaultValue)
    {
        return ObjectUtils.getString(getPropertyParams().get(name), defaultValue);
    }

    public void addRequestPayload(Object payload)
    {
        payloads.add(payload);
    }

}
