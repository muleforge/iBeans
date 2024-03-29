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
import org.mule.ibeans.test.IBeansRITestSupport;

import org.ibeans.annotation.IntegrationBean;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ParamFactoryTestCase extends IBeansRITestSupport
{
    @IntegrationBean
    private TestParamsFactoryIBean testIBean;

    @Test
    public void uriParamsOnMethod() throws Exception
    {
        testIBean.init("shhh".getBytes());

        String result = testIBean.doMethodUriParam("secret", new FirstParamFactory());
        assertNotNull(result);
        assertEquals("The key is shhh for secret. Param2 is: 'shhh secret'", result);
    }

    @Test
    public void paramsFieldOrdering() throws Exception
    {
        testIBean.init("shhh".getBytes());

        String result = testIBean.doUriParams("secret");
        assertNotNull(result);
        assertEquals("The key is shhh for secret. Param2 is: 'shhh secret'", result);
    }

    @Test
    public void headerParams() throws Exception
    {
        testIBean.init("shhh".getBytes());

        MuleMessage result = testIBean.doHeaderParam("secret");
        assertNotNull(result);
        assertEquals("Value is: secret", result.getPayloadAsString());
        assertEquals("shhh", result.getInboundProperty("header1"));
        assertEquals("shhh secret", result.getInboundProperty("header2"));
    }

    @Test
    public void headerParamsOnMethod() throws Exception
    {
        testIBean.init("shhh".getBytes());

        MuleMessage result = testIBean.doMethodHeaderParam("secret", new EchoParamFactory());
        assertNotNull(result);
        assertEquals("Value is: secret", result.getPayloadAsString());
        assertEquals("shhh", result.getInboundProperty("header1"));
        assertEquals("shhh secret", result.getInboundProperty("header2"));
        assertEquals("echoHeader", result.getInboundProperty("echoHeader"));
    }

    @Test
    public void propertyParamsOnMethod() throws Exception
    {
        testIBean.init("shhh".getBytes());

        MuleMessage result = testIBean.doMethodPropertyParam("secret", "hello", new ReversePropertyParamFactory("customProperty"));
        assertNotNull(result);
        assertEquals("Value is: secret", result.getPayloadAsString());
        assertEquals("shhh", result.getInboundProperty("header1"));
        assertEquals("shhh secret", result.getInboundProperty("header2"));
        assertEquals("olleh", result.getInboundProperty("propHeader"));
    }

    @Test
    public void headersWithNoParams() throws Exception
    {
        testIBean.init("shhh".getBytes());
        MuleMessage result = testIBean.doTestHeadersWithNoParams();
        assertNotNull(result);
        assertEquals("shhh", result.getInboundProperty("header1"));
    }

}
