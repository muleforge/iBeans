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

import org.mule.ibeans.api.client.params.InvocationContext;
import org.mule.ibeans.api.client.params.ParamFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;

/**
 * TODO
 */
public class AwsIso8601DateFactory implements ParamFactory
{
    public String create(String paramName, boolean optional, InvocationContext invocationContext)
    {
        final SimpleDateFormat iso8601 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

        // + 1 day
        final String expires = iso8601.format(System.currentTimeMillis() + 1000 * 60 * 60 * 24);

        try
        {
            return URLEncoder.encode(expires, "UTF-8");
        }
        catch (UnsupportedEncodingException e)
        {
            throw new RuntimeException(e);
        }
    }
}
