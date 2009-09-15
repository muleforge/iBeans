/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.ibeans.hostip;

import org.mule.ibeans.api.client.MockIntegrationBean;

import static org.mockito.Mockito.when;

/**
 * A quick IP look up test just to confirm we get the result back
 */
public class MockHostipIBeanTestCase extends HostipIBeanTestCase
{
    public static final String GOOD_IP = "12.215.42.19";
    public static final String BAD_IP = "12.215.42.";

    @MockIntegrationBean
    private HostipIBean hostip;

    @Override
    protected void doSetUp() throws Exception
    {
        super.doSetUp();
        when(hostip.getHostInfo(GOOD_IP)).thenAnswer(withXmlData("hostip-found-response.xml", hostip));
        when(hostip.getHostInfo(BAD_IP)).thenAnswer(withXmlData("hostip-not-found-response.xml", hostip));
    }

    protected HostipIBean getHostipIBean()
    {
        return hostip;
    }
}