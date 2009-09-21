/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.internal.config;

import org.mule.api.lifecycle.LifecycleManager;
import org.mule.context.DefaultMuleContextBuilder;
import org.mule.lifecycle.GenericLifecycleManager;
import org.mule.lifecycle.phases.MuleContextStartPhase;
import org.mule.lifecycle.phases.MuleContextStopPhase;

/**
 * Enhances the default context builder by customising the lifecycle manager to add support for JSR250
 * annotations @PostConstruct and @PreDestroy and also customising the start and shutdown splash screens for iBeans.
 */
public class IBeansMuleContextBuilder extends DefaultMuleContextBuilder
{
    public IBeansMuleContextBuilder()
    {
        //Customise the start up screen
        setStartupScreen(new StartupSplash());
        setShutdownScreen(new ShutdownSplash());
    }

    protected LifecycleManager getLifecycleManager()
    {
        if (lifecycleManager != null)
        {
            return lifecycleManager;
        }
        else
        {
            //Customise lifecycle to add support for JSR250 annotations @PostConstruct and @PreDestroy
            LifecycleManager lifecycleManager = new GenericLifecycleManager();
            //lifecycleManager.registerLifecycle(new MuleContextInjectPhase());
            lifecycleManager.registerLifecycle(new JSR250MulecontextInitPhase());
            lifecycleManager.registerLifecycle(new MuleContextStartPhase());
            lifecycleManager.registerLifecycle(new MuleContextStopPhase());
            lifecycleManager.registerLifecycle(new JSR250MulecontextDisposePhase());
            return lifecycleManager;
        }
    }
}
