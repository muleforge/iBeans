/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.web;

import static org.mule.ibeans.IBeansSupport.select;
import org.mule.ibeans.api.application.Receive;
import org.mule.ibeans.api.client.IntegrationBean;
import org.mule.ibeans.flickr.FlickrIBean;
import org.mule.ibeans.gmail.GMailIBean;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.activation.DataSource;
import javax.activation.URLDataSource;
import javax.annotation.PostConstruct;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * TODO
 */
public class FlickrToEmailWithAttachments //implements Initialisable
{
    @IntegrationBean
    private FlickrIBean flickr;

    @IntegrationBean
    private GMailIBean gmail;

    @PostConstruct
    public void initialise()
    {
        gmail.init("muletestinbox@gmail.com", "mule12345678");
        flickr.init("cc300e636a539ac68da3fef4bb39754d", FlickrIBean.FORMAT.XML, Document.class);
    }

    @Receive(uri = "vm://search")
    public void emailPhotos(String searchTerm) throws Exception
    {
        Document doc = flickr.searchPhotos(searchTerm);

        List<DataSource> photos = new ArrayList<DataSource>();

        for (Node n : select("//photo", doc))
        {
            URL url = flickr.getPhotoURL(n);
            DataSource ds = new URLDataSource(url);
            photos.add(ds);
        }

        gmail.send("muleTestInbox@gmail.com", null, null, null, "Some photos of " + searchTerm, "Here are some photos", photos.toArray(new DataSource[]{}));
    }
}
