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
 * Test using a Jms Channel which requires a channel builder
 */
public class JmsScheduledSendTestCase extends AbstractIBeansTestCase
{
    protected void doSetUp() throws Exception
    {
        registerBeans(createChannelBuilder("jms-publish", "jms://publish")
                .setConnector(new ActiveMQJmsConnector()));
        registerBeans(new JmsScheduleSendBean());
    }

    public void testJmsSend() throws Exception
    {
        String result = iBeansContext.receive("jms-publish", String.class, 2000);
        assertNotNull(result);
        assertEquals("New Message 1", result);

        result = iBeansContext.receive("jms-publish", String.class, 2000);
        assertNotNull(result);
        assertEquals("New Message 2", result);

        result = iBeansContext.receive("jms-publish", String.class, 2000);
        assertNotNull(result);
        assertEquals("New Message 3", result);
    }
}
