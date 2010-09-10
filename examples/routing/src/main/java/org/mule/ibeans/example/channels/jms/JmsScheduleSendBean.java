/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.example.channels.jms;

import org.mule.api.annotations.Schedule;
import org.mule.module.annotationx.api.Send;

import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Singleton;

/**
 * Will send a Jms message with a counter every second.  note that the bean is a Singleton becuase it retains state
 */
@Singleton
public class JmsScheduleSendBean
{
    private AtomicInteger count = new AtomicInteger(1);

    @Schedule(interval = 1000)
    @Send(config = "jms-publish")
    public Object send()
    {
        return "New Message " + count.getAndIncrement();
    }
}
