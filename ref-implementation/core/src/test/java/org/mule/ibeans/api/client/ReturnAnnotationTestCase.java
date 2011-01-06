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

import org.mule.ibeans.test.IBeansRITestSupport;

import java.net.URL;

import org.ibeans.annotation.IntegrationBean;
import org.junit.Test;
import org.junit.Ignore;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@Ignore
public class ReturnAnnotationTestCase extends IBeansRITestSupport
{
    @IntegrationBean
    private SearchIBean search;

    @Test
    public void returnCallURL() throws Exception
    {
        String result = search.searchAskAndReturnURLString("foo");
        assertNotNull(result);
        assertEquals("http://www.ask.com/web?q=foo&search=search", result);

        URL url = search.searchAskAndReturnURL("foo");
        assertNotNull(url);
        assertEquals("http://www.ask.com/web?q=foo&search=search", url.toString());
    }

}
