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

import org.mule.api.MuleContext;
import org.mule.ibeans.IBeansContext;
import org.mule.ibeans.api.client.Call;
import org.mule.ibeans.api.client.Template;
import org.mule.ibeans.internal.util.UriParamFilter;
import org.mule.impl.endpoint.AnnotatedEndpointData;
import org.mule.util.PropertiesUtils;
import org.mule.util.TemplateParser;

import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import javax.activation.DataSource;


/**
 * Holds the current state of an iBean at the point a method invocation was made. This object is used by the {@link org.mule.ibeans.api.client.params.ParamFactory}
 * to pass the current state into the factory.
 */
public class InvocationContext
{
    private Map<String, Object> uriParams;
    private Map<String, Object> headerParams;
    private Map<String, Object> propertyParams;
    private Map<String, Object> payloadParams;
    private List<Object> payloads;
    List<DataSource> attachments;
    private Method method;
    private Call call;
    private Template template;
    private boolean stateCall;
    private Class returnType;
    private IBeansContext iBeansContext;


    public InvocationContext(Method method, MuleContext muleContext, Map<String, Object> uriParams, Map<String, Object> headerParams, Map<String, Object> propertyParams, Map<String, Object> payloadParams, List<Object> payloads, List<DataSource> attachments, Class returnType, boolean stateCall) throws Exception
    {
        this.uriParams = uriParams;
        this.headerParams = headerParams;
        this.propertyParams = propertyParams;
        this.payloadParams = payloadParams;
        this.payloads = payloads;
        this.method = method;
        this.returnType = returnType;
        this.attachments = attachments;
        this.stateCall = stateCall;
        call = method.getAnnotation(Call.class);
        template = method.getAnnotation(Template.class);
        iBeansContext = muleContext.getRegistry().lookupObject(IBeansContext.class);
        if (call == null)
        {
            //Template method
            return;
        }

        //Add non-variablized parameters to the uriParams
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
        //finally, add the endpoint properties to the propertyParams
        if (call.properties().length > 0)
        {
            this.propertyParams.putAll(AnnotatedEndpointData.convert(call.properties()));
        }

    }

    public IBeansContext getIBeansContext()
    {
        return iBeansContext;
    }

    /**
     * Reterns a map of all URI params including fixed params on the Call URI and any variablized params.
     * The map is sort alphabetically (ascending order)
     *
     * @return A Map of all URI params
     */
    public Map<String, Object> getUriParams()
    {
        return uriParams;
    }

    /**
     * Returns a map of all Header params set on the iBean so far.
     * The map is sort alphabetically (ascending order)
     *
     * @return a Map of all Header params
     */
    public Map<String, Object> getHeaderParams()
    {
        return headerParams;
    }

    /**
     * Returns a map of all property params.
     *
     * @return A map of all property params.
     */
    public Map<String, Object> getPropertyParams()
    {
        return propertyParams;
    }

    /**
     * Looks up a param in all of the param collections. The search order is uriParams, headerParams, and then propertyParams.
     *
     * @param key The name of the parameter to find
     * @return The value of the parameter with the specified key or null if the parameter is not set
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

    /**
     * The method called
     *
     * @return The method called
     */
    public Method getMethod()
    {
        return method;
    }

    /**
     * A utility method for converting all parameter keys to lower case. Note this returns an alphabetically sorted map (ascending).
     *
     * @param params The map of params to convert
     * @return The newly converted parameter name. Note that this is a different instance from the map passed in.
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

    /**
     * A utility method to convert all parameter keys to upper case. Note that this returns an alphabetically sorted map (ascending).
     *
     * @param params The map of params to convert
     * @return The newly converted parameter name. Note that this is a different instance from the map passed in.
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

    /**
     * Will create a parsed URI for the {@link org.mule.ibeans.api.client.Call} URI. If the invocationContent is not
     * for a Call method, null will be returned.
     * <p/>
     * Note that ordering is very imporant. If UriParam values have not been evaluated, the UriParam value will retain its
     * template value.
     *
     * @return A URI with UriParam values parsed or null if this is not a {@link org.mule.ibeans.api.client.Call} method.
     * @throws URISyntaxException
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

    /**
     * A helper method that will strip query prameters from a URI. The URI used will be the result of calling {@link #getParsedCallUri()}.
     * If this is not a context for a Call method, null will be returned.
     *
     * @param uriParamNames An array of one or more query params to remove from the Call URI for this context.
     * @return A new URI with the query params filtered out or null if this context is not for a Call method.
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

    public boolean isCallMethod()
    {
        return call != null;
    }

    public boolean isTemplateMethod()
    {
        return template != null;
    }

    public List<DataSource> getAttachments()
    {
        return attachments;
    }

    public Class getReturnType()
    {
        return returnType;
    }

    public boolean isStateCall()
    {
        return stateCall;
    }

    public Map<String, Object> getPayloadParams()
    {
        return payloadParams;
    }

    public List<Object> getPayloads()
    {
        return payloads;
    }
}
