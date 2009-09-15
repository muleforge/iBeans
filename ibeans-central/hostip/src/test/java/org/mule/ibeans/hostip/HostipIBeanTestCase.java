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

import static org.mule.ibeans.IBeansSupport.selectValue;
import org.mule.ibeans.api.client.CallException;
import org.mule.ibeans.api.client.IntegrationBean;
import org.mule.ibeans.test.AbstractIBeansTestCase;

import org.w3c.dom.Document;

/**
 * A quick IP look up test just to confirm we get the result back
 */
public class HostipIBeanTestCase extends AbstractIBeansTestCase
{
    public static final String GOOD_IP = "12.215.42.19";
    public static final String BAD_IP = "12.215.42.";

    @IntegrationBean
    private HostipIBean hostip;

    @Override
    protected void doSetUp() throws Exception
    {
        getHostipIBean().init(Document.class);
    }

    protected HostipIBean getHostipIBean()
    {
        return hostip;
    }

    public void testHostip() throws Exception
    {
        Document result = getHostipIBean().getHostInfo(GOOD_IP);
        String loc = selectValue("//gml:coordinates", result);
        assertEquals("-88.4588,41.7696", loc);
    }

    public void testHostipError() throws Exception
    {
        try
        {
            getHostipIBean().getHostInfo(BAD_IP);
            fail("The iBean should have recognised a Bad ip");
        }
        catch (CallException e)
        {
            //exprected
        }
    }
}