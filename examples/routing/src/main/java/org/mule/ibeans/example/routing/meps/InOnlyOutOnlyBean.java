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

import org.mule.api.annotations.param.InboundHeaders;
import org.mule.module.annotationx.api.Receive;
import org.mule.module.annotationx.api.Send;

/**
 * Will invoke the annotated method with data received on the subscribe channel. The result of the method call will be
 * published via the publish channel. If a null is returned from the message nothing will be published.
 */
public class InOnlyOutOnlyBean
{
    @Receive(uri = "vm://test.in", id = "inbound")
    @Send(uri = "vm://test.out", id = "outbound")
    public String process(@InboundHeaders("foo?") String fooHeader) throws Exception
    {
        if (fooHeader != null)
        {
            return "foo header received";
        }
        else
        {
            return null;
        }
    }
}
