/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.internal.client;

import org.mule.api.MessagingException;
import org.mule.api.MuleMessage;
import org.mule.api.MuleSession;
import org.mule.config.i18n.CoreMessages;
import org.mule.ibeans.channels.CHANNEL;
import org.mule.ibeans.internal.ext.DefaultRequestInterfaceBinding;
import org.mule.ibeans.internal.ext.DynamicRequestEndpoint;

/**
 * TODO
 */
public class DynamicRequestInterfaceBinding extends DefaultRequestInterfaceBinding
{
    @Override
    public MuleMessage route(MuleMessage message, MuleSession session) throws MessagingException
    {
        try
        {
            int timeout = message.getIntProperty(CHANNEL.TIMEOUT, getMuleContext().getConfiguration().getDefaultResponseTimeout());
            if (inboundEndpoint instanceof DynamicRequestEndpoint)
            {
                return ((DynamicRequestEndpoint) inboundEndpoint).request(timeout, message);
            }
            else
            {
                return inboundEndpoint.request(getMuleContext().getConfiguration().getDefaultResponseTimeout());
            }
        }
        catch (Exception e)
        {
            throw new MessagingException(CoreMessages.failedToInvoke("inboundEndpoint.request()"), message, e);
        }
    }
}
