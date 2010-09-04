/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans;

import org.mule.transport.http.HttpConnector;

import org.ibeans.api.InvocationContext;
import org.ibeans.api.ParamFactory;

public class CheckHTTPPropertiesFactory implements ParamFactory
{
    public String create(String paramName, boolean optional, InvocationContext invocationContext)
    {
        String method = (String) invocationContext.getIBeanConfig().getPropertyParams().get(HttpConnector.HTTP_METHOD_PROPERTY);
        if (method == null)
        {
            throw new IllegalArgumentException("HTTP Method not set");
        }
        return method;
    }
}
