/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.ibeanscentral;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

public class GenericTypesTestCase extends TestCase
{
    public void testEquals() throws Exception
    {
        Type t1 = Object1.class.getMethod("method1", new Class[]{}).getGenericReturnType();
        Type t2 = Object1.class.getMethod("method2", new Class[]{}).getGenericReturnType();
        Type t3 = Object1.class.getMethod("method3", new Class[]{}).getGenericReturnType();
        Type t4 = Object1.class.getMethod("method4", new Class[]{}).getGenericReturnType();
        Type t5 = Object1.class.getMethod("method5", new Class[]{}).getGenericReturnType();

        assertTrue(isMatch(t1, t2)); //List<Exception> and List<Exception>
        assertTrue(isMatch(t1, t3)); //List<Exception> and List<IOException>
        assertTrue(isMatch(t1, t4)); //List<Exception> and List

        assertTrue(isMatch(t2, t3)); //List<Exception> and List<IOException>
        assertTrue(isMatch(t2, t4)); //List<Exception> and List

        assertFalse(isMatch(t1, t5)); //List<Exception> and List
        assertFalse(isMatch(t2, t5)); //List<Exception> and List
        assertFalse(isMatch(t3, t5));//List<IOException> and List
        assertTrue(isMatch(t4, t5)); // List and List
        assertTrue(isMatch(t5, t5)); //Same

        assertTrue(isMatch(t2, t1)); //List<Exception> and List<Exception>
        assertFalse(isMatch(t3, t1)); //List<IOException> and List<Exception>
        assertTrue(isMatch(t4, t1)); //List and List<Exception>

        assertTrue(isMatch(t1, new ArrayList<IOException>().getClass())); //List<Exception> and List<IOException>
        //TODO assertFalse(isMatch(t1, new ArrayList<String>().getClass())); //List<Exception> and List<String>

    }

    protected boolean isMatch(Type t1, Type t2)
    {
        Class raw1;
        Class param1 = null;

        Class raw2;
        Class param2 = null;
        if (t1 instanceof ParameterizedType)
        {
            raw1 = (Class) ((ParameterizedType) t1).getRawType();
            //Only match single param types right now
            param1 = (Class) ((ParameterizedType) t1).getActualTypeArguments()[0];
        }
        else
        {
            raw1 = (Class) t1;
        }

        if (t2 instanceof ParameterizedType)
        {
            raw2 = (Class) ((ParameterizedType) t2).getRawType();
            //Only match single param types right now            
            param2 = (Class) ((ParameterizedType) t2).getActualTypeArguments()[0];
        }
        else
        {
            raw2 = (Class) t2;
        }
        boolean match = false;
        if (raw1.isAssignableFrom(raw2))
        {
            if (param1 != null && param2 != null)
            {
                match = param1.isAssignableFrom(param2);
            }
            else
            {
                match = true;
            }
        }
        return match;
    }

    public class Object1
    {
        public List<Exception> method1()
        {
            return null;
        }

        public List<Exception> method2()
        {
            return null;
        }

        public List<IOException> method3()
        {
            return null;
        }

        public List method4()
        {
            List x = new ArrayList();
            x.add(new IOException());
            x.add(new FileNotFoundException());
            return x;
        }

        public List<Number> method5()
        {
            List x = new ArrayList();
            x.add(new Double(2.3));
            x.add(new Integer(3));
            return x;
        }
    }
}
