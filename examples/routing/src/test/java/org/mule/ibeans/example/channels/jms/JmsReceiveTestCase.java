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

import org.mule.ibeans.test.IBeansRITestSupport;
import org.mule.transport.jms.activemq.ActiveMQJmsConnector;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Test using a Jms Channel to register interest in a Jms Queue
 */
public class JmsReceiveTestCase extends IBeansRITestSupport
{
    @Before
    public void init() throws Exception
    {
        registerBeans(createChannelBuilder("jms-receive", "jms://receive")
                .setConnector(new ActiveMQJmsConnector(muleContext)));
        registerBeans(createChannelBuilder("jms-result", "jms://result")
                .setConnector(new ActiveMQJmsConnector(muleContext)));
        registerBeans(new JmsReceiveAndSendBean());
    }

    @Test
    public void jmsSend() throws Exception
    {
        //Lets send some test data
        iBeansContext.send("jms-receive", "Test Message");
        iBeansContext.send("jms-receive", "Test Message");
        iBeansContext.send("jms-receive", "Test Message");


        String result = iBeansContext.receive("jms-result", String.class, 2000);
        assertNotNull(result);
        assertEquals("Test Message Received", result);

        result = iBeansContext.receive("jms-result", String.class, 2000);
        assertNotNull(result);
        assertEquals("Test Message Received", result);

        result = iBeansContext.receive("jms-result", String.class, 2000);
        assertNotNull(result);
        assertEquals("Test Message Received", result);

        result = iBeansContext.receive("jms-result", String.class, 2000);
        assertNull(result);
    }
}