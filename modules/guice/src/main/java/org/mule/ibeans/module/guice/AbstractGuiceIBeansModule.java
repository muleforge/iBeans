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

import org.mule.api.MuleException;
import org.mule.api.registry.RegistrationException;
import org.mule.module.annotationx.config.ChannelConfigBuilder;
import org.mule.module.guice.AbstractMuleGuiceModule;
import org.mule.transport.quartz.config.ScheduleConfigBuilder;

/**
 * A Base Guice module for ibeans that provides helper methods for binding {@link org.mule.ibeans.config.ChannelConfigBuilder} objects to
 * the iBeans context
 */
public abstract class AbstractGuiceIBeansModule extends AbstractMuleGuiceModule
{
    /**
     * A helper method that binds a {@link org.mule.ibeans.config.ChannelConfigBuilder} to the Mule context.
     * Note that this does not bind the builder to the Guice injector, but there is no need to. This is a configuration
     * object consumable by the iBeans container only.
     *
     * @param builder
     */
    protected void bind(ChannelConfigBuilder builder)
    {
        try
        {
            muleContext.getRegistry().registerObject(builder.getName(), builder);
        }
        catch (RegistrationException e)
        {
            addError(e);
        }
    }

    protected void bind(ScheduleConfigBuilder builder)
    {
        try
        {
            muleContext.getRegistry().registerObject(builder.getName(), builder);
        }
        catch (RegistrationException e)
        {
            addError(e);
        }
    }

    protected ChannelConfigBuilder channelBuilder(String channelId, String uri)
    {
        try
        {
            return new ChannelConfigBuilder(channelId, uri, muleContext);
        }
        catch (MuleException e)
        {
            //TODO better handling.  What is the right thing to do? We cannot addError() and return null
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    protected ScheduleConfigBuilder scheduleBuilder(String schedueId)
    {
        try
        {
            return new ScheduleConfigBuilder(schedueId, muleContext);
        }
        catch (MuleException e)
        {
            //TODO better handling.  What is the right thing to do? We cannot addError() and return null            
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
