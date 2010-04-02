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
import org.mule.api.object.ObjectFactory;
import org.mule.ibeans.internal.MuleiBeansAnnotatedObjectProcessor;
import org.mule.ibeans.internal.MuleiBeansAnnotatedServiceBuilder;
import org.mule.impl.annotations.AnnotatedServiceBuilder;

import com.google.inject.Injector;
import com.google.inject.Key;

/**
 * TODO
 */
public class GuiceIBeansAnnotatedObjectProcessor extends MuleiBeansAnnotatedObjectProcessor
{
    private Injector injector;
    private Key key;

    public GuiceIBeansAnnotatedObjectProcessor(MuleContext muleContext, Injector injector, Key key)
    {
        super(muleContext);
        this.injector = injector;
        this.key = key;
    }

    @Override
    protected AnnotatedServiceBuilder createServiceBuilder(MuleContext muleContext) throws MuleException
    {
        return new GuiceIBeansAnnotatedServiceBuilder(muleContext, injector, key);
    }

    protected class GuiceIBeansAnnotatedServiceBuilder extends MuleiBeansAnnotatedServiceBuilder
    {
        private Injector injector;
        private Key key;

        public GuiceIBeansAnnotatedServiceBuilder(MuleContext context, Injector injector, Key key) throws MuleException
        {
            super(context);
            this.injector = injector;
            this.key = key;
        }

        @Override
        protected ObjectFactory createObjectFactory(Object object)
        {
            return new GuiceObjectFactory(injector, key);
        }
    }
}
