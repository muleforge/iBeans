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

import org.mule.ibeans.api.client.IntegrationBean;
import org.mule.ibeans.gmail.GMailIBean;
import org.mule.ibeans.test.AbstractIBeansTestCase;

import javax.mail.Message;

public class EmailSendTestCase extends AbstractIBeansTestCase
{
    public static final int TIMEOUT = 3000;

    //We still use the GMailIBean for testing but SendEmailPOJO we're testing does not
    @IntegrationBean
    private GMailIBean gmail;

    @Override
    protected boolean isStartContext()
    {
        //Since the send email is on a timer we need to only start the context once we have cleared the inbox
        return false;
    }

    @Override
    protected void doSetUp() throws Exception
    {
        registerBeans(new EmailSendPOJO());
        gmail.init("muletestinbox@gmail.com", "mule12345678");
        //drain the box
        while (gmail.receiveNext(TIMEOUT) != null)
        {
        }
        //Since the send email is on a timer we need to only start the context once we have cleared the inbox
        muleContext.start();

    }

    public void testBean() throws Exception
    {
        Message message = gmail.receiveNext(5000);
        assertNotNull(message);
        assertEquals("A Scheduled Email", message.getSubject());
        assertEquals("This is a test dude\r\n", message.getContent());

    }
}