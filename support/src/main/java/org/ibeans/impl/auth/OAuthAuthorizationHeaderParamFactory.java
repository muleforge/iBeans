/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.ibeans.impl.auth;

import java.net.URISyntaxException;
import java.util.Properties;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.httpclient.HttpConstants;
import org.ibeans.api.InvocationContext;
import org.ibeans.api.ParamFactory;
import org.ibeans.api.channel.HTTP;
import org.scribe.http.Request;
import org.scribe.oauth.Scribe;
import org.scribe.oauth.Token;

/**
 * The param factory used to create the HTTP Basic Authorization header.
 *
 * @see org.ibeans.impl.auth.OAuthAuthentication
 */
public class OAuthAuthorizationHeaderParamFactory implements ParamFactory
{
    public String create(String paramName, boolean optional, InvocationContext invocationContext) throws URISyntaxException
    {
        Properties p = new Properties();
        p.putAll(invocationContext.getIBeanConfig().getPropertyParams());
        Scribe scribe = new Scribe(p);
         // retrieve an access token for the Mule's request
        String requestUrl = invocationContext.getParsedCallUri();

        final String tokenString = (String)invocationContext.getIBeanConfig().getPropertyParams().get("oauth.access.token");

        if (tokenString == null) {
            throw new RuntimeException("No access token found for key: ");
        }
        final String secretString = (String)invocationContext.getIBeanConfig().getPropertyParams().get("oauth.secret.key");

        if (secretString == null) {
            throw new RuntimeException("No secret key found for key: oauth.secret.key");
        }
        final Token accessToken = new Token(tokenString, secretString);


        String httpMethod = (String)invocationContext.getIBeanConfig().getPropertyParams().get(HTTP.METHOD_KEY);
        if(httpMethod==null) httpMethod = "GET";

        // we leverage Scribe request signing but rely on Mule for the actual dispatch
        final Request.Verb verb = Request.Verb.valueOf(httpMethod.toUpperCase());
        final Request request = new Request(verb, requestUrl);
        scribe.signRequest(request, accessToken);
        final String authHeader = request.getHeaders().get(paramName);
        return authHeader;
    }
}
