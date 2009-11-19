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
import org.mule.tck.testmodels.fruit.Apple;

public class JsonCustomTransformerWithMixinsTestCase extends AbstractIBeansTestCase
{
    public static final String APPLE_JSON = "{\"washed\":false,\"bitten\":true}";

    @Override
    protected void doSetUp() throws Exception
    {
        registerBeans(new JsonCustomTransformerWithMixins());
    }

    public void testCustomTransform() throws Exception
    {
        //THough the data is simple we are testing two things -
        //1) Mixins are recognised by the Transformer resolver
        //2) that we succesfully mashal and marshal an object that is not annotated directly
        Apple apple = iBeansContext.transform(APPLE_JSON, Apple.class);
        assertNotNull(apple);
        assertFalse(apple.isWashed());
        assertTrue(apple.isBitten());

        String json = iBeansContext.transform(apple, String.class);
        assertNotNull(json);
        JsonData data = new JsonData(json);
        assertEquals("true", data.get("bitten"));
        assertEquals("false", data.get("washed"));
    }
}