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
import org.mule.ibeans.api.application.params.ReceivedHeaders;

import java.util.List;
import java.util.Map;

/**
 * TODO
 */
public class HeadersBean
{
    @ReceiveAndReply(uri = "vm://header")
    public String processHeader(@ReceivedHeaders("foo") String foo)
    {
        return foo;
    }

    @ReceiveAndReply(uri = "vm://headers")
    public Map processHeaders(@ReceivedHeaders("foo, bar") Map headers)
    {
        return headers;
    }

    @ReceiveAndReply(uri = "vm://headersList")
    public List processHeadersList(@ReceivedHeaders("foo, bar, baz?") List headers)
    {
        return headers;
    }
}