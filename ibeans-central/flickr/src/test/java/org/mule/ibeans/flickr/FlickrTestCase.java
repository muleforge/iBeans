/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.flickr;

import static org.mule.ibeans.IBeansSupport.select;
import org.mule.ibeans.api.client.CallException;
import org.mule.ibeans.api.client.IntegrationBean;
import org.mule.ibeans.test.AbstractIBeansTestCase;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class FlickrTestCase extends AbstractIBeansTestCase
{
    public static final String SEARCH_TERM = "donkeys";
    public static final String BAD_SEARCH_TERM = "bad";

    @IntegrationBean
    private FlickrIBean flickr;

    @Override
    protected void doSetUp() throws Exception
    {
        getFlickr().init("cc300e636a539ac68da3fef4bb39754d", FlickrIBean.FORMAT.XML, Document.class);
        registerBeans(new FlickrTransformers());
    }

    protected FlickrIBean getFlickr()
    {
        return flickr;
    }

    public void testFlickr() throws Exception
    {
        Document doc = getFlickr().searchPhotos(SEARCH_TERM);
        assertNotNull(doc);
        List<URL> photoUrls = new ArrayList<URL>();

        for (Node n : select("//photo", doc))
        {
            photoUrls.add(getFlickr().getPhotoURL(n));
        }

        assertEquals(10, photoUrls.size());
    }

    //This will fail since "badkey" is not a recognised key
    public void testFlickrError() throws Exception
    {
        getFlickr().init("badkey", FlickrIBean.FORMAT.XML, Document.class);

        try
        {
            getFlickr().searchPhotos(BAD_SEARCH_TERM);
        }
        catch (CallException e)
        {
            //Flickr error code
            assertEquals("100", e.getErrorCode());
        }
    }

    public void testSizeEnum() throws Exception
    {
        assertEquals("o", FlickrIBean.IMAGE_SIZE.Original.toString());
        assertEquals("m", FlickrIBean.DEFAULT_IMAGE_SIZE.toString());
        assertEquals(FlickrIBean.IMAGE_SIZE.Original, Enum.valueOf(FlickrIBean.IMAGE_SIZE.class, "Original"));

        Document doc = getFlickr().searchPhotos(SEARCH_TERM);
        assertNotNull(doc);
        List<URL> photoUrls = new ArrayList<URL>();

        for (Node n : select("//photo", doc))
        {
            photoUrls.add(getFlickr().getPhotoURL(n, FlickrIBean.IMAGE_SIZE.SmallSquare, FlickrIBean.IMAGE_TYPE.Jpeg));
        }
        assertEquals(10, photoUrls.size());
        assertTrue(photoUrls.get(0).toString().endsWith("_s.jpg"));
    }
}