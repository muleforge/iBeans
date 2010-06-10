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

import org.mule.api.MuleEventContext;
import org.mule.ibeans.api.application.ReceiveAndReply;
import org.mule.ibeans.api.application.Service;
import org.mule.impl.annotations.ObjectScope;

import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * TODO
 */
@Service(maxAsyncThreads = 3,scope = ObjectScope.POOLED, name = "MulePooledSingletonService")
public class PooledService extends LifecycleTrackerComponent
{

    @ReceiveAndReply(uri = "vm://MulePooledSingletonService.In")
    @Override
    public Object onCall(MuleEventContext eventContext) throws Exception
    {
        return super.onCall(eventContext);
    }

    @Override
    @Inject
    public void setProperty(@Named("mmps-value") String value)
    {
        super.setProperty(value);
    }
}