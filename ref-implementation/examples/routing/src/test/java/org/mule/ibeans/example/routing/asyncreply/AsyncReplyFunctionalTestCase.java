/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.example.routing.asyncreply;

import org.mule.ibeans.test.IBeansRITestSupport;

import org.ibeans.api.IBeansException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Tests registering an annotated component with the registry programmatically and using
 * property placeholders in the annotations
 */
public class AsyncReplyFunctionalTestCase extends IBeansRITestSupport
{
    @Before
    public void init() throws IBeansException
    {
        registerBeans(new BackEnd());
        registerBeans(new ClientProxy());
    }

    @Test
    public void replyTo() throws Exception
    {
        String result = iBeansContext.request("client", String.class, "Ross");
        assertNotNull(result);
        assertEquals("devieceR ssoR", result);
    }
}