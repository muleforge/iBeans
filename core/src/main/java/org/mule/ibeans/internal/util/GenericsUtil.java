/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.internal.util;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;

/**
 * This class is not currently used
 */
public class GenericsUtil
{
    public static Class getParameterizedTypeForMethodReturnType(Method method)
    {
        Class ret = method.getReturnType();

        if (Collection.class.isAssignableFrom(ret))
        {
            Type type = method.getGenericReturnType();
            if (type instanceof ParameterizedType)
            {
                type = ((ParameterizedType) type).getActualTypeArguments()[0];
                if (type instanceof Class)
                {
                    return (Class) type;
                }
                else if (type instanceof GenericArrayType)
                {
                    if (type.toString().equals("byte[]"))
                    {
                        return byte[].class;
                    }
                    else
                    {
                        throw new IllegalArgumentException(type.toString());
                    }
                }
                else
                {
                    throw new IllegalArgumentException();
                }
            }
        }
        return ret;
    }
}
