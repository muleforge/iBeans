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

import org.mule.ibeans.api.client.CallException;
import org.mule.ibeans.api.client.IntegrationBean;
import org.mule.ibeans.test.IBeansTestSupport;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ImplicitPropertiesInFactoryTestCase extends IBeansTestSupport
{
    @IntegrationBean
    private TestImplicitPropertiesinFactoryIBean testIBean;

    @Test
    public void getHttpMethod() throws Exception
    {
        try
        {
            testIBean.doStuff();
        }
        catch (IllegalArgumentException e)
        {
            //THis would only occur if the HTTP.MEthod was not set explicitly by iBeans
            assertEquals("HTTP Method not set", e.getMessage());
        }
        catch (CallException e)
        {
            //expected, we can't actually connect to the service
        }
    }

}