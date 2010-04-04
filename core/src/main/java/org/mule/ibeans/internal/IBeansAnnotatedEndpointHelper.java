/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.internal;

import org.mule.api.MuleContext;
import org.mule.api.MuleException;
import org.mule.api.endpoint.ImmutableEndpoint;
import org.mule.ibeans.api.client.Call;
import org.mule.ibeans.internal.client.CallOutboundEndpoint;
import org.mule.impl.endpoint.AnnotatedEndpointData;
import org.mule.impl.endpoint.AnnotatedEndpointHelper;

/**
 * TODO
 */
public class IBeansAnnotatedEndpointHelper extends AnnotatedEndpointHelper
{
    public IBeansAnnotatedEndpointHelper(MuleContext muleContext) throws MuleException
    {
        super(muleContext);
    }

    @Override
    public ImmutableEndpoint processEndpoint(AnnotatedEndpointData epData) throws MuleException
    {
        if(epData.getAnnotation() instanceof Call)
        {
            preprocessEndpointData(epData);
            return new CallOutboundEndpoint(muleContext, epData);
        }
        return super.processEndpoint(epData);
    }
}
