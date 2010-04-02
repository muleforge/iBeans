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
import org.mule.ibeans.api.application.BeanConfig;
import org.mule.ibeans.api.application.ReceiveAndReply;

/**
 * TODO
 */
@BeanConfig(name = "MulePrototypeService")
public class PrototypeService extends LifecycleTrackerComponent
{
    public PrototypeService()
    {
        System.out.println("");
    }

    @Override
    @ReceiveAndReply(uri = "vm://MulePrototypeService.In")
    public Object onCall(MuleEventContext eventContext) throws Exception
    {
        return super.onCall(eventContext);
    }
}