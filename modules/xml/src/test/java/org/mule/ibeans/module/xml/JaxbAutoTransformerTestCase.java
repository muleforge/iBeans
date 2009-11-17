/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.module.xml;

import org.mule.ibeans.IBeansSupport;
import org.mule.ibeans.test.AbstractIBeansTestCase;

import org.w3c.dom.Document;

public class JaxbAutoTransformerTestCase extends AbstractIBeansTestCase
{
    public static final String HOUSE_XML = "<house><street>Seymore Road</street><windows>12</windows><brick>true</brick></house>";

    public void testCustomTransform() throws Exception
    {
        House house = iBeansContext.transform(HOUSE_XML, House.class);
        assertNotNull(house);
        assertEquals("Seymore Road", house.getStreet());
        assertEquals(12, house.getWindows());
        assertTrue(house.isBrick());

        //and back again
        Document doc = iBeansContext.transform(house, Document.class);
        assertNotNull(doc);
        assertEquals("Seymore Road", IBeansSupport.selectValue("/house/street", doc));
        assertEquals("12", IBeansSupport.selectValue("/house/windows", doc));
        assertEquals("true", IBeansSupport.selectValue("/house/brick", doc));
    }
}