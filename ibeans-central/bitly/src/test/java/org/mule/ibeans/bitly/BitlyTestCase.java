/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.bitly;

import org.mule.ibeans.api.client.CallException;
import org.mule.ibeans.api.client.IntegrationBean;
import org.mule.ibeans.test.AbstractIBeansTestCase;
import org.mule.module.json.JsonData;

import java.net.URL;

import org.w3c.dom.Document;

public class BitlyTestCase extends AbstractIBeansTestCase
{
    @IntegrationBean
    private BitlyIBean bitly;

    public void testBitlyJsonWithNonHttpError() throws Exception
    {
        bitly.init("bitlyapidemo", "R_0da49e0a9118ff35f52f629d2d71bf07");
        bitly.setFormat("json", JsonData.class);

        try
        {
            bitly.expand("http://badurl.bom");
        }
        catch (CallException e)
        {
            assertTrue(e.getMessage().contains("No info available for requested document."));
            //exprected
        }
    }

    public void testBitlyXmlWithNonHttpError() throws Exception
    {
        bitly.init("bitlyapidemo", "R_0da49e0a9118ff35f52f629d2d71bf07");
        bitly.setFormat("xml", Document.class);

        try
        {
            bitly.expand("http://badurl.bom");
        }
        catch (CallException e)
        {
            assertEquals("1211", e.getErrorCode());
            assertTrue(e.getMessage().contains("No info available for requested document."));
            //exprected
        }
    }

    public void testBitlyWithHttpError() throws Exception
    {
        //bad creds, 401 error
        bitly.init("foo", "bar");
        bitly.setFormat("json", JsonData.class);

        try
        {
            bitly.expand("http://google.com");
        }
        catch (CallException e)
        {
            assertEquals("401", e.getErrorCode());
            //exprected
        }
    }

    public void testBitlyRoundTrip() throws Exception
    {
        bitly.init("bitlyapidemo", "R_0da49e0a9118ff35f52f629d2d71bf07");
        bitly.setFormat("json", JsonData.class);
        String url = "http://rossmason.blogspot.com/2008/01/about-me.html";
        JsonData result = bitly.shorten(url);
        assertNotNull(result);

        String shortUrl = result.get("results->" + url + "->shortUrl").toString();

        result = bitly.expand(shortUrl);
        assertNotNull(result);

        //We just need the short URL hash as the proerty name
        String longUrl = result.get("results->" + new URL(shortUrl).getPath().substring(1) + "->longUrl").toString();
        assertEquals(url, longUrl);

    }
}
