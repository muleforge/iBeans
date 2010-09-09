/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.web.jabsorb;

import org.mule.util.ClassUtils;

import org.jabsorb.serializer.AbstractSerializer;
import org.jabsorb.serializer.MarshallException;
import org.jabsorb.serializer.ObjectMatch;
import org.jabsorb.serializer.SerializerState;
import org.jabsorb.serializer.UnmarshallException;

/**
 * TODO
 */
public class ClassSerializer extends AbstractSerializer
{

    /**
     * Unique serialisation id.
     */
    private final static long serialVersionUID = 2;

    /**
     * Classes that this can serialise to.
     */
    private static Class<?>[] _JSONClasses = new Class[]{String.class};

    /**
     * Classes that this can serialise.
     */
    private static Class<?>[] _serializableClasses = new Class[]{Class.class};

    public Class[] getJSONClasses()
    {
        return _JSONClasses;
    }

    public Class[] getSerializableClasses()
    {
        return _serializableClasses;
    }

    public Object marshall(SerializerState state, Object p, Object o)
            throws MarshallException
    {
        if (o instanceof Class)
        {
            return ((Class)o).getName();
        }
        return null;
    }

    public ObjectMatch tryUnmarshall(SerializerState state, Class aClass, Object json) throws UnmarshallException
    {

        final Class<?> classes[] = json.getClass().getClasses();
        for (int i = 0; i < classes.length; i++)
        {
            if (classes[i].equals(Class.class))
            {
                state.setSerialized(json, ObjectMatch.OKAY);
                return ObjectMatch.OKAY;
            }
        }

        state.setSerialized(json, ObjectMatch.SIMILAR);
        return ObjectMatch.SIMILAR;
    }

    public Object unmarshall(SerializerState state, Class clazz, Object json)
            throws UnmarshallException
    {
        String val = json.toString();
        if (clazz.equals(Class.class))
        {
            try
            {
                return ClassUtils.loadClass(val, getClass());
            }
            catch (Exception e)
            {
                throw new UnmarshallException("could not load class: " + val, e);
            }
        }
        return null;
    }
}
