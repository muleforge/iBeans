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

import org.mule.ibeans.flickr.FlickrIBean;
import org.mule.ibeans.gmail.GMailIBean;
import org.mule.module.annotationx.api.Receive;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.activation.DataSource;
import javax.activation.URLDataSource;
import javax.annotation.PostConstruct;

import org.ibeans.annotation.IntegrationBean;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import static org.mule.ibeans.IBeansSupport.select;

/**
 * Search for photos and email them to me
 */
public class FlickrToEmailWithAttachments
{
    @IntegrationBean
    private FlickrIBean flickr;

    @IntegrationBean
    private GMailIBean gmail;

    @PostConstruct
    public void initialise()
    {
        //Init gmail: properties loaded in the context
        gmail.init("${gmail.username}", "${gmail.password}");

        //Init Flickr: properties loaded in the context
        flickr.init("${flickr.api.key}", "${flickr.secret.key}", FlickrIBean.FORMAT.XML, Document.class);
    }

    @Receive(uri = "vm://search")
    public void emailPhotos(String searchTerm) throws Exception
    {
        //Get 5 photos
        Document doc = flickr.search(searchTerm, 5, 1);

        List<DataSource> photos = new ArrayList<DataSource>();

        for (Node n : select("//photo", doc))
        {
            URL url = flickr.getPhotoURL(n);
            DataSource ds = new URLDataSource(url);
            photos.add(ds);
        }

        gmail.send("${gmail.username}", null, null, null, "Some photos of " + searchTerm, "Here are some photos", photos.toArray(new DataSource[]{}));
    }
}
