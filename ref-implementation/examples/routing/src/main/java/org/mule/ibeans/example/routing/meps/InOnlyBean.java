/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.example.routing.meps;

import org.mule.ibeans.IBeansContext;
import org.mule.module.annotationx.api.Receive;
import org.mule.tck.functional.FunctionalTestNotification;

import javax.inject.Inject;

/**
 * Receives a message from another party via a subscription channel. No result is expected and any result
 * returned from the service will be ignored.
 */
public class InOnlyBean
{
    @Inject
    private IBeansContext iBeansContext;

    @Receive(uri = "vm://test.in", id = "inbound")
    public void process(String data) throws Exception
    {
        iBeansContext.fireNotification
                (new FunctionalTestNotification(data, FunctionalTestNotification.EVENT_RECEIVED));
    }

}
