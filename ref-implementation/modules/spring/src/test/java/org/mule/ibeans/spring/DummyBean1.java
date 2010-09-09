/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.spring;

import org.mule.ibeans.IBeansContext;

/**
 * TODO
 */
public class DummyBean1
{
    private IBeansContext iBeansContext;


    public IBeansContext getIBeansContext()
    {
        return iBeansContext;
    }

    public void setIBeansContext(IBeansContext iBeansContext)
    {
        this.iBeansContext = iBeansContext;
    }
}
