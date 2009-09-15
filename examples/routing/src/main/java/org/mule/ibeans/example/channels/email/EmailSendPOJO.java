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

import org.mule.ibeans.api.application.Schedule;
import org.mule.ibeans.api.application.Send;
import org.mule.ibeans.api.application.params.SendHeaders;
import org.mule.transport.email.MailProperties;

import java.util.Map;

/**
 * TODO
 */
public class EmailSendPOJO
{
    @Schedule(interval = 3000)
    @Send(uri = "smtps://muletestinbox%40gmail.com:mule12345678@smtp.gmail.com:465", properties = "mail.smtps.auth=true")
    public String process(@SendHeaders Map sendHeaders)
    {
        sendHeaders.put(MailProperties.FROM_ADDRESS_PROPERTY, "muletestinbox@gmail.com");
        sendHeaders.put(MailProperties.TO_ADDRESSES_PROPERTY, "muletestinbox@gmail.com");
        sendHeaders.put(MailProperties.SUBJECT_PROPERTY, "A Scheduled Email");
        return "This is a test dude";
    }
}