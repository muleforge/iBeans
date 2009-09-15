/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.gmail;

import org.mule.ibeans.api.client.IntegrationBean;
import org.mule.ibeans.test.AbstractIBeansTestCase;

import javax.mail.Message;

/**
 * TODO
 */
public class GmailClientTestCase extends AbstractIBeansTestCase
{
    public static final int TIMEOUT = 3000;

    @IntegrationBean
    private GMailIBean gmail;

    @Override
    protected void doSetUp() throws Exception
    {
        gmail.init("muletestinbox@gmail.com", "mule12345678");
        //Clear any crap
        while (gmail.receiveNext(TIMEOUT) != null)
        {
        }
    }

    public void testGmailSendAndReceiveIMAP() throws Exception
    {
        gmail.send("muletestinbox@gmail.com", "Hey, Just Testing IMAP", "Test 1, 2, 3");

        Message mail = gmail.receiveNext(TIMEOUT);
        assertNotNull(mail);
        assertEquals("Hey, Just Testing IMAP", mail.getSubject());
        assertTrue(mail.getContent().toString().startsWith("Test 1, 2, 3"));

    }

    public void testGmailSendAndReceiveIMAPReusingIBean() throws Exception
    {
        gmail.init("muletestinbox@gmail.com", "mule12345678");

        gmail.send("muletestinbox@gmail.com", "Hey, Just Testing IMAP Again", "Test 1, 2, 3");

        Message mail = gmail.receiveNext(TIMEOUT);
        assertNotNull(mail);
        assertEquals("Hey, Just Testing IMAP Again", mail.getSubject());
        assertTrue(mail.getContent().toString().startsWith("Test 1, 2, 3"));

    }

}
