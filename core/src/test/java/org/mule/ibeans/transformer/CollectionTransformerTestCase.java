/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.transformer;

import org.mule.api.transformer.DataType;
import org.mule.ibeans.api.application.Transformer;
import org.mule.ibeans.internal.AnnotatedTransformerProxy;
import org.mule.ibeans.test.IBeansTestSupport;
import org.mule.transformer.types.CollectionDataType;
import org.mule.transformer.types.DataTypeFactory;
import org.mule.transport.http.ReleasingInputStream;

import java.lang.reflect.Method;
import java.util.ArrayList;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class CollectionTransformerTestCase extends IBeansTestSupport
{
    @Test
    public void transformerRegistration() throws Exception
    {
        Method m = getClass().getDeclaredMethod("dummy", ReleasingInputStream.class);
        Class c[] = new Class[1];
        c[0] = Object.class;
        AnnotatedTransformerProxy trans = new AnnotatedTransformerProxy(
                5, getClass(), m, c,
                null /*anno.sourceMimeType()*/, null /*anno.resultMimeType()*/);

        DataTypeFactory factory = new DataTypeFactory();
        DataType dt = factory.create(ArrayList.class,Object.class, null);
        assertTrue("should be a CollectionDataType", trans.getReturnDataType() instanceof CollectionDataType);
    }

    @Test
    public void transformerRegistration2() throws Exception
    {
        Method m = getClass().getDeclaredMethod("dummy2", ReleasingInputStream.class);
        Class c[] = new Class[1];
        c[0] = Object.class;
        AnnotatedTransformerProxy trans = new AnnotatedTransformerProxy(
                5, getClass(), m, c,
                null /*anno.sourceMimeType()*/, null /*anno.resultMimeType()*/);

        DataTypeFactory factory = new DataTypeFactory();
        DataType dt = factory.create(ArrayList.class,Object.class, null);
        assertTrue("should be a CollectionDataType", trans.getReturnDataType() instanceof CollectionDataType);
        assertEquals(String.class, ((CollectionDataType)trans.getReturnDataType()).getItemType());
    }


    @Transformer
    protected ArrayList dummy(ReleasingInputStream in)
    {
        return new ArrayList();
    }

    @Transformer
    protected ArrayList<String> dummy2(ReleasingInputStream in)
    {
        return new ArrayList<String>();
    }
}
