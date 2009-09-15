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

import org.mule.ibeans.api.application.Schedule;
import org.mule.ibeans.api.application.Send;
import org.mule.tck.testmodels.fruit.Apple;

/**
 * TODO
 */
public class ScheduledSendBean
{
    @Schedule(interval = 2000)
    //every 2 seconds
    @Send(uri = "vm://apples")
    public Apple createACleanApple()
    {
        Apple apple = new Apple();
        apple.wash();
        return apple;
    }
}
