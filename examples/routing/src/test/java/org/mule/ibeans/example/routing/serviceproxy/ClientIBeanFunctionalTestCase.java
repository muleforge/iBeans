/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.example.routing.serviceproxy;

import org.mule.api.MuleException;
import org.mule.ibeans.api.client.CallException;
import org.mule.ibeans.test.AbstractIBeansTestCase;


/**
 * Tests registering an annotated component with the registry programmatically and using
 * property placeholders in the annotations
 */
public class ClientIBeanFunctionalTestCase extends AbstractIBeansTestCase
{
    protected void doSetUp() throws Exception
    {
        registerBeans(new WebSearchBean());
    }

    public void testRequestResponse() throws Exception
    {
        String result = iBeansContext.request("vm://find", String.class, new Object[]{"ross", SearchEngine.ASK});

        //Try ask
        assertNotNull(result);
        assertTrue(result.indexOf("ross - Ask.com Search") > -1);

        //Try Yahoo
        result = iBeansContext.request("vm://find", String.class, new Object[]{"ross", SearchEngine.YAHOO});
        assertNotNull(result);
        assertTrue(result.indexOf("ross - Yahoo! Search Results") > -1);

        //Will return a 404, We have configured the Google search with the wrong URI
        try
        {
            iBeansContext.request("vm://find", String.class, new Object[]{"ross", SearchEngine.GOOGLE});
            fail("An exception should have been thrown");
        }
        catch (MuleException e)
        {
            assertTrue(e.getCause() instanceof CallException);
            assertEquals("404", ((CallException) e.getCause()).getErrorCode());
        }
    }
}