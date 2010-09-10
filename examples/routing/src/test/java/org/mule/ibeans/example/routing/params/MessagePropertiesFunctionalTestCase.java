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

import org.mule.api.MuleException;
import org.mule.ibeans.test.IBeansRITestSupport;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ibeans.api.IBeansException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class MessagePropertiesFunctionalTestCase extends IBeansRITestSupport
{

    private Map<String, Object> props;

    @Before
    public void init() throws IBeansException
    {
        registerBeans(new HeadersBean());

        props = new HashMap<String, Object>(3);
        props.put("foo", "fooValue");
        props.put("bar", "barValue");
        props.put("baz", "bazValue");
    }

    @Test
    public void singleHeader() throws Exception
    {
        String message = iBeansContext.request("vm://header", String.class, "test", props);
        assertNotNull(message);
        assertEquals("fooValue", message);
    }

    @Test
    public void mapHeaders() throws Exception
    {
        Map result = iBeansContext.request("vm://headers", Map.class, "test", props);
        assertEquals(2, result.size());
        assertEquals("fooValue", result.get("foo"));
        assertEquals("barValue", result.get("bar"));
        assertNull(result.get("baz"));
    }

    @Test
    public void listHeaders() throws Exception
    {
        List result = iBeansContext.request("vm://headersList", List.class, "test", props);
        assertEquals(3, result.size());
        assertTrue(result.contains("fooValue"));
        assertTrue(result.contains("barValue"));
        assertTrue(result.contains("bazValue"));
    }

    @Test
    public void listHeadersWithOptionalHeaderMissing() throws Exception
    {
        props.remove("baz");
        List result = iBeansContext.request("vm://headersList", List.class, "test", props);
        assertEquals(2, result.size());
        assertTrue(result.contains("fooValue"));
        assertTrue(result.contains("barValue"));
    }

    @Test(expected = MuleException.class)
    public void listHeadersWithMissingValue() throws Exception
    {
        props.remove("bar");
        iBeansContext.request("vm://headersList", List.class, "test", props);
        fail("request should have failed because a required header was missing");
    }
}