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

import org.mule.api.MuleMessage;
import org.mule.ibeans.api.client.params.InvocationContext;

import java.lang.reflect.Method;

/**
 * TODO
 */

public interface ClientAnnotationHandler
{
    public MuleMessage invoke(InvocationContext invocationContext, MuleMessage message) throws Exception;

    public String getScheme(Method method);
}
