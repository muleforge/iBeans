/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.module.guice;

import org.mule.api.MuleContext;
import org.mule.api.context.MuleContextAware;
import org.mule.api.lifecycle.InitialisationException;
import org.mule.object.AbstractObjectFactory;

import com.google.inject.Injector;
import com.google.inject.Key;

import javax.inject.Singleton;

/**
 * TODO
 */
public class GuiceObjectFactory extends AbstractObjectFactory
{
    private Injector injector;

    private Key<?> key;

    public GuiceObjectFactory(Injector injector, Key<?> key)
    {
        this.injector = injector;
        this.key = key;
    }

    @Override
    public void initialise() throws InitialisationException
    {

    }

    @Override
    public void dispose()
    {

    }

    @Override
    public Class<?> getObjectClass()
    {
        return key.getTypeLiteral().getRawType();
    }

    @Override
    public Object getInstance(MuleContext muleContext) throws Exception
    {
        Object instance = injector.getProvider(key).get();

        //Backward compatability injection support
        if(instance instanceof MuleContextAware && !isSingleton())
        {
            ((MuleContextAware)instance).setMuleContext(muleContext);
        }
        fireInitialisationCallbacks(instance);
        return instance;
    }

    @Override
    public boolean isSingleton()
    {
        return key.getTypeLiteral().getRawType().isAnnotationPresent(Singleton.class);
    }

    @Override
    public boolean isExternallyManagedLifecycle()
    {
        //Guice does not manage lifecycle
        return false;
    }

    public boolean isAutoWireObject()
    {
        //Guice does the wiring
        return false;
    }
}
