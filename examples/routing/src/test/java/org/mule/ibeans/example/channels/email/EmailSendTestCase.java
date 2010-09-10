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

import javax.mail.Message;

import org.ibeans.api.IBeansException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class EmailSendTestCase extends AbstractEmailTestCase
{
    protected void addBeans() throws IBeansException
    {
        registerBeans(new EmailReceiveUsingIBean());
    }

    @Test
    public void testBean() throws Exception
    {
        Message message = gmail.receiveNext(RECEIVE_TIMEOUT);
        assertNotNull(message);
        assertEquals("A Scheduled Email", message.getSubject());
        assertEquals("This is a test dude\r\n", message.getContent());

    }
}