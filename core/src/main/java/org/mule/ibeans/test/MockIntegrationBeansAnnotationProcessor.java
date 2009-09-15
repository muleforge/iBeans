/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.test;

import org.mule.api.MuleContext;
import org.mule.api.registry.ObjectProcessor;
import org.mule.ibeans.api.client.MockIntegrationBean;
import org.mule.utils.AnnotationMetaData;
import org.mule.utils.AnnotationUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.List;

import org.mockito.Mockito;

/**
 * Will process any fields on an object with the {@link org.mule.ibeans.api.client.MockIntegrationBean} annotation, inserting
 * a Mockito Mock object.  This is only used for testing, the {@link org.mule.ibeans.test.AbstractIBeansTestCase} will automatically
 * enable this processor.
 */
public class MockIntegrationBeansAnnotationProcessor implements ObjectProcessor
{
    public static final String NAME = "_mockIntegrationBeanProcessor";

    private MuleContext muleContext;

    public MockIntegrationBeansAnnotationProcessor(MuleContext muleContext)
    {
        this.muleContext = muleContext;
    }

    public Object process(Object object)
    {
        List<AnnotationMetaData> annos = AnnotationUtils.getFieldAnnotations(object.getClass(), MockIntegrationBean.class);

        if (annos.size() > 0)
        {
            for (AnnotationMetaData data : annos)
            {
                Field field = (Field) data.getMember();
                field.setAccessible(true);
                Object mockito = Mockito.mock(field.getType(), field.getName());
                InvocationHandler handler = new MockIBeanHandler(field.getType(), muleContext, mockito);

                Object mock = Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{field.getType(), MockIBean.class}, handler);
                try
                {
                    field.set(object, mock);
                }
                catch (IllegalAccessException e)
                {
                    throw new RuntimeException(e);
                }
            }
        }
        return object;
    }
}