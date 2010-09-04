/*
 * $Id: AbstractCallInterceptor.java 115 2009-10-22 04:08:33Z dfeist $
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.ibeans.api;

public abstract class AbstractCallInterceptor implements CallInterceptor
{

    public void intercept(InvocationContext invocationContext) throws Throwable
    {
        beforeCall(invocationContext);
        invocationContext.proceed();
        if (!invocationContext.isExceptionThrown())
        {
            afterCall(invocationContext);
        }
        else
        {
            handleException(invocationContext);
        }
    }

    public void beforeCall(InvocationContext invocationContext) throws Throwable
    {
        // Template method
    }

    public void afterCall(InvocationContext invocationContext) throws Throwable
    {
        // Template method
    }

    public void handleException(InvocationContext invocationContext) throws Throwable
    {
        // Template method
    }

}
