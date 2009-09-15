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

import org.mule.ibeans.test.AbstractIBeansTestCase;

/**
 * Tests registering an annotated component with the registry programmatically and using
 * property placeholders in the annotations
 */
public class AsyncReplyFunctionalTestCase extends AbstractIBeansTestCase
{
    @Override
    protected void doSetUp() throws Exception
    {
        registerBeans(new BackEnd());
        registerBeans(new ClientProxy());
    }

    public void testReplyTo() throws Exception
    {
        String result = iBeansContext.request("client", String.class, "Ross");
        assertNotNull(result);
        assertEquals("devieceR ssoR", result);
    }
}