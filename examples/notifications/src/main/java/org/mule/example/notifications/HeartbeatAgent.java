/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.example.notifications;

import org.mule.AbstractAgent;
import org.mule.api.DefaultMuleException;
import org.mule.api.MuleException;
import org.mule.api.lifecycle.InitialisationException;

import javax.resource.spi.work.Work;
import javax.resource.spi.work.WorkException;

/**
 * A simple agent that fire {@link org.mule.example.notifications.HeartbeatNotification} events at a given frequency to
 * notify that the server is alive and well.
 * <p/>
 * Note that this could be implmented easier just by using ibeans annotaitons doing something like i.e.
 * <code>
 * public class HeartbeatService
 * {
 * &amp;#064;MuleInject
 * private IntegrationBeansContext ibeans;
 * <p/>
 * &amp;#064;Schedule(interval = 5000)
 * public void fireHeartbeat()
 * {
 * ibeans.fireNotification(new HeartbeatNotification(ibeans.getConfig().getId()));
 * }
 * }
 * </code>
 * But then we get component notifications for the heartbeat service too.
 */
public class HeartbeatAgent extends AbstractAgent
{
    public static final String NAME = "Heartbeat";

    private long frequency = 5000;

    public HeartbeatAgent()
    {
        super(NAME);
    }

    public long getFrequency()
    {
        return frequency;
    }

    public void setFrequency(long frequency)
    {
        this.frequency = frequency;
    }

    public void initialise() throws InitialisationException
    {
        //No Op
    }

    public void registered()
    {
        //No Op
    }

    public void unregistered()
    {
        //No Op
    }

    public void start() throws MuleException
    {
        try
        {
            muleContext.getWorkManager().scheduleWork(new Heartbeat());
        }
        catch (WorkException e)
        {
            throw new DefaultMuleException(e);
        }
    }

    public void stop() throws MuleException
    {
        //No Op
    }

    public void dispose()
    {
        //No Op
    }

    public class Heartbeat implements Work
    {
        public void release()
        {

        }

        public void run()
        {
            while (true)
            {
                muleContext.fireNotification(new HeartbeatNotification(muleContext.getConfiguration().getId()));
                try
                {
                    Thread.sleep(frequency);
                }
                catch (InterruptedException e)
                {
                    break;
                }
            }
        }
    }
}
