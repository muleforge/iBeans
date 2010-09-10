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

import org.mule.api.annotations.param.Payload;
import org.mule.module.annotationx.api.ReceiveAndReply;
import org.mule.util.StringUtils;

/**
 * A test back-end service that receives text data and reverses it
 */
public class BackEnd
{
    @ReceiveAndReply(uri = "vm://backend")
    public String reverse(@Payload String payload)
    {
        return StringUtils.reverse(payload);
    }
}
