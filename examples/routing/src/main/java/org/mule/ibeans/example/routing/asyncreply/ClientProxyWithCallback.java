/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.example.routing.asyncreply;

import org.mule.config.annotations.endpoints.Reply;
import org.mule.ibeans.api.application.ReceiveAndReply;
import org.mule.ibeans.api.application.Send;
import org.mule.ibeans.api.application.params.MessagePayload;

/**
 * A client entry point service that invokes a backend service and then receives a reply.  Note that when the reply is
 * received we get a callback on {@link #org.mule.ibeans.example.routing.asyncreply.ClientProxy.replyProcessor()} where
 * we can change the payload if necessary before we return the reply back to the caller.
 * <p/>
 * Note that if the Reply callback was not set the reply message would be sent back directly the caller. See {@link org.mule.ibeans.example.routing.asyncreply.ClientProxy}
 */

public class ClientProxyWithCallback
{
    @ReceiveAndReply(uri = "vm://client", id = "client")
    @Send(uri = "vm://backend")
    @Reply(uri = "vm://reply", replyTimeout = 3000, callback = "replyProcessor")
    public String appendString(String payload)
    {
        return payload + " Received";
    }

    public Object replyProcessor(@MessagePayload String reply)
    {
        return reply + " Callback";
    }
}
