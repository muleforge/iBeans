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

import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.config.ExceptionHelper;
import org.mule.ibeans.api.client.CallException;

import java.util.Iterator;

/**
 * The defaut handler used for creating a {@link CallException} when an error occurs trying to dispatch
 * a call using iBeans
 */
public class DefaultCallExceptionHandler implements CallExceptionHandler
{
    public CallException createCallException(MuleMessage message, Throwable e, String connectorProtocol)
    {
        Throwable t = ExceptionHelper.getRootException(e);
        MuleException muleException = ExceptionHelper.getRootMuleException(e);
        String statusCodeName = ExceptionHelper.getErrorCodePropertyName(connectorProtocol);

        Object code = message.getProperty(statusCodeName);

        if (code != null)
        {
            code = code.toString();
        }
        CallException ce = new CallException(t.getMessage(), (String) code, t);
        for (Iterator iterator = message.getPropertyNames().iterator(); iterator.hasNext();)
        {
            String name = (String) iterator.next();
            ce.getInfo().put(name, message.getProperty(name));
        }
        if (muleException != null)
        {
            ce.getInfo().putAll(muleException.getInfo());
        }
        try
        {
            ce.getInfo().put("response.payload", message.getPayloadAsString());
        }
        catch (Exception e1)
        {
            ce.getInfo().put("exception.handler.error", e1.getMessage());
        }
        return ce;
    }
}
