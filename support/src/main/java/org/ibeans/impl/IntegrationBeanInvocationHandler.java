/*
 * $Id: IntegrationBeanInvocationHandler.java 333 2010-04-01 22:58:47Z ross $
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.ibeans.impl;

import java.beans.ExceptionListener;
import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ibeans.annotation.Interceptor;
import org.ibeans.api.CallInterceptor;
import org.ibeans.api.IBeanStateData;
import org.ibeans.api.IBeansException;
import org.ibeans.api.IBeansProperties;
import org.ibeans.impl.support.util.TemplateParser;
import org.ibeans.spi.IBeansPlugin;

/**
 * The proxy handler responsible for making calls on behalf of the the IntegrationBean.  This handler maintains any state
 * and parses any parameter annotations before making the call.
 */
public class IntegrationBeanInvocationHandler implements InvocationHandler, Serializable
{
    protected static transient Log logger = LogFactory.getLog(IntegrationBeanInvocationHandler.class);

    protected transient ExceptionListener exceptionListener;

    protected transient IBeanReader helper;

    protected transient TemplateParser parser = TemplateParser.createCurlyBracesStyleParser();

    protected transient LinkedList<CallInterceptor> defaultInterceptorList = new LinkedList<CallInterceptor>();

    protected transient CallInterceptor invoker;

    protected transient Map<Method, List<CallInterceptor>> interceptorListCache = new HashMap<Method, List<CallInterceptor>>();

    protected IBeanStateData iBeanStateData;

    protected IBeansPlugin plugin;

    public IntegrationBeanInvocationHandler(Class ibean, IBeansPlugin plugin) throws IBeansException
    {

        if (ibean == null)
        {
            throw new IBeansException("IBean Interface is null");
        }
        this.plugin = plugin;
        helper = new IBeanReader(plugin);
        iBeanStateData = helper.readStateData(ibean);

        //Interceptor Chain
        // Performs special handling for standard and non-integration methods
        defaultInterceptorList.add(new NonIntegrationMethodsCallInterceptor());
        // Populates invocationContext with field and method level params
        defaultInterceptorList.add(new PopulateiBeansParamsInterceptor(helper));

        defaultInterceptorList.add(new StateCallInterceptor());

        //Response chain
        //Responsible for converting the response from the call
        defaultInterceptorList.add(plugin.getResponseTransformInterceptor());
        //Responsible for processing an error response
        defaultInterceptorList.add(new ProcessErrorsInterceptor());

        String logDirectory = System.getProperty(IBeansProperties.LOG_RESPONSES_DIR);
        if (logDirectory != null)
        {
            defaultInterceptorList.add(new LogResponsesInterceptor(logDirectory));
        }
        //Allow plugins to insert there own response processor
        plugin.addInterceptors(defaultInterceptorList);

        //This mus be last
        defaultInterceptorList.add(plugin.getIBeanInvoker());
    }
    
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
    {
        if (interceptorListCache.get(method) == null)
        {
            if (method.getAnnotation(Interceptor.class) != null)
            {
                Interceptor interceptorAnnotation = method.getAnnotation(Interceptor.class);
                List interceptors = new ArrayList<CallInterceptor>();
                interceptors.addAll(defaultInterceptorList);
                interceptors.add(interceptors.size() - 3, interceptorAnnotation.value().newInstance());
                interceptorListCache.put(method, interceptors);
            }
            else
            {
                interceptorListCache.put(method, defaultInterceptorList);
            }
        }

        InternalInvocationContext invocationContext = new InternalInvocationContext(iBeanStateData, proxy, method, args,
               exceptionListener, interceptorListCache.get(method), plugin);

        invocationContext.proceed();
        
        //Copy the exception Listener if one was set, it will be applied to all further invocations
        exceptionListener = invocationContext.getExceptionListener();

        if (invocationContext.isExceptionThrown())
        {
            invocationContext.rethrowException();
        }

        if (invocationContext.getResult()==null)
        {
            return null;
        }
        return invocationContext.getResult();
    }


}
