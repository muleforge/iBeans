/*
 * $Id:  $
 * -------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.ibeans.impl;

import java.beans.ExceptionListener;

import org.ibeans.api.CallInterceptor;
import org.ibeans.api.InvocationContext;

/**
 * TODO
 */
final class NonIntegrationMethodsCallInterceptor implements CallInterceptor
{
    public void intercept(InvocationContext invocationContext)
    {
        if (invocationContext.getMethod().getName().equals("toString"))
        {
            invocationContext.setResult(toString());
        }
        else if (invocationContext.getMethod().getName().equals("hashCode"))
        {
            invocationContext.setResult(hashCode());
        }
        else if (invocationContext.getMethod().getName().equals("equals"))
        {
            invocationContext.setResult(equals(invocationContext.getArgs()[0]));
        }
        else if (invocationContext.getMethod().getName().equals("setExceptionListener"))
        {
            invocationContext.setExceptionListener((ExceptionListener) invocationContext.getArgs()[0]);
        }
        else
        {
            invocationContext.proceed();
        }
    }
}