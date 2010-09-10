/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.example.routing.params;

import org.mule.api.MuleMessage;
import org.mule.ibeans.test.IBeansRITestSupport;

import org.ibeans.api.IBeansException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class SendHeadersFunctionalTestCase extends IBeansRITestSupport
{
    @Before
    public void init() throws IBeansException
    {
        registerBeans(new SendHeadersBean());
    }

    @Test
    public void singleHeader() throws Exception
    {
        MuleMessage message = iBeansContext.request("vm://in", MuleMessage.class, "test");
        assertNotNull(message);
        assertEquals("test Received", message.getPayload());
        assertEquals("value1", message.getProperty("key1"));
    }
}