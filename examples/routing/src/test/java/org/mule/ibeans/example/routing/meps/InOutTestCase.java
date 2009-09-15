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

import org.mule.ibeans.test.AbstractIBeansTestCase;

import java.util.HashMap;
import java.util.Map;

public class InOutTestCase extends AbstractIBeansTestCase
{
    protected void doSetUp() throws Exception
    {
        registerBeans(new InOutBean());
    }

    public void testExchange() throws Exception
    {
        String result = iBeansContext.request("inbound", String.class, "some data");
        assertNotNull(result);
        assertEquals("foo header not received", result);

        Map props = new HashMap();
        props.put("foo", "bar");
        result = iBeansContext.request("inbound", String.class, "some data", props);
        assertNotNull(result);
        assertEquals("foo header received", result);
    }
}