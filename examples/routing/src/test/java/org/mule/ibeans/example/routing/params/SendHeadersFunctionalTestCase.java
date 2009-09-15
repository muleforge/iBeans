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
import org.mule.ibeans.test.AbstractIBeansTestCase;

public class SendHeadersFunctionalTestCase extends AbstractIBeansTestCase
{
    protected void doSetUp() throws Exception
    {
        registerBeans(new SendHeadersBean());
    }

    public void testSingleHeader() throws Exception
    {
        MuleMessage message = iBeansContext.request("vm://in", MuleMessage.class, "test");
        assertNotNull(message);
        assertEquals("test Received", message.getPayload());
        assertEquals("value1", message.getProperty("key1"));
    }
}