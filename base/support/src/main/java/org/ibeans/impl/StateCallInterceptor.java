/*
 * $Id$
 * -------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.ibeans.impl;

import org.ibeans.api.AbstractCallInterceptor;
import org.ibeans.api.InvocationContext;

/**
 * TODO
 */
final class StateCallInterceptor extends AbstractCallInterceptor
{
    public void intercept(InvocationContext invocationContext) throws Exception
    {
        if (invocationContext.isStateCall())
        {
            // If this is a state call we don't need to create a message
            // Neither do we proceed down the interceptor chain
            return;
        }
        else
        {
            invocationContext.proceed();
        }
    }
}