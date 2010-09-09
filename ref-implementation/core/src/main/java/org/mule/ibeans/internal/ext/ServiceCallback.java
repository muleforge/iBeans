/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.internal.ext;

import org.mule.api.MuleException;
import org.mule.api.endpoint.ImmutableEndpoint;
import org.mule.api.service.Service;

/**
 * This class marks a required hack for meta transports i.e. atom:http to be told when its being attached to a service
 * when the endpoint is used in combination with the @Schedule annotation since tit means that a receiver is never
 * created for the meta transport (the quartz receiver is created and delegates a pooling call to the transport or meta-transport)
 */
public interface ServiceCallback
{

    public void process(Service service, ImmutableEndpoint endpoint) throws MuleException;
}
