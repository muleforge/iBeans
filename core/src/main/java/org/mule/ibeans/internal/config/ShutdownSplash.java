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

import org.mule.api.MuleContext;
import org.mule.ibeans.i18n.IBeansMessages;
import org.mule.util.SplashScreen;

import java.util.Date;

/**
 * Displayed in the logs when Mule iBeans is shut down
 */
public class ShutdownSplash extends SplashScreen
{
    protected void doHeader(MuleContext context)
    {
        long currentTime = System.currentTimeMillis();
        header.add(IBeansMessages.shutdownNormally(new Date()).getMessage());
        long duration = 10;
        if (context.getStartDate() > 0)
        {
            duration = currentTime - context.getStartDate();
        }
        header.add(IBeansMessages.serverWasUpForDuration(duration).getMessage());
    }
}
