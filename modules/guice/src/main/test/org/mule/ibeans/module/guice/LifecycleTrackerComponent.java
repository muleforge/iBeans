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
import org.mule.api.MuleEventContext;
import org.mule.api.lifecycle.Callable;
import org.mule.api.service.Service;
import org.mule.api.service.ServiceAware;
import org.mule.registry.AbstractLifecycleTracker;

/**
 * TODO
 */
public class LifecycleTrackerComponent extends AbstractLifecycleTracker
        implements ServiceAware, Callable
{

     public void setService(final Service service)
    {
        getTracker().add("setService");
    }

    public Object onCall(final MuleEventContext eventContext) throws Exception {
        // dirty trick to get the component instance that was used for the
        // request
        return this;
    }

    @Override
    public void setMuleContext(MuleContext context)
    {
        super.setMuleContext(context);    //To change body of overridden methods use File | Settings | File Templates.
    }
}
