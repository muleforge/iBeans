/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.example.routing.splitter;

import org.mule.ibeans.test.IBeansRITestSupport;
import org.mule.util.IOUtils;

import java.util.ArrayList;
import java.util.List;

import org.ibeans.api.IBeansException;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mule.ibeans.IBeansSupport.selectValue;

/**
 * Tests registering an annotated component with the registry programmatically and using
 * property placeholders in the annotations
 */
public class SplitterFunctionalTestCase extends IBeansRITestSupport
{
    @Before
    public void init() throws IBeansException
    {
        registerBeans(new SplitterBridge());
    }

    @Test
    public void splitter() throws Exception
    {
        String data = IOUtils.getResourceAsString("batch-trades.xml", getClass());

        assertNotNull(data);
        iBeansContext.send("in", data);

        List<String> currencies = new ArrayList<String>(3);
        for (int i = 0; i < 3; i++)
        {
            Document doc = iBeansContext.receive("out", Document.class, 2000);
            if (doc != null)
            {
                currencies.add(selectValue("/Trade/Currency", doc));
            }
        }

        assertEquals(3, currencies.size());
        assertTrue(currencies.contains("USD"));
        assertTrue(currencies.contains("GBP"));
        assertTrue(currencies.contains("EUR"));
    }
}