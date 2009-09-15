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

import org.mule.ibeans.api.application.Transformer;
import org.mule.util.IOUtils;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.net.URL;
import java.net.MalformedURLException;

import javax.imageio.ImageIO;

/**
 * Transformers used by the Flickr iBean
 */
public class FlickrTransformers
{
    @Transformer
    public BufferedImage transformInputstreamToBufferedImage(InputStream is) throws IOException
    {
        try
        {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            IOUtils.copyLarge(is, baos);

            BufferedImage image = ImageIO.read(new ByteArrayInputStream(baos.toByteArray()));
            if (image == null)
            {
                throw new IOException("could not load images from stream: " + baos.toString());
            }
            return image;
        }
        finally
        {
            is.close();
        }
    }

    @Transformer
    public URL transformStringToURL(String string) throws MalformedURLException
    {
        return new URL(string);
    }
}
