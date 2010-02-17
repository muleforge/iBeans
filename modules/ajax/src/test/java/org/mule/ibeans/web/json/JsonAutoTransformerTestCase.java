/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.web.json;

import org.mule.ibeans.test.IBeansTestSupport;
import org.mule.ibeans.web.json.model.Item;
import org.mule.module.json.JsonData;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class JsonAutoTransformerTestCase extends IBeansTestSupport
{
    public static final String ITEM_JSON = "{\"code\":\"1234\",\"description\":\"Vacuum Cleaner\",\"in-stock\":true}";

    @Test
    public void testCustomTransform() throws Exception
    {
        Item item = iBeansContext.transform(ITEM_JSON, Item.class);
        assertNotNull(item);
        assertEquals("1234", item.getCode());
        assertEquals("Vacuum Cleaner", item.getDescription());
        assertTrue(item.isInStock());

        //and back again
        String json = iBeansContext.transform(item, String.class);
        assertNotNull(json);
        assertEquals(ITEM_JSON, json);
        JsonData data = new JsonData(json);
        assertEquals("1234", data.get("code"));
        assertEquals("Vacuum Cleaner", data.get("description"));
        assertEquals("true", data.get("in-stock"));
    }
}