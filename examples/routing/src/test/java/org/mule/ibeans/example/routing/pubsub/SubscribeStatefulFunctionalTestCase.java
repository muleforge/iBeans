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

import org.mule.ibeans.test.IBeansRITestSupport;
import org.mule.tck.testmodels.fruit.Apple;
import org.mule.tck.testmodels.fruit.Banana;
import org.mule.tck.testmodels.fruit.FruitBowl;
import org.mule.tck.testmodels.fruit.Orange;

import org.ibeans.api.IBeansException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests registering an annotated component with the registry programmatically and using
 * property placeholders in the annotations
 */
public class SubscribeStatefulFunctionalTestCase extends IBeansRITestSupport
{
    private ReceiveStatefulBean bean = new ReceiveStatefulBean();

    @Before
    public void init() throws IBeansException
    {
        registerBeans(bean);
    }

    @Test
    public void subscribe() throws Exception
    {
        //Because this is a publish annotation we will not recieve a response, but the component will received the messgae
        iBeansContext.send("fruit", new Apple());

        //dispatch a second and third apple
        iBeansContext.send("fruit", new Banana());
        iBeansContext.send("fruit", new Orange());

        //Need to wait for the dispatch JIC
        Thread.sleep(2000);
        FruitBowl bowl = bean.getBowl();
        assertEquals(3, bowl.getFruit().size());
        assertTrue(bowl.hasApple());
        assertTrue(bowl.hasBanana());
    }
}