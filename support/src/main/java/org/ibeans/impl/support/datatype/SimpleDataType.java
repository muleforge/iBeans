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

import javax.activation.MimeTypeParseException;

import org.ibeans.api.DataType;
import org.ibeans.api.channel.MimeType;

/**
 * A data type that simply wraps a Java type.  This type also allows a mime type to be associated
 * with the Java type.
 *
 * @since 1.0
 */
public class SimpleDataType<T> implements DataType<T>, Cloneable
{
    protected Class<?> type;
    protected String mimeType = ANY_MIME_TYPE;
    protected String encoding;

    public SimpleDataType(Class<?> type, String mimeType) throws MimeTypeParseException
    {
        this(type, (mimeType==null ? null : new MimeType(mimeType)));
    }
    public SimpleDataType(Class<?> type, MimeType mt)
    {
        this.type = type;
        if (mimeType == null)
        {
            this.mimeType = ANY_MIME_TYPE;
        }
        else
        {
                this.mimeType = mt.getPrimaryType() + "/" + mt.getSubType();
                if (mt.getParameter("charset") != null)
                {
                    encoding = mt.getParameter("charset");
                }
        }
    }

    public SimpleDataType(Class type)
    {
        this.type = type;
    }

    public Class getType()
    {
        return type;
    }

    public String getMimeType()
    {
        return mimeType;
    }

    public void setMimeType(String mimeType)
    {
        this.mimeType = (mimeType == null ? ANY_MIME_TYPE : mimeType);
    }

    public String getEncoding()
    {
        return encoding;
    }

    public void setEncoding(String encoding)
    {
        this.encoding = encoding;
    }

    public boolean isCompatibleWith(DataType dataType)
    {
        if (dataType instanceof ImmutableDataType)
        {
            dataType = ((ImmutableDataType) dataType).getWrappedDataType();
        }
        if (this == dataType)
        {
            return true;
        }
        if (dataType == null)
        {
            return false;
        }

        SimpleDataType that = (SimpleDataType) dataType;

        //ANY_MIME_TYPE will match to a null or non-null value for MimeType
        if ((this.getMimeType() == null && that.getMimeType() != null || that.getMimeType() == null && this.getMimeType() != null) && !ANY_MIME_TYPE.equals(this.mimeType) && !ANY_MIME_TYPE.equals(that.mimeType))
        {
            return false;
        }

        if (this.getMimeType() != null && !this.getMimeType().equals(that.getMimeType()) && !ANY_MIME_TYPE.equals(that.getMimeType()) && !ANY_MIME_TYPE.equals(this.getMimeType()))
        {
            return false;
        }

        if (!this.getType().isAssignableFrom(that.getType()))
        {
            return false;
        }

        return true;
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

        SimpleDataType that = (SimpleDataType) o;

        if (!type.equals(that.type))
        {
            return false;
        }

        //ANY_MIME_TYPE will match to a null or non-null value for MimeType
        if ((this.mimeType == null && that.mimeType != null || that.mimeType == null && this.mimeType != null) && !ANY_MIME_TYPE.equals(that.mimeType))
        {
            return false;
        }

        if (this.mimeType != null && !mimeType.equals(that.mimeType) && !ANY_MIME_TYPE.equals(that.mimeType))
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = type.hashCode();
        result = 31 * result + (mimeType != null ? mimeType.hashCode() : 0);
        return result;
    }

    @Override
    public String toString()
    {
        return "SimpleDataType{" +
                "type=" + type.getName() +
                ", mimeType='" + mimeType + '\'' +
                '}';
    }

    public DataType cloneDataType()
    {
        try
        {
            return (DataType) clone();
        }
        catch (CloneNotSupportedException e)
        {
            // This cannot happen, because we implement Cloneable
            throw new IllegalStateException(e);
        }
    }
}
