/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.example.routing.meps;

import org.mule.api.context.notification.ServerNotification;
import org.mule.ibeans.test.AbstractIBeansTestCase;
import org.mule.tck.functional.FunctionalTestNotificationListener;
import org.mule.util.concurrent.Latch;

import edu.emory.mathcs.backport.java.util.concurrent.TimeUnit;

public class InOnlyTestCase extends AbstractIBeansTestCase
{
    @Override
    protected void doSetUp() throws Exception
    {
        registerBeans(new InOnlyBean());
    }

    public void testExchange() throws Exception
    {
        final Latch latch = new Latch();
        iBeansContext.registerNotificationListener(new FunctionalTestNotificationListener()
        {
            public void onNotification(ServerNotification notification)
            {
                latch.countDown();
            }
        });

        iBeansContext.send("inbound", "some data");
        assertTrue(latch.await(3000, TimeUnit.MILLISECONDS));
    }
}
