/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.example.routing.params;

import org.mule.ibeans.api.application.ReceiveAndReply;
import org.mule.ibeans.api.application.params.MessagePayload;
import org.mule.ibeans.api.application.params.SendHeaders;

import java.util.Map;

/**
 * TODO
 */
public class SendHeadersBean
{
    @ReceiveAndReply(uri = "vm://in")
    public String process(@MessagePayload String foo, @SendHeaders Map sendHeaders)
    {
        sendHeaders.put("key1", "value1");
        return foo + " Received";
    }
}