/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.module.guice;

import org.mule.api.config.ConfigurationBuilder;
import org.mule.ibeans.test.IBeansTestSupport;
import org.mule.module.guice.GuiceConfigurationBuilder;
import org.mule.util.CollectionUtils;

import com.google.inject.Module;

import java.util.ArrayList;
import java.util.List;

/**
 * A base test support class for an IBeans test that wants to use Guice modules as part of the set up.
 */
public abstract class GuiceIBeansTestSupport extends IBeansTestSupport
{
    @Override
    protected void addBuilders(List<ConfigurationBuilder> builders)
    {
        List<Module> modules = new ArrayList<Module>();
        addModules(modules);
        builders.add(new GuiceConfigurationBuilder(CollectionUtils.toArrayOfComponentType(modules, Module.class)));
    }

     /**
     * Register any modules via the {@link org.mule.module.guice.GuiceConfigurationBuilder}
     *
     * @param modules A list of Guice modules to process
     */
    protected abstract void addModules(List<Module> modules);
}
