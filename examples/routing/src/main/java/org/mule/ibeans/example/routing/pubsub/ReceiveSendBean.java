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

import org.mule.ibeans.api.application.Receive;
import org.mule.ibeans.api.application.Send;
import org.mule.tck.testmodels.fruit.Apple;

/**
 * This example demostrates subscribing on a VM queued channel for Apple objects. The method {@link #washApples(org.mule.tck.testmodels.fruit.Apple)} will
 * get invoked and the fruit is added to the bowl. In the test the bowl is checked to see if our fruit was added.
 * <p/>
 * Note that by setting an id on the annotation you can use the the {@link org.mule.module.client.MuleClient} to reference the channel.
 * For example MuleClient.dispatch("apples", new Apple(), null);
 */
public class ReceiveSendBean
{
    @Receive(uri = "vm://apples", id = "apples")
    @Send(uri = "vm://washed", id = "washed")
    public Apple washApples(Apple apple)
    {
        apple.wash();
        return apple;
    }
}
