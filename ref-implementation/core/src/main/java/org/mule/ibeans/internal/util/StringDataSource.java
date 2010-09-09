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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.activation.DataSource;

/**
 * TODO
 */
public class StringDataSource implements DataSource
{
    private String name;
    private String data;
    private String charset;
    private String contentType = "text/plain";

    public StringDataSource(String name, String data)
    {
        this.data = data;
        this.name = name;
    }

    public StringDataSource(String name, String data, String contentType)
    {
        this.name = name;
        this.data = data;
        this.contentType = contentType;
    }

    public StringDataSource(String name, String data, String contentType, String charset)
    {
        this.name = name;
        this.data = data;
        this.charset = charset;
        this.contentType = contentType;
    }

    public InputStream getInputStream() throws IOException
    {
        return new ByteArrayInputStream(data.getBytes());
    }

    public OutputStream getOutputStream() throws IOException
    {
        return null;
    }

    public String getContentType()
    {
        return contentType;
    }

    public String getName()
    {
        return name;
    }

    public String getData()
    {
        return data;
    }

    public String getCharset()
    {
        return charset;
    }
}
