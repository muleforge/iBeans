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

import org.mule.DefaultMuleMessage;
import org.mule.api.MuleMessage;
import org.mule.ibeans.test.AbstractIBeansTestCase;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

public class JaxbTransformerTestCase extends AbstractIBeansTestCase
{
    public static final String CAR_XML = "<car><make>Toyota</make><model>Prius</model><features><feature><code>1234</code><description>Electric motor</description></feature><feature><code>0194</code><description>Electric windows</description></feature></features></car>";
    public static final String FEATURE_XML = "<feature><code>1234</code><description>Electric motor</description></feature>";

    @Override
    protected void doSetUp() throws Exception
    {
        registerBeans(new JAXBTransformer());
    }

    public void testCustomTransform() throws Exception
    {
        Car car = iBeansContext.transform(CAR_XML, Car.class);
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
        ByteArrayInputStream in = new ByteArrayInputStream(FEATURE_XML.getBytes());
        Map props = new HashMap();
        props.put("foo", "fooValue");
        MuleMessage msg = new DefaultMuleMessage(in, props, muleContext);
        Feature feature = iBeansContext.transform(msg, Feature.class);
        assertNotNull(feature);
        assertEquals("1234", feature.getCode());
        assertEquals("Electric motor", feature.getDescription());
    }
}