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

import org.mule.ibeans.api.application.Schedule;
import org.mule.ibeans.api.application.Send;
import org.mule.ibeans.api.application.Receive;

/**
 * TODO
 */
public class JmsReceiveAndSendBean
{
    @Receive(config = "jms-receive")
    @Send(config = "jms-result")
    public String receive(String message)
    {
        return message + " Received";
    }
}