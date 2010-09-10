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

import org.mule.module.annotationx.api.Receive;
import org.mule.module.annotationx.api.Send;

/**
 * TODO
 */
public class JmsReceiveAndSendWithURIsBean
{
    @Receive(uri = "jms://receive", id = "jms-receive", properties = "connectorName=activeMQ")
    @Send(uri = "jms://result", id = "jms-result", properties = "connectorName=activeMQ")
    public String receive(String message)
    {
        return message + " Received";
    }
}