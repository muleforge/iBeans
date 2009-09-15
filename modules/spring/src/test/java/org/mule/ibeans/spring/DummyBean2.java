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
import org.mule.ibeans.api.client.IntegrationBean;

import javax.inject.Inject;

/**
 * TODO
 */
public class DummyBean2
{
    @Inject
    private IBeansContext iBeansContext;

    @IntegrationBean
    private DummyIBean dummy;


    public IBeansContext getIBeansContext()
    {
        return iBeansContext;
    }

    public void setIBeansContext(IBeansContext iBeansContext)
    {
        this.iBeansContext = iBeansContext;
    }

    public DummyIBean getDummy()
    {
        return dummy;
    }

    public void setDummy(DummyIBean dummy)
    {
        this.dummy = dummy;
    }
}
