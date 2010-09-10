/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.example.channels.email;

import org.ibeans.api.IBeansException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class EmailReceiveTestCase extends AbstractEmailTestCase
{
    protected void addBeans() throws IBeansException
    {
        registerBeans(new EmailReceivePOJO());
    }

    @Test
    public void testBean() throws Exception
    {
        gmail.send("${gmail.username}", "Receive Test", "Receive Testing");
        String result = iBeansContext.receive("result", String.class, RECEIVE_TIMEOUT);
        assertNotNull(result);
        assertEquals("Received 'Receive Test' from muletestinbox@gmail.com. Message is: Receive Testing\r\n", result);

    }
}