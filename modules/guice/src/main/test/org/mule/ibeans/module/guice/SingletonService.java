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
import org.mule.module.annotationx.api.ReceiveAndReply;
import org.mule.module.annotationx.api.Service;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import javax.inject.Singleton;

/**
 * TODO
 */
@Singleton
@Service(name = "MuleSingletonService")
public class SingletonService extends LifecycleTrackerComponent
{
    public SingletonService()
    {
        System.out.println("");
    }

    @Override
    @ReceiveAndReply(uri = "vm://MuleSingletonService.In")
    public Object onCall(MuleEventContext eventContext) throws Exception
    {
        return super.onCall(eventContext);
    }

    @Override
    @Inject
    public void setProperty(@Named("mms-value") String value)
    {
        super.setProperty(value);
    }
}
