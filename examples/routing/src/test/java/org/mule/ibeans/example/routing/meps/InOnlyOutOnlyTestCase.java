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

public class InOnlyOutOnlyTestCase extends AbstractIBeansTestCase
{
    public static final int TIMEOUT = 3000;

    protected void doSetUp() throws Exception
    {
        registerBeans(new InOnlyOutOnlyBean());
    }

    public void testExchange() throws Exception
    {
        iBeansContext.send("inbound", "some data");

        String result = iBeansContext.receive("outbound", String.class, TIMEOUT);
        assertNull(result);

        Map<String, String> props = new HashMap<String, String>();
        props.put("foo", "bar");
        iBeansContext.send("inbound", "some data", props);

        result = iBeansContext.receive("outbound", String.class, TIMEOUT);
        assertNotNull(result);
        assertEquals("foo header received", result);
    }
}