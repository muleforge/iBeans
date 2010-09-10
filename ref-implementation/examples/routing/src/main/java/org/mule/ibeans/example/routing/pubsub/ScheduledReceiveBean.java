/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.example.routing.pubsub;

import org.mule.api.annotations.Schedule;
import org.mule.module.annotationx.api.Receive;
import org.mule.module.annotationx.api.Send;
import org.mule.tck.testmodels.fruit.Apple;

/**
 * When using the {@link org.mule.ibeans.api.application.Schedule} annotation with the {@link org.mule.ibeans.api.application.Receive} annotation
 * it creates a scheduled poll of the receiver URI. This is useful for scheduling reads from a channel.
 */
public class ScheduledReceiveBean
{
    @Schedule(interval = 3000)
    @Receive(uri = "vm://apples", id = "apples")
    @Send(uri = "vm://washed", id = "washed")
    public Apple washApples(Apple apple)
    {
        apple.wash();
        return apple;
    }
}
