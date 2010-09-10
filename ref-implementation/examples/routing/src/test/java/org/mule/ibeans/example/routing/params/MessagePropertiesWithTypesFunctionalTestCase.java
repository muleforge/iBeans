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
import org.mule.tck.testmodels.fruit.Apple;
import org.mule.tck.testmodels.fruit.Banana;
import org.mule.tck.testmodels.fruit.Orange;

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

public class MessagePropertiesWithTypesFunctionalTestCase extends IBeansRITestSupport
{
    private Map<String, Object> props;

    @Before
    public void init() throws IBeansException
    {
        registerBeans(new HeadersWithTypeBean());

        props = new HashMap<String, Object>(3);
        props.put("apple", new Apple());
        props.put("banana", new Banana());
        props.put("orange", new Orange());
    }

    @Test
    public void singleHeader() throws Exception
    {
        Banana result = iBeansContext.request("header", Banana.class, "test", props);
        assertNotNull(result);
        assertEquals(new Banana(), result);
    }

    @Test
    public void mapHeaders() throws Exception
    {
        Map result = iBeansContext.request("headers", Map.class, "test", props);
        assertEquals(2, result.size());
        assertEquals(new Apple(), result.get("apple"));
        assertEquals(new Orange(), result.get("orange"));
        assertNull(result.get("banana"));
    }

    @Test
    public void listHeaders() throws Exception
    {
        List result = iBeansContext.request("headersList", List.class, "test", props);
        assertNotNull(result);
        assertEquals(3, result.size());
        assertTrue(result.contains(new Apple()));
        assertTrue(result.contains(new Banana()));
        assertTrue(result.contains(new Orange()));
    }

    @Test
    public void listHeadersWithOptionalHeaderMissing() throws Exception
    {
        props.remove("orange");
        List result = iBeansContext.request("headersList", List.class, "test", props);
        assertEquals(2, result.size());
        assertTrue(result.contains(new Apple()));
        assertTrue(result.contains(new Banana()));
    }

    @Test(expected = MuleException.class)
    public void testListHeadersWithMissingValue() throws Exception
    {
        props.remove("apple");
        iBeansContext.request("headersList", List.class, "test", props);
        fail("required value was missing and an exception should hve been thrown");
    }

    @Test
    public void listHeadersAll() throws Exception
    {
        List result = iBeansContext.request("headersAllList", List.class, "test", props);
        assertTrue(result.contains(new Apple()));
        assertTrue(result.contains(new Banana()));
        assertTrue(result.contains(new Orange()));
    }

    @Test
    public void mapHeadersAll() throws Exception
    {
        Map result = iBeansContext.request("headersAll", Map.class, "test", props);
        assertEquals(new Apple(), result.get("apple"));
        assertEquals(new Orange(), result.get("orange"));
        assertEquals(new Banana(), result.get("banana"));
    }
}