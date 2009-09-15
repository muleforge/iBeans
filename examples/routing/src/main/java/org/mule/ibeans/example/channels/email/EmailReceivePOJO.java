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

import org.mule.ibeans.api.application.Receive;
import org.mule.ibeans.api.application.Send;
import org.mule.ibeans.api.application.params.MessagePayload;
import org.mule.ibeans.api.application.params.ReceivedHeaders;

import java.util.Map;

/**
 * TODO
 */
public class EmailReceivePOJO
{
    @Receive(uri = "imaps://muletestinbox%40gmail.com:mule12345678@imap.gmail.com:993", properties = "deleteReadMessages=false,moveToFolder=[Gmail]/Trash")
    @Send(uri = "vm://result", id = "result")
    public String process(@MessagePayload String message, @ReceivedHeaders("from, subject") Map headers)
    {
        String result = "Recevied '" + headers.get("subject") + "' from " + headers.get("from") + ". Message is: " + message;
        return result;
    }
}
