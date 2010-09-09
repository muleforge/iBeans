/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans;

import org.mule.api.MuleContext;
import org.mule.api.MuleException;
import org.mule.api.context.MuleContextAware;


/**
 * The factory used to create the {@link IBeansContext} object for an application.  The only
 * way to create this object is through this factory class.
 */
public class IBeansFactory implements MuleContextAware
{
    public static final String REGISTRY_KEY = "_integrationBeansContext";

    public void setMuleContext(MuleContext context)
    {
        try
        {
            context.getRegistry().registerObject(REGISTRY_KEY, new IBeansContext(context));
        }
        catch (MuleException e)
        {
            throw new RuntimeException(e);
        }
    }

}
