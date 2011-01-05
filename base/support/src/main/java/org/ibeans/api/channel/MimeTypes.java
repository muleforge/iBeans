/*
 * $Id$
 * -------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.ibeans.api.channel;

import javax.activation.MimeTypeParseException;

/**
 * Constants for Mime types used in iBeans
 */
public final class MimeTypes
{
    public static final MimeType ANY = create("*/*");

    public static final MimeType JSON = create("application/json");
    public static final MimeType ATOM = create("application/atom+xml");
    public static final MimeType RSS = create("application/rss+xml");
    public static final MimeType XML = create("text/xml");
    public static final MimeType APPLICATION_XML = create("application/xml");
    public static final MimeType TEXT = create("text/plain");
    public static final MimeType HTML = create("text/html");
    public static final MimeType BINARY = create("application/octet-stream");

    private MimeTypes()
    {
    }

    static MimeType create(String mime)
    {
        try
        {
            return new MimeType(mime);
        }
        catch (MimeTypeParseException e)
        {
            //This will never happen
            throw new RuntimeException(e);
        }
    }

}
