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

import org.ibeans.annotation.State;
import org.ibeans.annotation.param.HeaderParam;
import org.ibeans.annotation.param.Optional;
import org.ibeans.annotation.param.PropertyParam;
import org.ibeans.api.ClientAuthentication;

public interface OAuthAuthentication extends ClientAuthentication
{
    @HeaderParam("Authorization")
    OAuthAuthorizationHeaderParamFactory OAUTH_AUTHORIZATION_HEADER_FACTORY = new OAuthAuthorizationHeaderParamFactory();

    @State
    public void initOAuth(@PropertyParam("oauth.consumer.key") String consumerKey, @PropertyParam("oauth.consumer.secret") String consumerSecret);

    @State
    public void setAccessToken(@Optional @PropertyParam("oauth.access.token") String accessToken, @Optional @PropertyParam("oauth.access.secret") String accessSecret);
}
