/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.internal.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.activation.DataSource;

/**
 * A data source that wraps an input stream. Note that there is no associated {@link java.io.OutputStream}
 */
public final class InputStreamDataSource implements DataSource
{
    public static final String DEFAULT_TYPE = "application/octet-stream";

    private final InputStream in;
    private final String ctype;
    private String name;

    public InputStreamDataSource(InputStream in, String name)
    {
        this(in, name, null);
    }

    public InputStreamDataSource(InputStream in, String name, String ctype)
    {
        this.in = in;
        this.ctype = (ctype != null) ? ctype : DEFAULT_TYPE;
        this.name = name;
    }

    public String getContentType()
    {
        return ctype;
    }

    public String getName()
    {
        return name;
    }

    public InputStream getInputStream() throws IOException
    {
        return in;
    }

    public OutputStream getOutputStream() throws IOException
    {
        return null;
    }
}
