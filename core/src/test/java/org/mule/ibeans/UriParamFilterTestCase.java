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

import org.mule.ibeans.internal.util.UriParamFilter;
import org.mule.ibeans.test.IBeansTestSupport;

import java.util.regex.Pattern;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UriParamFilterTestCase extends IBeansTestSupport
{
    private UriParamFilter filter = new UriParamFilter();

    @Test
    public void optionalRemoveOneParam() throws Exception
    {
        String test = "http://foo.com?param=null.param";
        test = filter.filterParamsByValue(test, "null.param");
        assertEquals("http://foo.com", test);
        Pattern p;
    }

    @Test
    public void optionalRemoveTwoParam() throws Exception
    {
        String test = "http://foo.com?param=null.param&param2=foo";
        test = filter.filterParamsByValue(test, "null.param");
        assertEquals("http://foo.com?param2=foo", test);
    }

    @Test
    public void optionalRemoveThrteeParamsMiddle() throws Exception
    {
        String test = "http://foo.com?param0=foo&param1=null.param&param2=bar";
        test = filter.filterParamsByValue(test, "null.param");
        assertEquals("http://foo.com?param0=foo&param2=bar", test);
    }

    @Test
    public void optionalRemoveThreeParamsEnd() throws Exception
    {
        String test = "http://foo.com?param0=foo&param1=bar&param2=null.param";
        test = filter.filterParamsByValue(test, "null.param");
        assertEquals("http://foo.com?param0=foo&param1=bar", test);
    }

    @Test
    public void optionalRemoveAllButOne() throws Exception
    {
        String test = "http://foo.com?param0=foo&param1=null.param&param2=null.param&param3=null.param";
        test = filter.filterParamsByValue(test, "null.param");
        assertEquals("http://foo.com?param0=foo", test);
    }
}
