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
import org.mule.api.construct.FlowConstruct;
import org.mule.api.lifecycle.Disposable;
import org.mule.api.lifecycle.Initialisable;
import org.mule.api.lifecycle.InitialisationException;
import org.mule.api.lifecycle.Startable;
import org.mule.api.lifecycle.Stoppable;
import org.mule.api.model.EntryPointResolverSet;
import org.mule.component.DefaultComponentLifecycleAdapter;

/**
 * TODO
 */
public class IBeansComponentLifecycleAdapter extends DefaultComponentLifecycleAdapter
{
    public IBeansComponentLifecycleAdapter(Object componentObject, JavaComponent component, FlowConstruct flowConstruct, MuleContext muleContext)
            throws MuleException
    {
        super(componentObject, component, flowConstruct, muleContext);
    }

    public IBeansComponentLifecycleAdapter(Object componentObject, JavaComponent component, FlowConstruct flowConstruct, EntryPointResolverSet entryPointResolver, MuleContext muleContext)
            throws MuleException
    {
        super(componentObject, component, flowConstruct, entryPointResolver, muleContext);
    }

    public boolean isStarted()
    {
        return muleContext.getLifecycleManager().isPhaseComplete(Startable.PHASE_NAME);
    }

    public boolean isDisposed()
    {
        return muleContext.getLifecycleManager().isPhaseComplete(Disposable.PHASE_NAME);

    }


    public void initialise() throws InitialisationException
    {
        try
        {
            muleContext.getRegistry().applyLifecycle(this.componentObject, Initialisable.PHASE_NAME);
        }
        catch (MuleException e)
        {
            throw new InitialisationException(e, this);
        }
    }

    public void start() throws MuleException
    {
         muleContext.getRegistry().applyLifecycle(this.componentObject, Startable.PHASE_NAME);
    }

    public void stop() throws MuleException
    {
        muleContext.getRegistry().applyLifecycle(this.componentObject, Stoppable.PHASE_NAME);
    }

    public void dispose()
    {
        try
        {
            muleContext.getRegistry().applyLifecycle(this.componentObject, Disposable.PHASE_NAME);
        }
        catch (MuleException e)
        {
            logger.warn("Failed to dispose component: " + e.getMessage());
        }
    }
}
