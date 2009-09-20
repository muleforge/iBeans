/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.internal.config;

import org.mule.context.DefaultMuleContextFactory;
import org.mule.api.MuleContext;
import org.mule.api.lifecycle.InitialisationException;
import org.mule.api.context.MuleContextBuilder;
import org.mule.ibeans.IBeansContext;
import org.mule.ibeans.IBeansFactory;

/**
 * TODO
 */
public class IBeansMuleContextFactory extends DefaultMuleContextFactory
{

    @Override
    protected MuleContext doCreateMuleContext(MuleContextBuilder muleContextBuilder) throws InitialisationException
    {
        // Create muleContext instance and set it in MuleServer
        MuleContext muleContext = buildMuleContext(muleContextBuilder);
        //create IBeans
        new IBeansFactory().setMuleContext(muleContext);

        // Initialiase MuleContext
        muleContext.initialise();

        return muleContext;
    }
}
