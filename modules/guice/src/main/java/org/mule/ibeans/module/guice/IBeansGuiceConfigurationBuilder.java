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
import org.mule.api.MuleException;
import org.mule.api.registry.InjectProcessor;
import org.mule.api.registry.PreInitProcessor;
import org.mule.ibeans.internal.MuleiBeansAnnotatedObjectProcessor;
import org.mule.impl.annotations.processors.InjectAnnotationProcessor;
import org.mule.impl.annotations.processors.NamedAnnotationProcessor;
import org.mule.module.guice.GuiceConfigurationBuilder;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.Stage;

import java.util.Collection;

/**
 * A custom version of the Mule Guice config builder that customises the default registry object processors
 * to avoid processing the inject annotations twice
 */
public class IBeansGuiceConfigurationBuilder extends GuiceConfigurationBuilder
{
    public IBeansGuiceConfigurationBuilder()
    {
    }

    public IBeansGuiceConfigurationBuilder(ClassLoader classLoader)
    {
        super(classLoader);
    }

    public IBeansGuiceConfigurationBuilder(String basepath)
    {
        super(basepath);
    }

    public IBeansGuiceConfigurationBuilder(String basepath, ClassLoader classLoader)
    {
        super(basepath, classLoader);
    }

    public IBeansGuiceConfigurationBuilder(Module... modules)
    {
        super(modules);
    }

    public IBeansGuiceConfigurationBuilder(Stage stage, Module... modules)
    {
        super(stage, modules);
    }

    @Override
    protected void applyProcessors(Object o, Key key, Injector injector, MuleContext muleContext) throws MuleException
    {
        GuiceIBeansAnnotatedObjectProcessor annotatedObjectProcessor = new GuiceIBeansAnnotatedObjectProcessor(muleContext, injector, key);
        //Process injectors first
        Collection<InjectProcessor> injectProcessors = muleContext.getRegistry().lookupObjects(InjectProcessor.class);
        for (InjectProcessor processor : injectProcessors)
        {
            if (processor.getClass().equals(InjectAnnotationProcessor.class) || processor.getClass().equals(NamedAnnotationProcessor.class))
            {
                continue;
            }
            o = processor.process(o);
        }


        //Then any other processors
        Collection<PreInitProcessor> processors = muleContext.getRegistry().lookupObjects(PreInitProcessor.class);
        for (PreInitProcessor processor : processors)
        {
            if(processor.getClass().equals(MuleiBeansAnnotatedObjectProcessor.class))
            {
                annotatedObjectProcessor.process(o);
            }
            else
            {
                o = processor.process(o);
            }
        }
    }
}
