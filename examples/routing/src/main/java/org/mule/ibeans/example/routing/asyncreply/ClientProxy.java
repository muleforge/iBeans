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

import org.mule.module.annotationx.api.ReceiveAndReply;
import org.mule.module.annotationx.api.Reply;
import org.mule.module.annotationx.api.Send;

/**
 * A client entry point service that invokes a backend service and then receives a reply.
 * <p/>
 * Note that if the Reply callback was not set the reply message would be sent back directly the caller.
 */


public class ClientProxy
{
    @ReceiveAndReply(uri = "vm://client", id = "client")
    @Send(uri = "vm://backend")
    @Reply(uri = "vm://reply", replyTimeout = 3000)
    public String appendString(String payload)
    {
        return payload + " Received";
    }
}
