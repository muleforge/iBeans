/*
 * $Id$
 * -------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.ibeans.api;

import java.io.Serializable;

import org.ibeans.api.channel.MimeType;

/**
 * Defines a Java type associated with additional information about the data.  This may be a mime
 * type for the type or for collections, the collection item type can be stored with the collection
 * type.
 */
public interface DataType<T> extends Serializable, Cloneable
{
    public static final String ANY_MIME_TYPE = "*/*";

    /**
     * The object type of the source object to transform.
     *
     * @return the class object of the source object. This must not be null
     */
    Class<?> getType();

    /**
     * The mime type of the the source object to transform.
     *
     * @return the mime type of the source object. This may be null if the mime type is not known, or if the mime type is
     *         not needed
     */
    String getMimeType();

    /**
     * @return The encoding for the object to transform
     */
    String getEncoding();

    /**
     * @param encoding The encoding for the object to transform
     */
    void setEncoding(String encoding);

    /**
     * The mime type of the the source object to transform.
     *
     * @param mimeType the mime type of the source object. This may be null if the mime type is not known, or if the mime type is
     *                 not needed
     */
    void setMimeType(String mimeType);

    /**
     * Used to determine if this data type is compatible with the data type passed in.  This checks to see if the mime types are
     * equal and whether the Java types are assignable
     *
     * @param dataType the dataType object to compare with
     * @return true if the mime types are the same and this type can be assigned to the dataType.type.
     */
    boolean isCompatibleWith(DataType dataType);

    /**
     * @return Create an exact copy of this datatype
     */
    DataType cloneDataType();

}
