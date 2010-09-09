/*
 * $Id$
 * -------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.ibeans.impl.support.ds;

import java.net.URL;

import javax.activation.URLDataSource;

/**
 * An URL data source that allows the name of the datasource to be different from the filename,
 * useful when using HTTP multipart/form-data
 */
public class NamedURLDataSource extends URLDataSource
{
    private String name;

    public NamedURLDataSource(URL url, String name)
    {
        super(url);
        setName(name);
    }

    protected void setName(String name)
    {
        if (name != null && name.length() > 0)
        {
            this.name = name;
        }
    }

    @Override
    public String getName()
    {
        return (name == null ? super.getName() : name);
    }


}