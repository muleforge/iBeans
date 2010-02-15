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

import org.mule.ibeans.api.client.IntegrationBean;
import org.mule.ibeans.test.AbstractIBeansTestCase;
import org.mule.ibeans.transformers.PrimitveTransformers;

import org.w3c.dom.Document;

public class ReturnAnnotationTestCase extends AbstractIBeansTestCase
{
    @IntegrationBean
    private ReturnExpressionsIBean test;

    @Override
    protected void doSetUp() throws Exception
    {
        registerBeans(new PrimitveTransformers());
    }

    protected ReturnExpressionsIBean getTestIBean()
    {
        return test;
    }

    public void testBooleanReturn() throws Exception
    {
        boolean result = getTestIBean().testBooleanReturn();
        assertTrue(result);
    }

    public void testStringReturn() throws Exception
    {
        String result = getTestIBean().testStringReturn();
        assertEquals("true", result);
    }

    public void testNumberReturn() throws Exception
    {
        Integer result = getTestIBean().testNumberReturn();
        assertEquals(new Integer(14), result);
    }

    public void testDomXmlReturn() throws Exception
    {
        Document result = getTestIBean().testDomReturn();
        assertEquals("bar", result.getDocumentElement().getNodeName());
        assertEquals("true", result.getDocumentElement().getTextContent());
    }
}
