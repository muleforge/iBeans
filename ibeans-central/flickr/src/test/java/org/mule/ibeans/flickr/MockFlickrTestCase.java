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

import org.mule.ibeans.api.client.MockIntegrationBean;

import static org.mockito.Mockito.when;

public class MockFlickrTestCase extends FlickrTestCase
{
    @MockIntegrationBean
    private FlickrIBean flickr;

    @Override
    protected void doSetUp() throws Exception
    {
        super.doSetUp();
        when(getFlickr().searchPhotos(SEARCH_TERM)).thenAnswer(withXmlData("search-donkeys.xml", getFlickr()));
        when(getFlickr().searchPhotos(BAD_SEARCH_TERM)).thenAnswer(withXmlData("bad-key-response.xml", getFlickr()));
    }

    protected FlickrIBean getFlickr()
    {
        return flickr;
    }
}