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
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.ibeans.api.InvocationContext;
import org.ibeans.api.ParamFactory;
import org.ibeans.api.channel.CHANNEL;
import org.ibeans.api.channel.HTTP;
import org.ibeans.impl.support.util.UriParamFilter;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.TwitterApi;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;


/**
 * The param factory used to create the OAuth Authorization header.
 *
 * @see org.ibeans.impl.auth.OAuthAuthentication
 */
public class OAuthAuthorizationHeaderParamFactory implements ParamFactory
{
    public String create(String paramName, boolean optional, InvocationContext invocationContext) throws URISyntaxException
    {
        Properties p = new Properties();
        p.putAll(invocationContext.getIBeanConfig().getPropertyParams());
        
        // retrieve an access token for the Mule's request
        UriParamFilter filter = new UriParamFilter();
        String requestUrl = filter.filterParamsByValue(invocationContext.getParsedCallUri(), CHANNEL.NULL_URI_PARAM);

        final String accessTokenString = (String)invocationContext.getIBeanConfig().getPropertyParams().get("oauth.access.token");
        final String accessSecretString = (String)invocationContext.getIBeanConfig().getPropertyParams().get("oauth.access.secret");

        // This was meant to be a non authenticated call. Don't use oAuth;
        if (accessTokenString == null && accessSecretString == null) 
        {
        	return null;
        }
        
        if (accessTokenString == null) 
        {
            throw new RuntimeException("No access token found for key: oauth.access.token");
        }

        if (accessSecretString == null) 
        {
            throw new RuntimeException("No secret key found for key: oauth.access.secret");
        }
        
        final String consumerKeyString = (String)invocationContext.getIBeanConfig().getPropertyParams().get("oauth.consumer.key");
        
        if (consumerKeyString == null) 
        {
        	throw new RuntimeException("No consumer key found for key: oauth.consumer.key");
        }
        
        final String consumerSecretString = (String)invocationContext.getIBeanConfig().getPropertyParams().get("oauth.consumer.secret");
        
        if (consumerSecretString == null) 
        {
        	throw new RuntimeException("No consumer secret key found for key: oauth.consumer.secret");
        }
        
        String httpMethod = (String)invocationContext.getIBeanConfig().getPropertyParams().get(HTTP.METHOD_KEY);
        
        // Since the signature is dependent upon the verb it must be set by the user.
        if (httpMethod == null ) {
        	throw new RuntimeException("When using oAuth the HTTP method must be defined per call");
        }
        
        // Using Scribe to create the authorization signature. The provider below is required 
        // but is only used if we were using Scribe to retrieve the request token.
        OAuthService service = new ServiceBuilder()
        							.provider(TwitterApi.class)
        							.apiKey(consumerKeyString)
        							.apiSecret(consumerSecretString)
        							.build();
        Token accessToken = new Token(accessTokenString, accessSecretString); 
        
        OAuthRequest request = new OAuthRequest(Verb.valueOf(httpMethod.toUpperCase()), requestUrl);
        
        // Before signing the request add all the body parameters
        Iterator<Map.Entry<String, Object>> iterator = invocationContext.getIBeanConfig().getPayloadParams().entrySet().iterator();
        while (iterator.hasNext()) {
        	Map.Entry<String, Object> entry = iterator.next();
        	request.addBodyParameter(entry.getKey().toString(), entry.getValue().toString());
        }
        
        service.signRequest(accessToken, request);
        
        final String authHeader = request.getHeaders().get(paramName);
        return authHeader;
    }
}
