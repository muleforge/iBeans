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

import org.apache.commons.codec.binary.Base64;
import org.ibeans.api.InvocationContext;
import org.ibeans.api.ParamFactory;

/**
 * The param factory used to create the HTTP Basic Authorization header.
 *
 * @see org.mule.ibeans.api.client.authentication.HttpBasicAuthentication
 */
public class HttpBasicHeaderParamFactory implements ParamFactory
{
    public String create(String paramName, boolean optional, InvocationContext invocationContext)
    {
        StringBuffer header = new StringBuffer(128);
        String user = (String) invocationContext.getIBeanConfig().getPropertyParams().get("username");
        if (user == null)
        {
            return null;
        }
        String password = (String) invocationContext.getIBeanConfig().getPropertyParams().get("password");
        header.append("Basic ");
        String token = user + ":" + password;
        header.append(new String(Base64.encodeBase64(token.getBytes())));
        return header.toString();
    }
}
