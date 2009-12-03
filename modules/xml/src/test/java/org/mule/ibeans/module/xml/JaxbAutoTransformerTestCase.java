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
import org.mule.ibeans.module.xml.model.Item;
import org.mule.ibeans.test.AbstractIBeansTestCase;

import org.w3c.dom.Document;

public class JaxbAutoTransformerTestCase extends AbstractIBeansTestCase
{
    public static final String ITEM_XML = "<item><code>1234</code><description>Vacuum Cleaner</description><in-stock>true</in-stock></item>";

    public void testCustomTransform() throws Exception
    {
        Item item = iBeansContext.transform(ITEM_XML, Item.class);
        assertNotNull(item);
        assertEquals("1234", item.getCode());
        assertEquals("Vacuum Cleaner", item.getDescription());
        assertTrue(item.isInStock());

        //and back again
        Document doc = iBeansContext.transform(item, Document.class);
        assertNotNull(doc);
        assertEquals("1234", IBeansSupport.selectValue("/item/code", doc));
        assertEquals("Vacuum Cleaner", IBeansSupport.selectValue("/item/description", doc));
        assertEquals("true", IBeansSupport.selectValue("/item/in-stock", doc));
    }
}