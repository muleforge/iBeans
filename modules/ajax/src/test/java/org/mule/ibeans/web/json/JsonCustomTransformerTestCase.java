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

import org.mule.DefaultMuleMessage;
import org.mule.api.MuleMessage;
import org.mule.ibeans.test.AbstractIBeansTestCase;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

public class JsonCustomTransformerTestCase extends AbstractIBeansTestCase
{
    public static final String CAR_JSON = "{\"features\":[{\"code\":\"1234\",\"description\":\"Electric motor\"},{\"code\":\"0194\",\"description\":\"Electric windows\"}],\"make\":\"Toyota\",\"model\":\"Prius\"}";
    public static final String FEATURE_JSON = "{\"code\":\"1234\",\"description\":\"Electric motor\"}";

    @Override
    protected void doSetUp() throws Exception
    {
        registerBeans(new JsonCustomTransformer());
    }

    public void testCustomTransform() throws Exception
    {
//        Car car = new Car("Toyota", "Prius");
//        car.getFeatures().add(new Feature("1234", "Electric motor"));
//        car.getFeatures().add(new Feature("0194", "Electric windows"));
//        String result = iBeansContext.transform(car, String.class);
//        System.out.println(result);

        Car car = iBeansContext.transform(CAR_JSON, Car.class);
        assertNotNull(car);
        assertEquals("Toyota", car.getMake());
        assertEquals("Prius", car.getModel());
        assertEquals(2, car.getFeatures().size());
        assertEquals("1234", car.getFeatures().get(0).getCode());
        assertEquals("Electric motor", car.getFeatures().get(0).getDescription());
        assertEquals("0194", car.getFeatures().get(1).getCode());
        assertEquals("Electric windows", car.getFeatures().get(1).getDescription());
    }

    public void testCustomTransformWithMuleMessage() throws Exception
    {
        ByteArrayInputStream in = new ByteArrayInputStream(FEATURE_JSON.getBytes());
        Map props = new HashMap();
        props.put("foo", "fooValue");
        MuleMessage msg = new DefaultMuleMessage(in, props, muleContext);
        Feature feature = iBeansContext.transform(msg, Feature.class);
        assertNotNull(feature);
        assertEquals("1234", feature.getCode());
        assertEquals("Electric motor", feature.getDescription());
    }
}