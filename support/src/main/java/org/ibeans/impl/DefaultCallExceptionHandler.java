/*
 * $Id: DefaultCallExceptionHandler.java 2 2009-09-15 10:51:49Z ross $
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.ibeans.impl;

import org.ibeans.api.CallException;
import org.ibeans.api.CallExceptionHandler;
import org.ibeans.api.Response;

/**
 * The default handler used for creating a {@link CallException} when an error occurs trying to dispatch
 * a call using iBeans
 */
public class DefaultCallExceptionHandler implements CallExceptionHandler
{
    public CallException createCallException(Response message, Throwable e, String connectorProtocol)
    {
        //String statusCodeName = ExceptionHelper.getErrorCodePropertyName(connectorProtocol);

        String code = message.getStatusCode();

        CallException ce = new CallException(e.getMessage(), code, e);
        for (String name : message.getHeaderNames())
        {
            ce.getInfo().put(name, message.getHeader(name));
        }

        try
        {
            ce.getInfo().put("response.payload", message.getPayload());
        }
        catch (Exception e1)
        {
            ce.getInfo().put("exception.handler.error", e1.getMessage());
        }
        return ce;
    }

    
}
