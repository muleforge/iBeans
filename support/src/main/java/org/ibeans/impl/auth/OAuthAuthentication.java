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

import java.net.URL;

import org.ibeans.annotation.State;
import org.ibeans.annotation.param.HeaderParam;
import org.ibeans.annotation.param.Optional;
import org.ibeans.annotation.param.PropertyParam;
import org.ibeans.api.ClientAuthentication;

/**
 * TODO
 *
 * Requires 'oauth.request.token.url' to be set as a {@link org.ibeans.annotation.param.PropertyParam}
 *
 * Sets {@link org.ibeans.annotation.param.PropertyParam} 'OAuthPin' 
 */
public interface OAuthAuthentication extends ClientAuthentication
{
    @HeaderParam("Authorization")
    OAuthAuthorizationHeaderParamFactory OAUTH_AUTHORIZATION_HEADER_FACTORY = new OAuthAuthorizationHeaderParamFactory();

    public URL getOAuthAuthenticateURL(@PropertyParam("consumerKey") String consumerKey, @PropertyParam("consumerSecret") String consumerSecret);

    //public void verify(@PropertyParam("pin") String pin, @PropertyParam("userKey") String userKey) throws Exception;

    @State
    public void initOAuth(@PropertyParam("oauth.access.token") String accessToken, @PropertyParam("oauth.secret.key") String secretKey);
}
