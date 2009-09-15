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
 */
public class ScheduledSendFunctionalTestCase extends AbstractIBeansTestCase
{
    protected void doSetUp() throws Exception
    {
        registerBeans(new ScheduledReceiveBean());
    }

    public void testSchedleReceiveAndPublish() throws Exception
    {
        //put a new apple onto the apples queue where our bean will poll for new apples
        iBeansContext.send("apples", new Apple());
        Apple result = iBeansContext.receive("washed", Apple.class, 4000);
        assertNotNull(result);
        assertTrue(result.isWashed());
        //We only put one apple on the queue so we should not find any more on the washed queue
        result = iBeansContext.receive("washed", Apple.class, 4000);
        assertNull(result);

        //lets put another apple on the queue
        iBeansContext.send("apples", new Apple());

        result = iBeansContext.receive("washed", Apple.class, 4000);
        assertNotNull(result);
        assertTrue(result.isWashed());
        //We only put one more apple on the queue so we should not find any more on the washed queue
        result = iBeansContext.receive("washed", Apple.class, 4000);
        assertNull(result);
    }
}