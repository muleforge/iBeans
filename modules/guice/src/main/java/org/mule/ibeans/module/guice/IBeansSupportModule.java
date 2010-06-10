/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.module.guice;

import org.mule.api.MuleContext;
import org.mule.config.annotations.endpoints.Channel;
import org.mule.ibeans.internal.IntegrationBeanAnnotatedObjectProcessor;
import org.mule.utils.AnnotationMetaData;
import org.mule.utils.AnnotationUtils;

import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.MembersInjector;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matchers;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

import java.util.List;

/**
 * Introduces iBeans processors when configuring objects from Guice.  This model adds support for -
 * - Processing the @IntegrationBean injection annotation (currently only support on Fields)
 * - Processing the flow annotations {@link org.mule.ibeans.api.application.Send}, {@link org.mule.ibeans.api.application.Receive},
 * {@link org.mule.ibeans.api.application.ReceiveAndReply} and {@link @Schedule} annotations
 */
public class IBeansSupportModule extends AbstractModule
{
    private MuleContext muleContext;

    public IBeansSupportModule(MuleContext muleContext)
    {
        this.muleContext = muleContext;
    }

    @Override
    protected void configure()
    {
        bindListener(Matchers.any(), new ServiceIBeansTypeListener());
        bindListener(Matchers.any(), new IBeansTypeListener());

    }

    /**
     * Responsible for discovering flow annotations such as @Receive and @Send and constructing a Mule service from the
     * bean being processed
     */
    class ServiceIBeansTypeListener implements TypeListener
    {

        public <I> void hear(TypeLiteral<I> iTypeLiteral, TypeEncounter<I> iTypeEncounter)
        {

            iTypeEncounter.register(new MembersInjector<I>()
            {
                public void injectMembers(I o)
                {
                    List<AnnotationMetaData> annos = AnnotationUtils.getAllMethodAnnotations(o.getClass());

                    if (annos.size() > 0)
                    {
                        for (AnnotationMetaData data : annos)
                        {
                            if (data.getAnnotation().annotationType().isAnnotationPresent(Channel.class))
                            {
                                GuiceIBeansAnnotatedObjectProcessor processor = new GuiceIBeansAnnotatedObjectProcessor(muleContext, Key.get(o.getClass()) );
                                processor.process(o);
                            }
                        }
                    }
                }
            });

        }
    }

    /**
     * Responsible for handling the @IntegrationBean injection annotation
     */
    class IBeansTypeListener implements TypeListener
    {
        public <I> void hear(TypeLiteral<I> iTypeLiteral, TypeEncounter<I> iTypeEncounter)
        {
            iTypeEncounter.register(new MembersInjector<I>()
            {
                private IntegrationBeanAnnotatedObjectProcessor processor = new IntegrationBeanAnnotatedObjectProcessor(muleContext);

                public void injectMembers(I o)
                {
                    processor.process(o);
                }
            });
        }
    }
}
