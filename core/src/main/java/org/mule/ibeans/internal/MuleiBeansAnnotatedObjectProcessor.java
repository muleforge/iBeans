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
import org.mule.api.MuleException;
import org.mule.api.model.Model;
import org.mule.config.annotations.endpoints.Channel;
import org.mule.ibeans.internal.ext.IBeansComponentLifecycleAdapterFactory;
import org.mule.impl.annotations.processors.AnnotatedServiceObjectProcessor;
import org.mule.utils.AnnotationMetaData;
import org.mule.utils.AnnotationUtils;

import java.util.List;

/**
 * Will check all method level annotations to see if they are {@link org.mule.config.annotations.endpoints.Channel} annotations.
 */
public class MuleiBeansAnnotatedObjectProcessor extends AnnotatedServiceObjectProcessor
{
    public MuleiBeansAnnotatedObjectProcessor()
    {
    }

    public MuleiBeansAnnotatedObjectProcessor(MuleContext muleContext)
    {
        this();
        setMuleContext(muleContext);
    }

    @Override
    public Object process(Object object)
    {
        List<AnnotationMetaData> annos = AnnotationUtils.getAllMethodAnnotations(object.getClass());

        if (annos.size() > 0)
        {
            for (AnnotationMetaData data : annos)
            {
                if (data.getAnnotation().annotationType().isAnnotationPresent(Channel.class))
                {
                    //Since this processor creates a service entry based on annotations on the service component, we only want olny
                    //want to create the service once.  If the ObjectFactory for the service is prototype, this processor will
                    //get called for every instance of the object
                    String serviceName = object.getClass().getName() + ".service";
                    if (context.getRegistry().lookupService(serviceName) != null)
                    {
                        return object;
                    }
                    org.mule.api.service.Service service;
                    try
                    {
                        MuleiBeansAnnotatedServiceBuilder builder = new MuleiBeansAnnotatedServiceBuilder(context);
                        builder.setModel(getOrCreateModel());
                        service = builder.createService(object);
                        context.getRegistry().registerService(service);
                        break;
                    }
                    catch (MuleException e)
                    {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return object;
    }

    @Override
    protected Model createModel()
    {
        Model m = super.createModel();
        m.setLifecycleAdapterFactory(new IBeansComponentLifecycleAdapterFactory());
        m.setEntryPointResolverSet(new IBeansEntrypointResolverSet());
        return m;

    }
}
