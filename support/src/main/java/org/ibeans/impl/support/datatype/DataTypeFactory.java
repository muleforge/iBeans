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

import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collection;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.MimeTypeParseException;

import org.ibeans.api.DataType;
import org.ibeans.api.channel.MimeType;
import org.ibeans.api.channel.MimeTypes;

/**
 * Factory class used to create {@link org.mule.api.transformer.DataType} objects based on the 
 * parameter types passed into the factory methods.
 *
 * @since 1.0
 */
public class DataTypeFactory
{
    public static final DataType<String> TEXT_STRING = new SimpleDataType<String>(String.class, MimeTypes.TEXT);
    public static final DataType<String> XML_STRING = new SimpleDataType<String>(String.class, MimeTypes.XML);
    public static final DataType<String> JSON_STRING = new SimpleDataType<String>(String.class, MimeTypes.JSON);
    public static final DataType<String> HTML_STRING = new SimpleDataType<String>(String.class, MimeTypes.HTML);
    public static final DataType<String> ATOM_STRING = new SimpleDataType<String>(String.class, MimeTypes.ATOM);
    public static final DataType<String> RSS_STRING = new SimpleDataType<String>(String.class, MimeTypes.RSS);

    //Common Java types
    public static final DataType<String> STRING = new SimpleDataType<String>(String.class);
    public static final DataType<String> OBJECT = new SimpleDataType<String>(Object.class);
    public static final DataType<String> BYTE_ARRAY = new SimpleDataType<String>(byte[].class);
    public static final DataType<String> INPUT_STREAM = new SimpleDataType<String>(InputStream.class);



    public static DataType<?> create(Class<?> type)
    {
        return create(type, MimeTypes.ANY);
    }

    public static DataType<?> createImmutable(Class<?> type)
    {
        return new ImmutableDataType(create(type, MimeTypes.ANY));
    }

    public static DataType<?> createWithEncoding(Class<?> type, String encoding)
    {
        DataType dataType = create(type);
        dataType.setEncoding(encoding);
        return dataType;
    }

    public static DataType<?> create(Class<?> type, String mimeType) throws MimeTypeParseException
    {
        return create(type, new MimeType(mimeType));
    }

    public static DataType<?> create(Class<?> type, MimeType mimeType)
    {
        if (Collection.class.isAssignableFrom(type))
        {
            Class<? extends Collection<?>> collectionType = (Class<? extends Collection<?>>)type;
            Class<?> itemType = GenericsUtils.getCollectionType(collectionType);
            if (itemType == null)
            {
                return new CollectionDataType(collectionType, mimeType);
            }
            else
            {
                return new CollectionDataType(collectionType, itemType, mimeType);
            }
        }
        //Special case where proxies are used for testing
        if (Proxy.isProxyClass(type))
        {
            return new SimpleDataType(type.getInterfaces()[0], mimeType);
        }

        return new SimpleDataType(type, mimeType);
    }

    public static <T> DataType create(Class<? extends Collection> collClass, Class<T> itemType)
    {
        return create(collClass, itemType, MimeTypes.ANY);
    }

    public static <T> DataType create(Class<? extends Collection> collClass, Class<T> itemType, String mimeType) throws MimeTypeParseException
    {
        return new CollectionDataType(collClass, itemType, mimeType);
    }

    public static <T> DataType create(Class<? extends Collection> collClass, Class<T> itemType, MimeType mimeType)
    {
        return new CollectionDataType(collClass, itemType, mimeType);
    }

    /**
     * Will create a {@link org.mule.api.transformer.DataType} object from an object instance. This method will check
     * if the object o is a {@link org.mule.api.MuleMessage} instance and will take the type from the message payload
     * and check if a mime type is set on the message and used that when constructing the {@link org.mule.api.transformer.DataType}
     * object.
     *
     * @param o an object instance.  This can be a {@link javax.activation.DataHandler}, a {@link javax.activation.DataSource} a collection, a proxy instance or any other
     *          object
     * @return a data type that represents the object type.
     * @throws MimeTypeParseException if the object type passed in has a mime type defined, but that type is malformed
     */
    public static DataType createFromObject(Object o) throws MimeTypeParseException
    {
        Class type = o.getClass();
        String mime = null;
        MimeType mimeType;
        if (o instanceof DataHandler)
        {
            mime = ((DataHandler) o).getContentType();
        }
        else if (o instanceof DataSource)
        {
            mime = ((DataSource) o).getContentType();
        }

        if (mime != null)
        {
            mimeType = new MimeType(mime);
        }
        else
        {
            mimeType = MimeTypes.ANY;
        }

        return create(type, mimeType);
    }

    public static DataType createFromReturnType(Method m)
    {
        return createFromReturnType(m, MimeTypes.ANY);
    }

    public static DataType createFromReturnType(Method m, String mimeType) throws MimeTypeParseException
    {
        return createFromReturnType(m, new MimeType(mimeType));
    }
    public static DataType createFromReturnType(Method m, MimeType mimeType)
    {
        if (Collection.class.isAssignableFrom(m.getReturnType()))
        {
            Class<? extends Collection> cType = (Class<? extends Collection>) m.getReturnType();
            Class itemType = GenericsUtils.getCollectionReturnType(m);

            if (itemType != null)
            {
                return new CollectionDataType(cType, itemType, mimeType);
            }
            else
            {
                return new CollectionDataType(cType, mimeType);
            }
        }
        else
        {
            return new SimpleDataType(m.getReturnType(), mimeType);
        }
    }

    public static DataType createFromParameterType(Method m, int paramIndex)
    {
        return createFromParameterType(m, paramIndex, MimeTypes.ANY);
    }

    public static DataType createFromParameterType(Method m, int paramIndex, String mimeType) throws MimeTypeParseException
    {
        return createFromParameterType(m, paramIndex, new MimeType(mimeType));
    }
    public static DataType createFromParameterType(Method m, int paramIndex, MimeType mimeType)
    {
        if (Collection.class.isAssignableFrom(m.getParameterTypes()[paramIndex]))
        {
            Class<? extends Collection> cType = (Class<? extends Collection>) m.getParameterTypes()[paramIndex];
            Class itemType = GenericsUtils.getCollectionParameterType(new MethodParameter(m, paramIndex));

            if (itemType != null)
            {
                return new CollectionDataType(cType, itemType, mimeType);
            }
            else
            {
                return new CollectionDataType(cType, mimeType);
            }
        }
        else
        {
            return new SimpleDataType(m.getParameterTypes()[paramIndex], mimeType);
        }
    }

    public static DataType createFromField(Field f)
    {
        return createFromField(f, MimeTypes.ANY);
    }

    public static DataType createFromField(Field f, String mimeType) throws MimeTypeParseException
    {
        return createFromField(f, new MimeType(mimeType));
    }

    public static DataType createFromField(Field f, MimeType mimeType)
    {
        if (Collection.class.isAssignableFrom(f.getType()))
        {
            Class<? extends Collection> cType = (Class<? extends Collection>) f.getType();
            Class itemType = GenericsUtils.getCollectionFieldType(f);

            if (itemType != null)
            {
                return new CollectionDataType(cType, itemType, mimeType);
            }
            else
            {
                return new CollectionDataType(cType, mimeType);
            }
        }
        else
        {
            return new SimpleDataType(f.getType(), mimeType);
        }
    }

}
