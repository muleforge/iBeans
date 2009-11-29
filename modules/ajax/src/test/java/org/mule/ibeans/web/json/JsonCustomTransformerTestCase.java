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
import org.mule.transformer.types.CollectionDataType;
import org.mule.transformer.types.ListDataType;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonCustomTransformerTestCase extends AbstractIBeansTestCase
{
    public static final String CAR_JSON = "{\"features\":[{\"code\":\"1234\",\"description\":\"Electric motor\"},{\"code\":\"0194\",\"description\":\"Electric windows\"}],\"make\":\"Toyota\",\"model\":\"Prius\"}";
    public static final String FEATURE_JSON = "{\"code\":\"1234\",\"description\":\"Electric motor\"}";
    public static final String FEATURES_JSON = "[{\"code\":\"1234\",\"description\":\"Electric motor\"},{\"code\":\"0194\",\"description\":\"Electric windows\"}]";

    @Override
    protected void doSetUp() throws Exception
    {
        registerBeans(new JsonCustomTransformer());
    }

    public void testCustomTransform() throws Exception
    {
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

    public void testCustomListTransform() throws Exception
    {
        List<Feature> features = iBeansContext.transform(FEATURES_JSON, new CollectionDataType<List<Feature>>(List.class, Feature.class));
        assertNotNull(features);
        assertEquals("1234", features.get(0).getCode());
        assertEquals("Electric motor", features.get(0).getDescription());
        assertEquals("0194", features.get(1).getCode());
        assertEquals("Electric windows", features.get(1).getDescription());


        String cars_json = "[" + CAR_JSON + "," + CAR_JSON + "]";
        List<Car> cars = iBeansContext.transform(cars_json, new CollectionDataType<List<Car>>(List.class, Car.class));
        assertNotNull(cars);
        assertEquals(2, cars.size());
    }

    public void testDifferentListTransformer() throws Exception
    {
        //Test that we can resolve other collections

        String cars_json = "[" + CAR_JSON + "," + CAR_JSON + "]";
        List<Car> cars = iBeansContext.transform(cars_json, new ListDataType<List<Car>>(Car.class));
        assertNotNull(cars);
        assertEquals(2, cars.size());
    }
}