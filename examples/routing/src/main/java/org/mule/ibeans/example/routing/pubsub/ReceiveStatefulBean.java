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
import org.mule.tck.testmodels.fruit.Fruit;
import org.mule.tck.testmodels.fruit.FruitBowl;

import javax.inject.Singleton;

/**
 * This example demostrates subscribing on a VM queued channel for Apple objects. The method {@link #washApples(org.mule.tck.testmodels.fruit.Apple)} will
 * get invoked and then the test checks to see if the fruit was added to the bowl
 * <p/>
 * Note that by setting an id on the annotation you can use the the {@link org.mule.module.client.MuleClient} to reference the channel.
 * For example MuleClient.dispatch("apples", new Apple(), null);
 */
@Singleton
public class ReceiveStatefulBean
{
    private FruitBowl bowl = new FruitBowl();

    @Receive(uri = "vm://fruit", id = "fruit")
    public void addToBowl(Fruit fruit)
    {
        bowl.addFruit(fruit);
    }

    public FruitBowl getBowl()
    {
        return bowl;
    }
}
