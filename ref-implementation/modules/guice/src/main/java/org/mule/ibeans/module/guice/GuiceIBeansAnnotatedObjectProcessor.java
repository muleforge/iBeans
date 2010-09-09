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
import org.mule.api.construct.FlowConstruct;
import org.mule.api.lifecycle.Startable;
import org.mule.api.lifecycle.Stoppable;
import org.mule.api.model.EntryPointResolverSet;
import org.mule.api.model.Model;
import org.mule.api.object.ObjectFactory;
import org.mule.component.DefaultComponentLifecycleAdapter;
import org.mule.component.DefaultComponentLifecycleAdapterFactory;
import org.mule.module.annotationx.config.AnnotatedServiceBuilder;
import org.mule.module.annotationx.parsers.AnnotatedServiceObjectProcessor;

import com.google.inject.Key;

/**
 * TODO
 */
public class GuiceIBeansAnnotatedObjectProcessor extends AnnotatedServiceObjectProcessor
{
    private Key key;

    public GuiceIBeansAnnotatedObjectProcessor(MuleContext muleContext, Key key)
    {
        super();
        setMuleContext(muleContext);
        this.key = key;

    }

    //@Override
    protected AnnotatedServiceBuilder createServiceBuilder(MuleContext muleContext) throws MuleException
    {
        return new GuiceIBeansAnnotatedServiceBuilder(muleContext);
    }

    protected class GuiceIBeansAnnotatedServiceBuilder extends AnnotatedServiceBuilder
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
            model.setLifecycleAdapterFactory(new DefaultComponentLifecycleAdapterFactory()
            {
                @Override
                public LifecycleAdapter create(Object pojoService, JavaComponent component, FlowConstruct flowConstruct, EntryPointResolverSet resolver, MuleContext muleContext) throws MuleException
                {
                    return new DefaultComponentLifecycleAdapter(pojoService, component, flowConstruct, resolver, muleContext)
                    {
                        @Override
                        protected void setLifecycleFlags()
                        {
                            isStartable = Startable.class.isInstance(componentObject);
                            isStoppable = Stoppable.class.isInstance(componentObject);
                            isInitialisable = false; //Handled by the container
                            isDisposable = false; //Handled by the container
                        }
                    };
                }


            });
        }
    }
}
