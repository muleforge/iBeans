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

import org.mule.api.annotations.Schedule;
import org.mule.api.annotations.param.InboundHeaders;
import org.mule.api.annotations.param.Payload;
import org.mule.ibeans.channels.IMAP;
import org.mule.module.annotationx.api.Receive;
import org.mule.module.annotationx.api.Send;

import java.util.Map;

/**
 * TODO
 */
public class EmailReceivePOJO
{
    @Schedule(interval = 5000)
    @Receive(uri = "imaps://${gmail.username.encoded}:${gmail.password}@imap.gmail.com:993", properties = {IMAP.KEEP_READ_MESSAGES, IMAP.MOVE_TO_FOLDER_KEY + "[Gmail]/Trash"})
    @Send(uri = "vm://result", id = "result")
    public String process(@Payload String message, @InboundHeaders("from, subject") Map headers)
    {
        return "Received '" + headers.get("subject") + "' from " + headers.get("from") + ". Message is: " + message;
    }
}
