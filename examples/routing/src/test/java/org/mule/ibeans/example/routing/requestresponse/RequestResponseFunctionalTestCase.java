/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.example.routing.requestresponse;

import org.mule.ibeans.IBeansSupport;
import org.mule.ibeans.test.AbstractIBeansTestCase;

import org.w3c.dom.Document;


/**
 * Tests registering an annotated component with the registry programmatically and using
 * property placeholders in the annotations
 */
public class RequestResponseFunctionalTestCase extends AbstractIBeansTestCase
{
    protected void doSetUp() throws Exception
    {
        registerBeans(new SimpleProcessingBean());
    }

    public void testRequestResponse() throws Exception
    {
        Document result = iBeansContext.request("vm://test", Document.class, "<foo><bar></bar></foo>");
        assertNotNull(result);
        assertEquals("hello", IBeansSupport.selectOne("/foo/bar", result).getTextContent());
    }
}