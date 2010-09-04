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

import java.util.Set;

import javax.activation.MimeTypeParseException;

import org.ibeans.api.channel.MimeType;

/**
 * Defines a Set collection type with item type information
 *
 * @since 1.0
 */
public class SetDataType<T> extends CollectionDataType<T>
{
    public SetDataType()
    {
        super(Set.class);
    }

    public SetDataType(Class type, String mimeType) throws MimeTypeParseException
    {
        this(type, new MimeType(mimeType));
    }

    public SetDataType(Class type, MimeType mimeType)
    {
        super(Set.class, type, mimeType);
    }

    public SetDataType(Class type)
    {
        super(Set.class, type);
    }
}