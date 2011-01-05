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

import java.io.File;
import java.io.InputStream;
import java.net.URL;

import javax.activation.DataSource;

import org.ibeans.impl.support.util.Utils;

/**
 * Factory for creating a named data source out of a given object.
 *
 */
public class DataSourceFactory
{
    public static DataSource create(String name, Object source)
    {
        if (Utils.isEmpty(name))
        {
            name = "attachment" + source.hashCode();
        }

        if (source instanceof DataSource)
        {
            return (DataSource) source;
        }

        else if (source instanceof File)
        {
            return new NamedFileDataSource((File) source, name);
        }
        else if (source instanceof URL)
        {
            return new NamedURLDataSource((URL) source, name);
        }

        else if (source instanceof InputStream)
        {
            return new InputStreamDataSource((InputStream) source, name);
        }
        else
        {
            return new StringDataSource(name, source.toString());
        }
    }
}
