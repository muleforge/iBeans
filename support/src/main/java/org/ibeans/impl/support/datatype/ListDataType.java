/*
 * $Id:  $
 * -------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.ibeans.impl.support.datatype;

import java.util.List;

import javax.activation.MimeTypeParseException;

import org.ibeans.api.channel.MimeType;

/**
 * Defines a List collection type with item type information
 *
 * @since 3.0
 */
public class ListDataType<T> extends CollectionDataType<T>
{
    public ListDataType()
    {
        super(List.class);
    }

    public ListDataType(Class type, String mimeType) throws MimeTypeParseException
    {
        this(type, new MimeType(mimeType));
    }

    public ListDataType(Class type, MimeType mimeType)
    {
        super(List.class, type, mimeType);
    }

    public ListDataType(Class type)
    {
        super(List.class, type);
    }
}
