/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.module.xml.expressions;

import org.mule.ibeans.api.client.MockIntegrationBean;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * Test that the Return annotation works with a Mock on on a call method
 */
public class MockReturnAnnotationTestCase extends ReturnAnnotationTestCase
{
    @MockIntegrationBean
    private ReturnExpressionsIBean test;

    @Override
    protected ReturnExpressionsIBean getTestIBean()
    {
        return test;
    }


    @Test
    public void testCallWithReturnAnnotation() throws Exception
    {
        Mockito.when(test.getSomeValue()).thenAnswer(withXmlData("test-return-data.xml", test));

        String result = test.getSomeValue();
        Assert.assertEquals("baz", result);
    }

}
