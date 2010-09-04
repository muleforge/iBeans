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

import org.mule.context.DefaultMuleContextBuilder;

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
}
