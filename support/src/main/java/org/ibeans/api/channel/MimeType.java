/*
 * $Id:  $
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
 * TODO
 */
public class MimeType extends javax.activation.MimeType
{
    public MimeType()
    {
    }

    public MimeType(String s) throws MimeTypeParseException
    {
        super(s);
    }

    public MimeType(String s, String s1) throws MimeTypeParseException
    {
        super(s, s1);
    }


    public boolean isCompatible(MimeType that)
    {
        return (this.getPrimaryType().equals(that.getPrimaryType()) && this.getSubType().equals(that.getSubType()));
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }

        return match((MimeType)o);
    }

    @Override
    public int hashCode()
    {
        return toString().hashCode();
    }
}
