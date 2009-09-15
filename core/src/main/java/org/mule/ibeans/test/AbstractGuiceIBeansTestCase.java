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

import org.mule.api.config.ConfigurationException;
import org.mule.module.guice.GuiceConfigurationBuilder;

import com.google.inject.Module;

/**
 * A base testcase for an IBeans test that wants to use Guice moudles as part of the set up. to configure modules
 * just pass them in via the {@link #addBeans(java.util.List)} method
 */
public abstract class AbstractGuiceIBeansTestCase extends AbstractIBeansTestCase
{
    /**
     * Register any modules via the {@link org.mule.module.guice.GuiceConfigurationBuilder}
     *
     * @param modules A list of Guice modules to process
     * @throws ConfigurationException if the configuration builder fails to configure the modules on the context
     */
    protected void registerModules(Module... modules) throws ConfigurationException
    {
        new GuiceConfigurationBuilder(modules).configure(muleContext);
    }
}
