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
import org.mule.ibeans.test.IBeansRITestSupport;

import org.ibeans.api.IBeansException;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


/**
 * Tests registering an annotated component with the registry programmatically and using
 * property placeholders in the annotations
 */
public class RequestResponseFunctionalTestCase extends IBeansRITestSupport
{
    @Before
    public void init() throws IBeansException
    {
        registerBeans(new SimpleProcessingBean());
    }

    @Test
    public void requestResponse() throws Exception
    {
        Document result = iBeansContext.request("vm://test", Document.class, "<foo><bar></bar></foo>");
        assertNotNull(result);
        assertEquals("hello", IBeansSupport.selectOne("/foo/bar", result).getTextContent());
    }
}