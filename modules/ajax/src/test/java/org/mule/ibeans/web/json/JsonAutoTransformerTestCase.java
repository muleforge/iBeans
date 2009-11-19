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

import org.mule.ibeans.test.AbstractIBeansTestCase;
import org.mule.module.json.JsonData;

public class JsonAutoTransformerTestCase extends AbstractIBeansTestCase
{
    public static final String HOUSE_JSON = "{\"street\":\"Seymore Road\",\"windows\":12,\"brick\":true}";

    public void testCustomTransform() throws Exception
    {
        House house = iBeansContext.transform(HOUSE_JSON, House.class);
        assertNotNull(house);
        assertEquals("Seymore Road", house.getStreet());
        assertEquals(12, house.getWindows());
        assertTrue(house.isBrick());

        //and back again
        String json = iBeansContext.transform(house, String.class);
        assertNotNull(json);
        assertEquals(HOUSE_JSON, json);
        JsonData data = new JsonData(json);
        assertEquals("Seymore Road", data.get("street"));
        assertEquals("12", data.get("windows"));
        assertEquals("true", data.get("brick"));
    }
}