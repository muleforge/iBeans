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
import org.mule.ibeans.test.AbstractIBeansTestCase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessagePropertiesFunctionalTestCase extends AbstractIBeansTestCase
{
    private Map<String, Object> props;

    @Override
    protected void doSetUp() throws Exception
    {
        registerBeans(new HeadersBean());

        props = new HashMap<String, Object>(3);
        props.put("foo", "fooValue");
        props.put("bar", "barValue");
        props.put("baz", "bazValue");
    }

    public void testSingleHeader() throws Exception
    {
        String message = iBeansContext.request("vm://header", String.class, "test", props);
        assertNotNull(message);
        assertEquals("fooValue", message);
    }

    public void testMapHeaders() throws Exception
    {
        Map result = iBeansContext.request("vm://headers", Map.class, "test", props);
        assertEquals(2, result.size());
        assertEquals("fooValue", result.get("foo"));
        assertEquals("barValue", result.get("bar"));
        assertNull(result.get("baz"));
    }

    public void testListHeaders() throws Exception
    {
        List result = iBeansContext.request("vm://headersList", List.class, "test", props);
        assertEquals(3, result.size());
        assertTrue(result.contains("fooValue"));
        assertTrue(result.contains("barValue"));
        assertTrue(result.contains("bazValue"));
    }

    public void testListHeadersWithOptionalHeaderMissing() throws Exception
    {
        props.remove("baz");
        List result = iBeansContext.request("vm://headersList", List.class, "test", props);
        assertEquals(2, result.size());
        assertTrue(result.contains("fooValue"));
        assertTrue(result.contains("barValue"));
    }

    public void testListHeadersWithMissingValue() throws Exception
    {
        props.remove("bar");

        try
        {
            iBeansContext.request("vm://headersList", List.class, "test", props);
            fail("request should have failed because a required header was missing");
        }
        catch (MuleException e)
        {
            //Expected
        }
    }
}