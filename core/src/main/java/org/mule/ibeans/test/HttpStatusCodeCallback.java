/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.test;

import org.mule.api.MuleMessage;
import org.mule.message.DefaultExceptionPayload;
import org.mule.transport.http.HttpConnector;

/**
 * Sets a Http status code on the result message created on a mock invocation
 */
public class HttpStatusCodeCallback implements MockMessageCallback
{
    private int status;

    public HttpStatusCodeCallback(int status)
    {
        this.status = status;
    }

    public void onMessage(MuleMessage message)
    {
        message.setProperty(HttpConnector.HTTP_STATUS_PROPERTY, status);
        if(status >= 400)
        {
            message.setExceptionPayload(new DefaultExceptionPayload(new Exception("Mock Http Error")));
        }
    }
}
