/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.internal;

import org.mule.api.MuleContext;
import org.mule.api.context.MuleContextAware;
import org.mule.api.registry.ObjectProcessor;
import org.mule.ibeans.api.client.IntegrationBean;
import org.mule.ibeans.internal.client.AnnotatedInterfaceBinding;
import org.mule.impl.annotations.processors.AnnotatedServiceObjectProcessor;
import org.mule.utils.AnnotationMetaData;
import org.mule.utils.AnnotationUtils;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Will check all method level annotations to see if they are {@link org.mule.config.annotations.endpoints.Channel} annotations.
 */
public class IntegrationBeanAnnotatedObjectProcessor implements ObjectProcessor, MuleContextAware
{
    private MuleContext muleContext;

    public IntegrationBeanAnnotatedObjectProcessor()
    {
    }

    public IntegrationBeanAnnotatedObjectProcessor(MuleContext muleContext)
    {
        this();
        setMuleContext(muleContext);
    }

    public void setMuleContext(MuleContext context)
    {
        this.muleContext = context;
    }

    public Object process(Object object)
    {

        List<AnnotationMetaData> annos = AnnotationUtils.getFieldAnnotations(object.getClass(), IntegrationBean.class);

        for (AnnotationMetaData data : annos)
        {
            Field field = (Field) data.getMember();
            field.setAccessible(true);
            AnnotatedInterfaceBinding router = new AnnotatedInterfaceBinding();
            router.setMuleContext(muleContext);
            router.setInterface(field.getType());
            Object proxy = router.createProxy(new Object());
            try
            {
//                if(field.get(object)!=null)
//                {
//                    return object;
//                    //TODO fix: The DefaultLifecycleAdapter calls applyprocessors that invoke the @IntegrationBean processor
//                    // But iBeans also discovers IntegrationBean annotations
//                    //throw new IllegalStateException("Integration Beans is already set on object: " + object);
//                }
                field.set(object, proxy);
            }
            catch (IllegalAccessException e)
            {
                throw new RuntimeException("Failed to create IbtegrationBean proxy for: " + field.getType(), e);
            }
        }
        return object;
    }
}