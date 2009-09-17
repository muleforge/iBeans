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

import org.mule.ibeans.api.client.Call;
import org.mule.ibeans.api.client.CallException;
import org.mule.ibeans.api.client.State;
import org.mule.ibeans.api.client.Template;
import org.mule.ibeans.api.client.Usage;
import org.mule.ibeans.api.client.filters.XmlErrorFilter;
import org.mule.ibeans.api.client.params.Payload;
import org.mule.ibeans.api.client.params.ReturnType;
import org.mule.ibeans.api.client.params.UriParam;
import org.mule.ibeans.channels.HTTP;

import java.awt.image.BufferedImage;
import java.net.URL;

import org.w3c.dom.Node;

@Usage("Provides simplified access to the Flickr API for searching and reading photos. This bean supports XML and JSON responses. XML is used by default, but JSON can be set by" +
        "setting the 'format' param to 'json' in one of the init methods. Note that 'json' or '' are the only valid values for 'format'. The ReturnType param can be set to java.lang.String (default), org.w3c.dom.Document, or org.mule.module.json.JsonData.")
@XmlErrorFilter(expr = "/rsp/@stat = 'fail'", errorCode = "/rsp/err/@code")
public interface FlickrIBean
{
    public static enum IMAGE_SIZE
    {
        SmallSquare("s"),
        Thumbnail("t"),
        Small("m"),
        Medium("-"),
        Large("b"),
        Original("o");

        private String value;

        private IMAGE_SIZE(String value)
        {
            this.value = value;
        }

        public String toString()
        {
            return value;
        }

    }

    public static enum IMAGE_TYPE
    {
        Jpeg("jpg"),
        Gif("gif"),
        Png("png");

        private String value;

        private IMAGE_TYPE(String value)
        {
            this.value = value;
        }

        public String toString()
        {
            return value;
        }
    }

    public static enum FORMAT
    {
        JSON("json"),
        XML("");

        private String value;

        private FORMAT(String value)
        {
            this.value = value;
        }

        public String toString()
        {
            return value;
        }
    }

    @ReturnType
    public static final Class DEFAULT_RETURN_TYPE = String.class;

    @UriParam("format")
    public static final FORMAT DEFAULT_FORMAT = FORMAT.XML;

    @UriParam("image_type")
    public static final IMAGE_TYPE DEFAULT_IMAGE_TYPE = IMAGE_TYPE.Jpeg;

    @UriParam("image_size")
    public static final IMAGE_SIZE DEFAULT_IMAGE_SIZE = IMAGE_SIZE.Small;

    @State
    public void init(@UriParam("api_key") String apikey);

    @State
    public void init(@UriParam("api_key") String apikey, @UriParam("format") FORMAT format);

    @State
    public void init(@UriParam("api_key") String apikey, @UriParam("format") FORMAT format, @ReturnType() Class returnType);

    @Call(uri = "http://www.flickr.com/services/rest?method=flickr.photos.search&api+key={api_key}&tags={tags}&per_page=10&format={format}&nojsoncallback=1", properties = {HTTP.METHOD_GET})
    public <T> T searchPhotos(@UriParam("tags") String tags) throws CallException;

    @Call(uri = "{photo_url}", properties = {HTTP.FOLLOW_REDIRECTS})
    public BufferedImage getPhoto(@UriParam("photo_url") String photoUrl) throws CallException;

    @Template("http://static.flickr.com/#[xpath2:@server]/#[xpath2:@id]_#[xpath2:@secret]_{image_size}.{image_type}")
    public URL getPhotoURL(@Payload Node photoNode) throws CallException;

    @Template("http://static.flickr.com/#[xpath2:@server]/#[xpath2:@id]_#[xpath2:@secret]_{image_size}.{image_type}")
    public URL getPhotoURL(@Payload Node photoNode, @UriParam("image_size") IMAGE_SIZE size, @UriParam("image_type") IMAGE_TYPE type) throws CallException;
}