/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.module.xml.expressions;

import org.mule.ibeans.IBeansException;
import org.mule.ibeans.api.client.IntegrationBean;
import org.mule.ibeans.test.IBeansTestSupport;
import org.mule.ibeans.transformers.PrimitveTransformers;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ReturnAnnotationTestCase extends IBeansTestSupport
{
    @IntegrationBean
    private ReturnExpressionsIBean test;

    @Before
    public void init() throws IBeansException
    {
        registerBeans(new PrimitveTransformers());
    }

    protected ReturnExpressionsIBean getTestIBean()
    {
        return test;
    }

    @Test
    public void booleanReturn() throws Exception
    {
        boolean result = getTestIBean().testBooleanReturn();
        assertTrue(result);
    }

    @Test
    public void stringReturn() throws Exception
    {
        String result = getTestIBean().testStringReturn();
        assertEquals("true", result);
    }

    @Test
    public void numberReturn() throws Exception
    {
        Integer result = getTestIBean().testNumberReturn();
        assertEquals(new Integer(14), result);
    }

    @Test
    public void domXmlReturn() throws Exception
    {
        Document result = getTestIBean().testDomReturn();
        assertEquals("bar", result.getDocumentElement().getNodeName());
        assertEquals("true", result.getDocumentElement().getTextContent());
    }
}
