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

import org.mule.api.model.EntryPointResolver;
import org.mule.model.resolvers.LegacyEntryPointResolverSet;
import org.mule.model.resolvers.ReflectionEntryPointResolver;

import java.util.HashSet;

/**
 * Determines which resolvers are used when invoking a component method
 */
public class IBeansEntrypointResolverSet extends LegacyEntryPointResolverSet
{
    public IBeansEntrypointResolverSet()
    {
        this(false);
    }

    public IBeansEntrypointResolverSet(boolean synchronizedCalls)
    {
        super();
        setEntryPointResolvers(new HashSet<EntryPointResolver>());
        IBeansMethodHeaderPropertyEntryPointResolver methodHeader = new IBeansMethodHeaderPropertyEntryPointResolver();
        methodHeader.setSynchronizeCall(synchronizedCalls);
        addEntryPointResolver(methodHeader);
        addAnnotatedEntryPointResolver();
        //Do we really need this one ofr iBeans
        addEntryPointResolver(new ReflectionEntryPointResolver());
    }

}