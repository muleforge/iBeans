/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans;

import org.mule.api.MuleMessage;
import org.mule.ibeans.api.client.IntegrationBean;
import org.mule.ibeans.test.AbstractIBeansTestCase;

public class ParamFactoryTestCase extends AbstractIBeansTestCase
{
    @IntegrationBean
    private TestParamsFactoryIBean testIBean;

    public void testUriParamsOnMethod() throws Exception
    {
        testIBean.init("shhh".getBytes());

        String result = testIBean.doMethodUriParam("secret", new FirstParamFactory());
        assertNotNull(result);
        assertEquals("The key is shhh for secret. Param2 is: 'shhh secret'", result);
    }

    public void testParamsFieldOrdering() throws Exception
    {
        testIBean.init("shhh".getBytes());

        String result = testIBean.doUriParams("secret");
        assertNotNull(result);
        assertEquals("The key is shhh for secret. Param2 is: 'shhh secret'", result);
    }

    public void testHeaderParams() throws Exception
    {
        testIBean.init("shhh".getBytes());

        MuleMessage result = testIBean.doHeaderParam("secret");
        assertNotNull(result);
        assertEquals("Value is: secret", result.getPayloadAsString());
        assertEquals("shhh", result.getProperty("header1"));
        assertEquals("shhh secret", result.getProperty("header2"));
    }

    public void testHeaderParamsOnMethod() throws Exception
    {
        testIBean.init("shhh".getBytes());

        MuleMessage result = testIBean.doMethodHeaderParam("secret", new EchoParamFactory());
        assertNotNull(result);
        assertEquals("Value is: secret", result.getPayloadAsString());
        assertEquals("shhh", result.getProperty("header1"));
        assertEquals("shhh secret", result.getProperty("header2"));
        assertEquals("echoHeader", result.getProperty("echoHeader"));
    }

    public void testPropertyParamsOnMethod() throws Exception
    {
        testIBean.init("shhh".getBytes());

        MuleMessage result = testIBean.doMethodPropertyParam("secret", "hello", new ReversePropertyParamFactory("customProperty"));
        assertNotNull(result);
        assertEquals("Value is: secret", result.getPayloadAsString());
        assertEquals("shhh", result.getProperty("header1"));
        assertEquals("shhh secret", result.getProperty("header2"));
        assertEquals("olleh", result.getProperty("propHeader"));
    }

}
