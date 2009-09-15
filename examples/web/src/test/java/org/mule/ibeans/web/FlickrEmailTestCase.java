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

import org.mule.ibeans.flickr.FlickrTransformers;
import org.mule.ibeans.test.AbstractIBeansTestCase;

/**
 * TODO
 */
public class FlickrEmailTestCase extends AbstractIBeansTestCase
{
    @Override
    protected void doSetUp() throws Exception
    {
        registerBeans(new FlickrToEmailWithAttachments());
        registerBeans(new FlickrTransformers());
    }

    public void testFlickrSearchWithEmail() throws Exception
    {
        iBeansContext.send("vm://search", "mules");

        //We have to sleep for a while to make sure the email gets sent with 10 photos attached
        Thread.sleep(40000);
    }

}
