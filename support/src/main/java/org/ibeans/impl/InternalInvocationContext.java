/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.ibeans.impl;

import java.beans.ExceptionListener;
import java.lang.reflect.Method;
import java.lang.reflect.TypeVariable;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.activation.DataSource;

import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.util.ParameterParser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ibeans.annotation.Call;
import org.ibeans.annotation.State;
import org.ibeans.annotation.Template;
import org.ibeans.api.CallException;
import org.ibeans.api.CallInterceptor;
import org.ibeans.api.DataType;
import org.ibeans.api.IBeanInvocationData;
import org.ibeans.api.IBeanStateData;
import org.ibeans.api.InvocationContext;
import org.ibeans.api.ParamFactoryHolder;
import org.ibeans.api.Request;
import org.ibeans.api.Response;
import org.ibeans.impl.support.datatype.DataTypeFactory;
import org.ibeans.impl.support.ds.DataSourceFactory;
import org.ibeans.impl.support.util.UriParamFilter;
import org.ibeans.impl.support.util.Utils;
import org.ibeans.spi.ExpressionParser;
import org.ibeans.spi.IBeansPlugin;

/**
 * Holds the current state of an iBean at the point a method invocation was made.
 * This object is used by the {@link org.ibeans.api.params.ParamFactory}
 * to pass the current state into the factory.
 */
public class InternalInvocationContext implements InvocationContext
{
    /**
     * logger used by this class
     */
    protected transient final Log logger = LogFactory.getLog(InternalInvocationContext.class);

    //All invocationData params for the iBean being invoked
    protected IBeanInvocationData invocationData;

    //All static
    protected IBeanStateData iBeanStateData;

    //Call specific information
    protected Method method;
    protected Call call;
    protected Template template;
    protected Boolean stateCall;
    protected Object proxy;
    protected Object[] args;

    //The interceptor chain that will be used for an invocation
    protected CallInterceptorChain interceptorChain;

    //The current request
    private Request request;

    //The response after the Call
    private Response response;

    //The final result to be returned to the caller
    private Object result;

    //The exception thrown while making a call if any
    private Throwable exception;

    //Exception listener for the iBean
    protected ExceptionListener exceptionListener;

    protected ExpressionParser expressionParser;

    protected IBeansPlugin plugin;

    public InternalInvocationContext(IBeanStateData stateData, Object proxy,
                                     Method method,
                                     Object[] args,
                                     ExceptionListener exceptionListener,
                                     List<CallInterceptor> interceptors,
                                     IBeansPlugin plugin) throws Exception
    {
        this.iBeanStateData = stateData;
        this.interceptorChain = new CallInterceptorChain(interceptors);
        this.method = method;
        this.args = args;
        this.proxy = proxy;
        this.exceptionListener = exceptionListener;
        this.plugin = plugin;
        this.expressionParser = plugin.getExpressionParser();

        call = method.getAnnotation(Call.class);
        template = method.getAnnotation(Template.class);
        invocationData = new DefaultIBeanConfig();

        if (call == null)
        {
            // Template method
            return;
        }
        // Add non-variable-ized parameters to the uriParams
        final String fullUri = call.uri();
        String uri = fullUri.substring(fullUri.indexOf('?') + 1);

        Properties queryParams = Utils.getPropertiesFromQueryString(uri);
        for (Iterator<Object> iterator = queryParams.keySet().iterator(); iterator.hasNext();)
        {
            String key = (String) iterator.next();
            if (!invocationData.getUriParams().containsKey(key))
            {
                invocationData.getUriParams().put(key, queryParams.getProperty(key));
            }

        }
        
        // Add the endpoint properties to the propertyParams
        if (call.properties().length > 0)
        {
            invocationData.getPropertyParams().putAll(Utils.convertKeyValuePairsToMap(call.properties()));
        }
        
        // Add any default @BodyParam
        if (call.bodyParamFilter().length > 0)
        {
        	for (String bodyParam : call.bodyParamFilter())
        	{
        		if (stateData.getPayloadParams().containsKey(bodyParam))
        		{
        			invocationData.addPayloadParam(bodyParam, stateData.getPayloadParams().get(bodyParam));
        		} 
        		else 
        		{
        			throw new IllegalArgumentException("Default BodyParam is not defined: " + bodyParam);
        		}
        	}
        }
        
        // Add any default @HeaderParam
        if (call.headerParamFilter().length > 0)
        {
        	for (String headerParam : call.headerParamFilter())
        	{
        		if (stateData.getHeaderParams().containsKey(headerParam))
        		{
        			invocationData.addHeaderParam(headerParam, stateData.getHeaderParams().get(headerParam));
        		}
        		else 
        		{
        			throw new IllegalArgumentException("Default HeaderParam is not defined: " + headerParam);
        		}
        	}
        	
        }

    }


    public IBeanStateData getIBeanDefaultConfig()
    {
        return iBeanStateData;
    }

    public Map<String, String> getCallSpecificUriParams()
    {
        if (isCallMethod())
        {
            Call call = getMethod().getAnnotation(Call.class);
            final String fullUri = call.uri();
            String uri = fullUri.substring(fullUri.indexOf('?') + 1);

            // reparse the query string, we'll need to omit this 'signature' param
            final List<NameValuePair> queryParams = new ParameterParser().parse(uri, '&');

            // filter and sort the queryParams
            final SortedMap<String, String> filteredParams = new TreeMap<String, String>();

            for (NameValuePair param : queryParams)
            {
                filteredParams.put(param.getName(), invocationData.getUriParams().get(param.getName()).toString());
            }
            return filteredParams;

        }
        return Collections.emptyMap();
    }

    public Map<String, String> getTemplateSpecificUriParams()
    {
        if (isTemplateMethod())
        {
            Template call = getMethod().getAnnotation(Template.class);
            final String fullUri = call.value();
            String uri = fullUri.substring(fullUri.indexOf('?') + 1);
            if (uri.length() == 0)
            {
                return Collections.emptyMap();
            }
            //TODO remove dep on Http client

            // reparse the query string, we'll need to omit this 'signature' param
            final List<NameValuePair> queryParams = new ParameterParser().parse(uri, '&');

            // filter and sort the queryParams
            final SortedMap<String, String> filteredParams = new TreeMap<String, String>();

            for (NameValuePair param : queryParams)
            {
                filteredParams.put(param.getName(), (String) invocationData.getUriParams().get(param.getName()));
            }
            return filteredParams;

        }
        return Collections.emptyMap();
    }


    /* (non-Javadoc)
     * @see org.mule.ibeans.api.InvocationContextInterface#getMethod()
     */

    public Method getMethod()
    {
        return method;
    }

    public IBeanInvocationData getIBeanConfig()
    {
        return invocationData;
    }

    /* (non-Javadoc)
     * @see org.mule.ibeans.api.InvocationContextInterface#keysToLowerCase(java.util.Map)
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
     * @see org.mule.ibeans.api.InvocationContextInterface#keysToUpperCase(java.util.Map)
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
    * @see org.mule.ibeans.api.InvocationContextInterface#getParsedCallUri()
    */

    public String getParsedCallUri() throws URISyntaxException
    {
        if (call == null)
        {
            return null;
        }

        //return getExpressionParser().parsePropertyPlaceholders(invocationData.getUriParams(), call.uri());
        return getExpressionParser().parseUriTokens(invocationData.getUriParams(), call.uri());
    }

    public ExpressionParser getExpressionParser()
    {
        return expressionParser;
    }


    /* (non-Javadoc)
    * @see org.mule.ibeans.api.InvocationContextInterface#removeQueryParameters(java.lang.String)
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
     * @see org.mule.ibeans.api.InvocationContextInterface#isCallMethod()
     */

    public boolean isCallMethod()
    {
        return call != null;
    }

    /* (non-Javadoc)
     * @see org.mule.ibeans.api.InvocationContextInterface#isTemplateMethod()
     */

    public boolean isTemplateMethod()
    {
        return template != null;
    }



    /* (non-Javadoc)
     * @see org.mule.ibeans.api.InvocationContextInterface#isStateCall()
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
     * @see org.mule.ibeans.api.InvocationContextInterface#proceed()
     */

    public void proceed()
    {
        interceptorChain.proceed();
    }

    /* (non-Javadoc)
     * @see org.mule.ibeans.api.InvocationContextInterface#exceptionThrown()
     */

    public boolean isExceptionThrown()
    {
        return exception != null;
    }

    /* (non-Javadoc)
     * @see org.mule.ibeans.api.InvocationContextInterface#throwException()
     */

    public void rethrowException() throws CallException
    {
        if(exception instanceof CallException)
        {
            throw (CallException)exception;
        }
        else
        {
            exception.printStackTrace();
            throw ProcessErrorsInterceptor.createCallException(this, exception);
        }
    }

    class CallInterceptorChain
    {
        private List<CallInterceptor> interceptors;
        private int cursor;

        public CallInterceptorChain(List<CallInterceptor> interceptorList)
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
                    Throwable root = e;
                    while(root.getCause()!=null)
                    {
                        root = root.getCause();
                    }
                    InternalInvocationContext.this.exception = root;
                }
            }
        }
    }

    /* (non-Javadoc)
     * @see org.mule.ibeans.api.InvocationContextInterface#getExceptionListener()
     */

    public ExceptionListener getExceptionListener()
    {
        return exceptionListener;
    }

    /* (non-Javadoc)
     * @see org.mule.ibeans.api.InvocationContextInterface#setExceptionListener(java.beans.ExceptionListener)
     */

    public void setExceptionListener(ExceptionListener exceptionListener)
    {
        this.exceptionListener = exceptionListener;
    }


    /* (non-Javadoc)
     * @see org.mule.ibeans.api.InvocationContextInterface#setRequestMuleMessage(org.mule.api.MuleMessage)
     */
    public void setRequest(Request request)
    {
        this.request = request;
    }

    /* (non-Javadoc)
     * @see org.mule.ibeans.api.InvocationContextInterface#getRequest()
     */
    public Request getRequest()
    {
        return request;
    }

    /* (non-Javadoc)
     * @see org.mule.ibeans.api.InvocationContextInterface#getResponse()
     */
    public Response getResponse()
    {
        return response;
    }


    /* (non-Javadoc)
    * @see org.mule.ibeans.api.InvocationContextInterface#setResultMuleMessage(org.mule.api.MuleMessage)
    */

    public void setResponse(Response response)
    {
        this.response = response;
    }

    /* (non-Javadoc)
     * @see org.mule.ibeans.api.InvocationContextInterface#getResult()
     */

    public Object getResult()
    {
        return result;
    }

    /* (non-Javadoc)
     * @see org.mule.ibeans.api.InvocationContextInterface#setResult(java.lang.Object)
     */

    public void setResult(Object result)
    {
        this.result = result;
    }

    /* (non-Javadoc)
     * @see org.mule.ibeans.api.InvocationContextInterface#getArgs()
     */

    public Object[] getArgs()
    {
        return args;
    }

    public Object getProxy()
    {
        return proxy;
    }


    public void createMessage() throws Exception
    {
        if (getIBeanConfig().getPayloads().size() == 0)
        {
            if (getIBeanConfig().getPayloadParams().size() == 0)
            {
                getIBeanConfig().getPayloads().add("");
            }
            else
            {
                getIBeanConfig().getPayloads().add(getIBeanConfig().getPayloadParams());
            }
        }

        createParameters(getIBeanDefaultConfig().getUriFactoryParams(), getIBeanConfig().getUriParams(), this);
        createParameters(getIBeanDefaultConfig().getHeaderFactoryParams(), getIBeanConfig().getHeaderParams(), this);
        createParameters(getIBeanDefaultConfig().getPropertyFactoryParams(), getIBeanConfig().getPropertyParams(), this);
        createAttachments(getIBeanDefaultConfig().getAttachmentFactoryParams(), getIBeanConfig().getAttachments(), this);

        this.request = plugin.createRequest(getIBeanConfig());
    }

    protected void createAttachments(Set<ParamFactoryHolder> factories, Set<DataSource> attachments, InvocationContext context) throws Exception
    {
        for (ParamFactoryHolder holder : factories)
        {
            Object param = holder.getParamFactory().create(holder.getParamName(), false, context);
            if (param != null)
            {
                //Array of attachments not supported, only single attachments can be created via a ParamFactory
                attachments.add(DataSourceFactory.create(holder.getParamName(), param));
            }
        }

    }

    protected void createParameters(Set<ParamFactoryHolder> factories, Map<String, Object> params, InvocationContext context) throws Exception
    {

        for (ParamFactoryHolder holder : factories)
        {
            Object param = holder.getParamFactory().create(holder.getParamName(), false, context);
            if (param != null)
            {
                params.put(holder.getParamName(), param);
            }
        }
    }

    public DataType getInvocationReturnType()
    {
        //TODO this was stored in a separate var, may cause issues
        return invocationData.getReturnType();
    }

    public void setInvocationReturnType(DataType invocationReturnType)
    {
        this.invocationData.setReturnType(invocationReturnType);
    }

    public DataType<?> getReturnType()
    {
        if(invocationData.getReturnType()!=null)
        {
            return invocationData.getReturnType();
        }
        else if (method.getGenericReturnType() instanceof TypeVariable && getIBeanDefaultConfig().getReturnType()!=null)
        {
            return getIBeanDefaultConfig().getReturnType();
        }
        else
        {
            return DataTypeFactory.createFromReturnType(getMethod());
        }
    }
}
