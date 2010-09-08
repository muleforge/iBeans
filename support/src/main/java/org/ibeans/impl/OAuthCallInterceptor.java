/*
 * $Id$
 * -------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.ibeans.impl;

import java.beans.ExceptionListener;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ibeans.api.CallInterceptor;
import org.ibeans.api.InvocationContext;
import org.ibeans.impl.auth.OAuthAuthentication;
import org.scribe.oauth.Scribe;
import org.scribe.oauth.Token;

/**
 * TODO
 */
final class OAuthCallInterceptor implements CallInterceptor
{
    public static final String OAUTH_REQUEST_TOKEN = "oauth.request.token";

    private Map<String, Object> store = new HashMap<String, Object>();

    public OAuthCallInterceptor(Map<String, Object> store)
    {
        this.store = store;
    }

    public void intercept(InvocationContext invocationContext)
    {
        /**
         * logger used by this class
         */
        final Log logger = LogFactory.getLog(OAuthCallInterceptor.class);

        if (invocationContext.getMethod().getDeclaringClass().equals(OAuthAuthentication.class))
        {
            if (invocationContext.getMethod().getName().equals("getOAuthAuthenticateURL"))
            {
                Properties props = new Properties();
                props.putAll(invocationContext.getIBeanConfig().getPropertyParams());
                Scribe scribe = new Scribe(props);

                final Token requestToken = scribe.getRequestToken();
                invocationContext.getIBeanDefaultConfig().getPropertyParams().put(OAUTH_REQUEST_TOKEN, requestToken);

                final String authURL = (String) invocationContext.getIBeanConfig().getPropertyParams().get("oauth.request.token.url");

                URL url = null;
                try
                {
                    url = new URL(authURL.replace(OAUTH_REQUEST_TOKEN, requestToken.getToken()));
                }
                catch (MalformedURLException e)
                {
                    //not going to happen
                }

                System.out.println("OAuth URL for ibean is: " + url);
                invocationContext.setResult(url);
            }
            if (invocationContext.getMethod().getName().equals("verify"))
            {
                Properties props = new Properties();
                props.putAll(invocationContext.getIBeanConfig().getPropertyParams());
                Scribe scribe = new Scribe(props);

                final Token requestToken = (Token) invocationContext.getIBeanDefaultConfig().getPropertyParams().get(OAUTH_REQUEST_TOKEN);

                final String pin = (String) invocationContext.getIBeanConfig().getPropertyParams().get("pin");
                final String user = (String) invocationContext.getIBeanConfig().getPropertyParams().get("userKey");
                final Token accessToken = scribe.getAccessToken(requestToken, pin);
                logger.info("Access token received from LinkedIn: " + accessToken);

                //TODO
                store.put(user, accessToken);
//                try
//                {
//                    final String userName = request.getUserPrincipal().getName();
//                    final MuleMessage responseMessage = muleClient.send("vm://oauth.tokens.service", new Object[]{userName,
//                            "api.linkedin.com", accessToken}, null);
//                    log("Access token stored in Mule with response code: " + responseMessage.getPayload() + " for user: " + userName);
//                }
//                catch (final MuleException me)
//                {
//                    throw new ServletException("Can not store OAuth tokens in Mule", me);
//                }
            }
        }
        else
        {
            invocationContext.proceed();
        }
    }
}