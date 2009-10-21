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

import org.mule.ibeans.api.client.params.InvocationContext;
import org.mule.ibeans.api.client.params.ParamFactory;
import org.mule.util.StringUtils;

/**
 * TODO
 */
public class ReversePropertyParamFactory implements ParamFactory
{
    private String propertyName;

    public ReversePropertyParamFactory(String propertyName)
    {
        this.propertyName = propertyName;
    }

    public String create(String paramName, boolean optional, InvocationContext invocationContext)
    {
        String prop = (String) invocationContext.getPropertyParams().get(propertyName);
        if (prop == null && !optional)
        {
            throw new IllegalArgumentException("PropertyParam value was null for: " + propertyName);
        }
        return StringUtils.reverse(prop);
    }
}