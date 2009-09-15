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

import org.mule.ibeans.test.AbstractIBeansTestCase;
import org.mule.transport.jms.activemq.ActiveMQJmsConnector;

/**
 * Test using a Jms Channel to register interest in a Jms Queue
 */
public class JmsReceiveWithURITestCase extends AbstractIBeansTestCase
{
    protected void doSetUp() throws Exception
    {
        ActiveMQJmsConnector connector = new ActiveMQJmsConnector();
        connector.setName("activeMQ");
        registerBeans(connector);
        registerBeans(new JmsReceiveAndSendWithURIsBean());
    }

    public void testJmsSend() throws Exception
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