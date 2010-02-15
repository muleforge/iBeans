/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.mock;

import org.mule.ibeans.api.client.CallException;
import org.mule.ibeans.api.client.MockIntegrationBean;
import org.mule.ibeans.test.AbstractExternalPropsIBeansTestCase;

import org.w3c.dom.Document;

import static org.mockito.Mockito.when;
import static org.mule.ibeans.IBeansSupport.selectValue;

public class MockIBeanTestCase extends AbstractExternalPropsIBeansTestCase
{
    public static final String GOOD_IP = "12.215.42.19";
    public static final String BAD_IP = "12.215.42.";

    @MockIntegrationBean
    private HostipIBean hostip;

    public void testHostip() throws Exception
    {
        hostip.init(Document.class);
        when(hostip.getHostInfo(GOOD_IP)).thenAnswer(withXmlData("mock/hostip-found-response.xml", hostip));

        Document result = hostip.getHostInfo(GOOD_IP);
        String loc = selectValue("//*[local-name()='coordinates']", result);
        assertEquals("-88.4588,41.7696", loc);
    }

    public void testHostipError() throws Exception
    {
        //Because we are testing this in the core module we cannot import the xml module, so
        //we set the return type to sting and define a RegEx error filter on the iBean
        hostip.init(String.class);
        when(hostip.getHostInfo(BAD_IP)).thenAnswer(withXmlData("mock/hostip-not-found-response.xml", hostip));

        try
        {
            hostip.getHostInfo(BAD_IP);
            fail("The iBean should have recognised a Bad ip");
        }
        catch (CallException e)
        {
            //expected
        }
    }

    public void testTemplateMethod() throws Exception
    {
        String result = hostip.dummyTemplateMethod("three");
        assertEquals("one two three", result);
    }
}
