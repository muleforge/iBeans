/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.api.client.params;

import org.mule.api.transformer.DataType;
import org.mule.ibeans.IBeansContext;

import java.beans.ExceptionListener;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.activation.DataSource;


/**
 * Holds the current state of an iBean at the point a method invocation was made. This object is used by the {@link org.mule.ibeans.api.client.params.ParamFactory}
 * to pass the current state into the factory.
 */
public interface InvocationContext
{

    IBeansContext getIBeansContext();


    // iBeans Request Context

    /**
     * Returns a map of all URI params including fixed params on the Call URI and any variablized params.
     * The map is sorted alphabetically (ascending order)
     *
     * @return A Map of all URI params
     */
    Map<String, Object> getUriParams();

    /**
     * Returns a map of all URI params defined in the current Call URI, this ignores any global parame, though these are
     * available using {@link #getUriParams()}.
     * The map is sorted alphabetically (ascending order)
     *
     * @return A Map of URI params specifically on the current Call URI
     */
    Map<String, String> getCallSpecificUriParams();

    /**
     * There are often scanarios where a {@link org.mule.ibeans.api.client.Template} method is used to generate
     * a URL. In the case where there is processing on the parameters, this method can be used to get only those that
     * are set on the Template URI
     *
     * @return a map of Uri params set on a template URL
     */
    Map<String, String> getTemplateSpecificUriParams();

    /**
     * Returns a map of all requestHeader params set on the iBean so far. The map is sorted
     * alphabetically (ascending order)
     *
     * @return a Map of all Header params
     * @deprecated
     */
    Map<String, Object> getHeaderParams();

    /**
     * Returns a map of all request Header params set on the iBean so far. The map is sorted
     * alphabetically (ascending order)
     *
     * @return a Map of all request Header params
     */
    Map<String, Object> getRequestHeaderParams();

    /**
     * Adds a Header parameter.
     */
    void addRequestHeaderParam(String name, Object value);

    /**
     * @return list of attachments that will be sent with the request
     */
    Set<DataSource> getRequestAttachments();

    /**
     * @param attachment The attachment that will be be added
     */
    void addRequestAttachment(DataSource attachment);

    void setRequestPayloads(List<Object> payloads);

    void addRequestPayload(Object payload);

    Map<String, Object> getRequestPayloadParams();

    List<Object> getRequestPayloads();

    // iBeans Response Context

    Object getResponseHeaderParam(String name);

    Object getResult();

    void setResult(Object result);

    /**
     * The return type as . This is read-only in the invocation context because it is
     * determined by the return type of he annotated method
     *
     * @return the return type
     */
    DataType getReturnType();


    // ibeans Request/Response Context

    /**
     * Returns a map of all property params.
     *
     * @return A map of all property params.
     */
    Map<String, Object> getPropertyParams();

    String getStringPropertyParam(String name, String defaultValue);

    boolean getBooleanPropertyParam(String name, boolean defaultValue);

    /**
     * Looks up a param in all of the param collections. The search order is
     * uriParams, headerParams, and then propertyParams.
     *
     * @param key The name of the parameter to find
     * @return The value of the parameter with the specified key or null if the
     *         parameter is not set
     */
    Object getParam(String key);


    // iBeans util methods

    /**
     * The method called
     *
     * @param params The map of params to convert
     * @return The newly converted parameter name. Note that this is a different
     *         instance from the map passed in.
     */
    Map<String, Object> keysToLowerCase(Map<String, Object> params);

    /**
     * A utility method to convert all parameter keys to upper case. Note that this
     * returns an alphabetically sorted map (ascending).
     *
     * @param params The map of params to convert
     * @return A URI with UriParam values parsed or null if this is not a
     *         {@link org.mule.ibeans.api.client.Call} method.
     * @throws URISyntaxException
     */
    String getParsedCallUri() throws URISyntaxException;

    /**
     * A helper method that will strip query prameters from a URI. The URI used will
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

    boolean exceptionThrown();

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

}
