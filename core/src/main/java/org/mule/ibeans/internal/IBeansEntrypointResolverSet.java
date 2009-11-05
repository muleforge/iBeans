/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.internal;

import org.mule.model.resolvers.DefaultEntryPointResolverSet;
import org.mule.model.resolvers.MethodHeaderPropertyEntryPointResolver;
import org.mule.model.resolvers.ReflectionEntryPointResolver;

/**
 * Determins which resolvers are used when invoking a component method
 */
public class IBeansEntrypointResolverSet extends DefaultEntryPointResolverSet
{
    public IBeansEntrypointResolverSet()
    {
        addResolvers(false);
    }

    public IBeansEntrypointResolverSet(boolean synchronizedCalls)
    {
        addResolvers(synchronizedCalls);
    }

    protected void addResolvers(boolean sync)
    {
        MethodHeaderPropertyEntryPointResolver methodHeader = new MethodHeaderPropertyEntryPointResolver();
        methodHeader.setSynchronizeCall(sync);
        addEntryPointResolver(methodHeader);
        ReflectionEntryPointResolver preTransformResolver = new ReflectionEntryPointResolver();
        preTransformResolver.setSynchronizeCall(sync);
        addEntryPointResolver(preTransformResolver);
        ReflectionEntryPointResolver postTransformResolver = new ReflectionEntryPointResolver();
        postTransformResolver.setSynchronizeCall(sync);
        postTransformResolver.setTransformFirst(false);
        addEntryPointResolver(postTransformResolver);
    }
}