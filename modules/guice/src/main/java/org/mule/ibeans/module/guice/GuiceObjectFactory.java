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
import org.mule.api.lifecycle.InitialisationException;
import org.mule.api.registry.RegistrationException;
import org.mule.api.service.ServiceAware;
import org.mule.object.AbstractObjectFactory;

import com.google.inject.Injector;
import com.google.inject.Key;

/**
 * TODO
 */
public class GuiceObjectFactory extends AbstractObjectFactory
{
    private Key<?> key;
    private Injector injector;

    public GuiceObjectFactory(Key<?> key)
    {
        this.key = key;
    }

    @Override
    public void initialise() throws InitialisationException
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

        Object instance = getInjector(muleContext).getInstance(key);

        //TODO need a better way to handle this
        //There is no way around needing to inject the service like this since we need to associate
        //the component with the service when it is created.  This happens outside of any DI container
        if(instance instanceof ServiceAware)
        {
            ((ServiceAware)instance).setService(service);
        }
        return instance;

    }

    protected Injector getInjector(MuleContext muleContext) throws RegistrationException
    {
        if(injector==null)
        {
            injector = muleContext.getRegistry().lookupObject(Injector.class);
        }
        return injector;
    }

    @Override
    public boolean isSingleton()
    {
        //TODO how to find out the scope with the key
        return true;
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
