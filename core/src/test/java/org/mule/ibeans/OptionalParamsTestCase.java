/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans;

import org.mule.ibeans.api.client.IntegrationBean;
import org.mule.ibeans.test.IBeansTestSupport;

import java.net.UnknownHostException;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class OptionalParamsTestCase extends IBeansTestSupport
{
    @IntegrationBean
    private TestUriIBean test;

    @Test
    public void optionalParams() throws Exception
    {
        String result = test.doSomethingOptional("x", "y");
        assertEquals("http://doesnotexist.bom?param1=x&param2=y", result);

        result = test.doSomethingOptional("x", null);
        assertEquals("http://doesnotexist.bom?param1=x", result);

        result = test.doSomethingOptional(null, "y");
        assertEquals("http://doesnotexist.bom?param2=y", result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void paramNull() throws UnknownHostException
    {
        test.doSomethingElse("x", null);
        fail("Null argument is not optional");
    }
}
