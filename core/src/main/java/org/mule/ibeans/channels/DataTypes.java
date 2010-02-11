/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.channels;

import org.mule.api.transformer.DataType;
import org.mule.transformer.types.SimpleDataType;

import java.io.InputStream;

/**
 * Common data types used in iBeans
 */
public interface DataTypes
{
    static final DataType<String> JSON_STRING = new SimpleDataType<String>(String.class, MimeTypes.JSON);
    static final DataType<InputStream> JSON_STREAM = new SimpleDataType<InputStream>(InputStream.class, MimeTypes.JSON);

    static final DataType<String> XML_STRING = new SimpleDataType<String>(String.class, MimeTypes.XML);
    static final DataType<InputStream> XML_STREAM = new SimpleDataType<InputStream>(InputStream.class, MimeTypes.XML);

    static final DataType<String> APPLICATION_XML_STRING = new SimpleDataType<String>(String.class, MimeTypes.APPLICATION_XML);
    static final DataType<InputStream> APPLICATION_XML_STREAM = new SimpleDataType<InputStream>(InputStream.class, MimeTypes.APPLICATION_XML);

    static final DataType<String> HTML_STRING = new SimpleDataType<String>(String.class, MimeTypes.HTML);
    static final DataType<InputStream> HTML_STREAM = new SimpleDataType<InputStream>(InputStream.class, MimeTypes.HTML);

    static final DataType<String> TEXT_STRING = new SimpleDataType<String>(String.class, MimeTypes.TEXT);
    static final DataType<InputStream> TEXT_STREAM = new SimpleDataType<InputStream>(InputStream.class, MimeTypes.TEXT);

    static final DataType<String> ATOM_STRING = new SimpleDataType<String>(String.class, MimeTypes.ATOM);
    static final DataType<InputStream> ATOM_STREAM = new SimpleDataType<InputStream>(InputStream.class, MimeTypes.ATOM);

    static final DataType<String> RSS_STRING = new SimpleDataType<String>(String.class, MimeTypes.RSS);
    static final DataType<InputStream> RSS_STREAM = new SimpleDataType<InputStream>(InputStream.class, MimeTypes.RSS);

}
