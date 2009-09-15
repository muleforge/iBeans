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

import org.mule.ibeans.test.AbstractIBeansTestCase;
import org.mule.tck.testmodels.fruit.Apple;

/**
 * Tests registering an annotated component with the registry programmatically and using
 * property placeholders in the annotations
 */
public class SubscribePublishFunctionalTestCase extends AbstractIBeansTestCase
{
    public static final int TIMEOUT = 2000;

    protected void doSetUp() throws Exception
    {
        registerBeans(new ReceiveSendBean());
    }

    public void testSubPub() throws Exception
    {
        //Because this is a publish annotation we will not recieve a response, but the component will received the messgae
        iBeansContext.send("apples", new Apple());

        //dispatch a second apple
        iBeansContext.send("apples", new Apple());

        //Get the first Apple
        Apple result = iBeansContext.receive("washed", Apple.class, TIMEOUT);
        assertNotNull(result);
        assertTrue(result.isWashed());

        //And the second
        result = iBeansContext.receive("washed", Apple.class, TIMEOUT);
        assertNotNull(result);
        assertTrue(result.isWashed());

        //There should be no more
        result = iBeansContext.receive("washed", Apple.class, TIMEOUT);
        assertNull(result);
    }
}