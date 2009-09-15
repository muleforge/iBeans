/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.aws;

import org.mule.ibeans.api.client.Call;
import org.mule.ibeans.api.client.params.InvocationContext;
import org.mule.ibeans.api.client.params.ParamFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.util.ParameterParser;
import sun.misc.BASE64Encoder;

/**
 * Implementing http://docs.amazonwebservices.com/AWSEC2/2009-04-04/DeveloperGuide/index.html?using-query-api.html#query-authentication
 */
public class AwsSignatureFactory implements ParamFactory
{
    private static final String PARAM_SIGNATURE = "Signature";

    public String create(String paramName, boolean optional, InvocationContext invocationContext)
    {
        byte[] awsSecretKey = (byte[]) invocationContext.getPropertyParams().get("aws_secret_key");
        String sig;

        final Call call = invocationContext.getMethod().getAnnotation(Call.class);
        if (call == null)
        {
            throw new IllegalArgumentException("@Call annotation is missing from the method " + invocationContext.getMethod());
        }

        final String fullUri = call.uri();
        String uri = fullUri.substring(fullUri.indexOf('?') + 1);

        // reparse the query string, we'll need to omit this 'signature' param
        final List<NameValuePair> queryParams = new ParameterParser().parse(uri, '&');

        // filter and sort the queryParams
        final SortedMap<String, String> filteredParams = new TreeMap<String, String>();

        for (NameValuePair param : queryParams)
        {
            filteredParams.put(param.getName(), param.getValue());
        }

        filteredParams.remove(PARAM_SIGNATURE);

        final String urlToHash = createAwsUrl(fullUri, filteredParams, invocationContext.getUriParams());

        try
        {
            // hash it all!
            Mac mac = Mac.getInstance(AwsIBean.DEFAULT_SIGNATURE_METHOD);
            mac.init(new SecretKeySpec(awsSecretKey, mac.getAlgorithm()));
            mac.update(urlToHash.getBytes());
            byte[] result = mac.doFinal();

            sig = URLEncoder.encode(new BASE64Encoder().encode(result), "UTF-8");
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }

        System.out.println("urlToHash = " + urlToHash + "&" + "Signature=" + sig);

        return sig;
    }

    protected String createAwsUrl(String fullUri, SortedMap<String, String> queryParams, Map<String, Object> parsedParams)
    {
        StringBuilder sb;
        try
        {
            // TODO string concat, yeah, I know...
            sb = new StringBuilder()
                    .append("GET\n")
                    .append(new URL(fullUri).getHost()).append("\n")
                    .append("/\n");
            boolean firstParam = true;
            for (Map.Entry<String, String> entry : queryParams.entrySet())
            {
                if (!firstParam)
                {
                    sb.append("&");
                }

                final String key = entry.getKey();
                sb.append(key).append("=");
                // if it is not a static value, merge the parsed params in
                String value = entry.getValue();
                if (value.startsWith("{") && value.endsWith("}"))
                {
                    // time to merge
                    value = "" + parsedParams.get(key);
                }
                sb.append(value);

                firstParam = false;
            }
        }
        catch (MalformedURLException e)
        {
            throw new RuntimeException(e);
        }

        return sb.toString();
    }
}
