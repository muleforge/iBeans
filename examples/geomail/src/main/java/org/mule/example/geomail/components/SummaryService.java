/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.example.geomail.components;

import org.mule.example.geomail.dao.Sender;
import org.mule.ibeans.flickr.FlickrBase;
import org.mule.ibeans.flickr.FlickrSearchIBean;
import org.mule.module.annotationx.api.Receive;
import org.mule.module.annotationx.api.Send;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.ibeans.annotation.IntegrationBean;
import org.ibeans.api.CallException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import static org.ibeans.impl.IBeansSupport.selectOne;


/**
 * Simple validates that a sender has valid lat and long values before sending to the client
 */
public class SummaryService
{
    public static final String SUMMARY_TEMPLATE = "<table><tr><td><img src='{7}'/></td><td><b>{0}, {1}</b><br/>Lat: {3}, Lon: {4}<br/>Email: {5}<br/>IP: {6}</td></tr></table>";

    @IntegrationBean
    private FlickrSearchIBean flickr;

    @Inject
    @Named("flickr.api.key")
    private String flickrApiKey;

    @PostConstruct
    public void init()
    {
        flickr.init(flickrApiKey, FlickrBase.FORMAT.XML, Document.class);
    }


    @Receive(uri = "vm://summary")
    @Send(uri = "vm://storage")
    public Sender generateSummary(Sender sender)
    {

        if (sender.getLatitude() == null || sender.getLongitude() == null)
        {
            //returning null will stop message flow, nothing to send to the client
            return null;
        }
        if (sender.getSummary() != null)
        {
            return sender;
        }
        String summary = MessageFormat.format(SUMMARY_TEMPLATE,
                sender.getLocationName(), sender.getCountryName(),
                sender.getLatitude(), sender.getLongitude(),
                sender.getEmail(), sender.getIp(),
                getPhotoUrlForLocation(sender.getLocationName()));

        sender.setSummary(summary);
        return sender;
    }

    protected URL getPhotoUrlForLocation(String location)
    {
        Map<String, Object> params = new HashMap<String, Object>(2);
        params.put("tags", location);
        params.put("per_page", 1);

        URL photoUrl;
        try
        {
            Document doc = flickr.search(params);
            Node photo = selectOne("//photo", doc);
            if (photo != null)
            {
                photoUrl = flickr.getPhotoURL(photo, FlickrBase.IMAGE_SIZE.Thumbnail, FlickrBase.IMAGE_TYPE.Jpeg);
            }
            else
            {
                photoUrl = getDefaultImageUrl();
            }
        }
        catch (CallException e)
        {
            //Could not retrieve photo, not the end of the world
            photoUrl = getDefaultImageUrl();
        }
        return photoUrl;
    }

    private URL getDefaultImageUrl()
    {
        try
        {
            return new URL("http://www.indiald.com/images/No.png");
        }
        catch (MalformedURLException e)
        {
            //not going to happen
            return null;
        }
    }
}
