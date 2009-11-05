/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.module.http;

import org.mule.api.MuleException;
import org.mule.api.endpoint.InboundEndpoint;
import org.mule.api.transport.MessageRequester;
import org.mule.transport.AbstractMessageRequesterFactory;

/**
 * Creates a customised message requester that is used by iBeans
 */
public class HttpClientMessageRequesterFactory2 extends AbstractMessageRequesterFactory
{
    public MessageRequester create(InboundEndpoint endpoint) throws MuleException
    {
        return new HttpClientMessageRequester2(endpoint);
    }
}
