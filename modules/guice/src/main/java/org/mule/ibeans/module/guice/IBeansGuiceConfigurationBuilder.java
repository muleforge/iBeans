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
import org.mule.module.guice.GuiceConfigurationBuilder;

import com.google.inject.Module;
import com.google.inject.Stage;

import java.util.List;

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
    protected List<Module> getSystemModules(MuleContext muleContext)
    {
        List<Module> modules = super.getSystemModules(muleContext);
        modules.add(new IBeansSupportModule(muleContext));
        return modules;
    }
}
