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
import org.mule.ibeans.test.AbstractIBeansTestCase;

public class ImplicitPropertiesInFactoryTestCase extends AbstractIBeansTestCase
{
    @IntegrationBean
    private TestImplicitPropertiesinFactoryIBean testIBean;

    public void testGetHttpMethod() throws Exception
    {
        try
        {
            testIBean.doStuff();
        }
        catch (IllegalArgumentException e)
        {
            assertEquals("HTTP Method not set", e.getMessage());
        }
        catch (CallException e)
        {
            //expected
        }
    }

}