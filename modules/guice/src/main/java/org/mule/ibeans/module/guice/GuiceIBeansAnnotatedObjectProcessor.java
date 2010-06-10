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
import org.mule.api.MuleException;
import org.mule.api.component.JavaComponent;
import org.mule.api.component.LifecycleAdapter;
import org.mule.api.lifecycle.Startable;
import org.mule.api.lifecycle.Stoppable;
import org.mule.api.model.EntryPointResolverSet;
import org.mule.api.model.Model;
import org.mule.api.object.ObjectFactory;
import org.mule.api.registry.RegistrationException;
import org.mule.component.DefaultLifecycleAdapter;
import org.mule.component.DefaultLifecycleAdapterFactory;
import org.mule.ibeans.internal.MuleiBeansAnnotatedObjectProcessor;
import org.mule.ibeans.internal.MuleiBeansAnnotatedServiceBuilder;
import org.mule.impl.annotations.AnnotatedServiceBuilder;

import com.google.inject.Key;

/**
 * TODO
 */
public class GuiceIBeansAnnotatedObjectProcessor extends MuleiBeansAnnotatedObjectProcessor
{
    private Key key;

    public GuiceIBeansAnnotatedObjectProcessor(MuleContext muleContext, Key key)
    {
        super(muleContext);
        this.key = key;

    }

    @Override
    protected AnnotatedServiceBuilder createServiceBuilder(MuleContext muleContext) throws MuleException
    {
        return new GuiceIBeansAnnotatedServiceBuilder(muleContext);
    }

    protected class GuiceIBeansAnnotatedServiceBuilder extends MuleiBeansAnnotatedServiceBuilder
    {


        public GuiceIBeansAnnotatedServiceBuilder(MuleContext context) throws MuleException
        {
            super(context);
        }

        @Override
        protected ObjectFactory createObjectFactory(Object object)
        {
            return new GuiceObjectFactory(key);
        }

        @Override
        public void setModel(Model model)
        {
            super.setModel(model);
            model.setLifecycleAdapterFactory(new DefaultLifecycleAdapterFactory()
            {
                @Override
                public LifecycleAdapter create(Object pojoService, JavaComponent component, EntryPointResolverSet resolver, MuleContext muleContext) throws MuleException
                {
                    return new DefaultLifecycleAdapter(pojoService, component, resolver, muleContext)
                    {


                        @Override
                        protected void setLifecycleFlags()
                        {
                            isStartable = Startable.class.isInstance(componentObject.get());
                            isStoppable = Stoppable.class.isInstance(componentObject.get());
                            isInitialisable = false; //Handled by the container
                            isDisposable = false; //Handled by the container
                        }

                        @Override
                        protected void registerComponentIfNecessary() throws RegistrationException
                        {
                            // do nothing
                        }
                    };
                }


            });
        }
    }
}
