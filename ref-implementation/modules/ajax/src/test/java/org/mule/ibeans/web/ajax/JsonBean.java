/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.web.ajax;

import org.codehaus.jackson.annotate.JsonAutoDetect;

/**
 * TODO
 */
@JsonAutoDetect
public class JsonBean
{
    private String name;

    public JsonBean()
    {
    }

    public JsonBean(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    @Override
    public String toString()
    {
        return "JsonBean{" +
                "name='" + name + '\'' +
                '}';
    }
}
