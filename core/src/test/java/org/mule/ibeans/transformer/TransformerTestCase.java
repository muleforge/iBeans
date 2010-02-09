/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.transformer;

import org.mule.ibeans.test.AbstractIBeansTestCase;
import org.mule.tck.testmodels.fruit.Banana;

/**
 * Test that the {@link FruitTransformers} is loaded into the iBeans container and is used
 * by this test case
 */
public class TransformerTestCase extends AbstractIBeansTestCase
{
    @Override
    protected void doSetUp() throws Exception
    {
        registerBeans(new FruitBiter());
        //The test does not discover annotations on the class path, so you need to register all annotated objects
        registerBeans(new FruitTransformers());
    }

    public void testCustomTransform() throws Exception
    {
        Banana banana = iBeansContext.request("vm://test", Banana.class, "banana");
        assertNotNull(banana);
        assertTrue(banana.isBitten());
    }
}
