/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.ibeans.api;

import java.beans.ExceptionListener;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.util.Map;

import org.ibeans.spi.ExpressionParser;


/**
 * Holds the current state of an iBean at the point a method invocation was made. This object is used by the {@link org.mule.ibeans.api.client.params.ParamFactory}
 * to pass the current state into the factory.
 */
public interface InvocationContext
{
    Request getRequest();

    Response getResponse();

    IBeanInvocationData getIBeanConfig();

    IBeanStateData getIBeanDefaultConfig();

    ExpressionParser getExpressionParser();
    
    /**
     * Returns a map of all URI params defined in the current Call URI, this ignores any global parame, though these are
     * available using {@link #getUriParams()}.
     * The map is sorted alphabetically (ascending order)
     *
     * @return A Map of URI params specifically on the current Call URI
     */
    Map<String, String> getCallSpecificUriParams();

    /**
     * There are often scenarios where a {@link org.mule.ibeans.api.client.Template} method is used to generate
     * a URL. In the case where there is processing on the parameters, this method can be used to get only those that
     * are set on the Template URI
     *
     * @return a map of Uri params set on a template URL
     */
    Map<String, String> getTemplateSpecificUriParams();


    // iBeans Response Context

   // Object getResponseHeaderParam(String name);

    Object getResult();

    void setResult(Object result);

    // iBeans util methods

    /**
     * A utility method to convert all parameter keys to lower case. Note that this method must
     * return an alphabetically sorted map (ascending).
     *
     * @param params The map of params to convert
     * @return The newly converted parameter name. Note that this is a different
     *         instance from the map passed in.
     */
    Map<String, Object> keysToLowerCase(Map<String, Object> params);

    /**
     * A utility method to convert all parameter keys to upper case. Note that this method must
     * return an alphabetically sorted map (ascending).
     *
     * @param params The map of params to convert
     * @return A URI with UriParam values parsed or null if this is not a
     *         {@link org.mule.ibeans.api.client.Call} method.
     */
    Map<String, Object> keysToUpperCase(Map<String, Object> params);


    String getParsedCallUri() throws URISyntaxException;

    /**
     * A utility method that will strip query parameters from a URI. The URI used will
     * be the result of calling {@link #getParsedCallUri()}. If this is not a context
     * for a Call method, null will be returned.
     *
     * @param uriParamNames An array of one or more query params to remove from the
     *                      Call URI for this context.
     * @return A new URI with the query params filtered out or null if this context
     *         is not for a Call method.
     */
    String removeQueryParameters(String... uriParamNames) throws URISyntaxException;


    // Exception Handling

    ExceptionListener getExceptionListener();

    void setExceptionListener(ExceptionListener exceptionListener);

    boolean isExceptionThrown();

    void rethrowException() throws Throwable;


    // Interceptors

    void proceed();


    // Proxy details

    Method getMethod();

    Object[] getArgs();

    Object getProxy();


    // Other

    boolean isStateCall();

    boolean isCallMethod();

    boolean isTemplateMethod();


    public DataType getInvocationReturnType();

    public void setInvocationReturnType(DataType invocationReturnType);

    public DataType getReturnType();

    public Class[] getParamTypes();

    public void setParamTypes(Class[] paramTypes);
}
