/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.example.routing.meps;

import org.mule.ibeans.api.application.ReceiveAndReply;
import org.mule.ibeans.api.application.params.ReceivedHeaders;

/**
 * Receives a message from another party and the result of the service invocation is returned. If the
 * service returns null a message with a NullPayload payload is returned.
 */
public class InOutBean
{
    @ReceiveAndReply(uri = "vm://test.in", id = "inbound")
    public String process(@ReceivedHeaders("foo?") String fooHeader) throws Exception
    {
        if (fooHeader != null)
        {
            return "foo header received";
        }
        else
        {
            return "foo header not received";
        }
    }
}
