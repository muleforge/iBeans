/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.internal.ext;

import org.mule.api.MuleContext;
import org.mule.api.MuleException;
import org.mule.api.component.JavaComponent;
import org.mule.api.component.LifecycleAdapter;
import org.mule.api.component.LifecycleAdapterFactory;
import org.mule.api.model.EntryPointResolverSet;
import org.mule.component.DefaultLifecycleAdapter;

/**
 * TODO
 */
public class IBeansComponentLifecycleAdapterFactory implements LifecycleAdapterFactory
{
    public LifecycleAdapter create(Object pojoService, JavaComponent component, EntryPointResolverSet resolver, MuleContext muleContext) throws MuleException
    {
        return new DefaultLifecycleAdapter(pojoService, component, resolver, muleContext);
    }
}
