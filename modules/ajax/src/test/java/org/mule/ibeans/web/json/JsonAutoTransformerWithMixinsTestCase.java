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
import org.mule.module.json.JsonData;
import org.mule.tck.testmodels.fruit.Apple;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

//TODO: IBEANS-141. No support for Mixin resolution yet
public class JsonAutoTransformerWithMixinsTestCase extends IBeansTestSupport
{
    public static final String APPLE_JSON = "{\"washed\":false,\"bitten\":true}";

    @Before
    public void init() throws Exception
    {
        //We don't register a custom transformer, instead we register a 'global' mapper that will
        //be used for Json transforms
        ObjectMapper mapper = new ObjectMapper();
        mapper.getSerializationConfig().addMixInAnnotations(Apple.class, AppleMixin.class);
        mapper.getDeserializationConfig().addMixInAnnotations(Apple.class, AppleMixin.class);
        registerBeans(mapper);
    }

    @Test
    public void testCustomTransform() throws Exception
    {
        //Though the data is simple we are testing two things -
        //1) Mixins are recognised by the Transformer resolver
        //2) that we successfully marshal and marshal an object that is not annotated directly
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