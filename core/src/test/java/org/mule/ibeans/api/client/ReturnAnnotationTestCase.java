/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.api.client;

import org.mule.ibeans.test.AbstractIBeansTestCase;

import java.net.URL;

/**
 * TODO
 */
public class ReturnAnnotationTestCase extends AbstractIBeansTestCase
{
    @IntegrationBean
    private SearchIBean search;

    public void testReturnCallURL() throws Exception
    {
        String result = search.searchAskAndReturnURLString("foo");
        assertNotNull(result);
        assertEquals("http://www.ask.com/web?q=foo&search=search", result);

        URL url = search.searchAskAndReturnURL("foo");
        assertNotNull(url);
        assertEquals("http://www.ask.com/web?q=foo&search=search", url.toString());
    }

}
