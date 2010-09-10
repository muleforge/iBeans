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
import org.mule.ibeans.gmail.GMailIBean;
import org.mule.ibeans.test.IBeansRITestSupport;

import javax.mail.Message;
import javax.mail.internet.MimeMultipart;

import org.ibeans.annotation.IntegrationBean;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class FlickrEmailTestCase extends IBeansRITestSupport
{
    @IntegrationBean
    private GMailIBean gmail;

    public FlickrEmailTestCase()
    {
        //Since the send email is on a timer we need to only start the context once we have cleared the inbox
        setStartContext(false);
    }

    @Before
    public void init() throws Exception
    {
        registerBeans(new FlickrToEmailWithAttachments(), new FlickrTransformers());
        gmail.init("${gmail.username}", "${gmail.password}");

        //drain the box
        while (gmail.receiveNext(1000) != null)
        {
        }
        //Since the send email is on a timer we need to only start the context once we have cleared the inbox
        startContext();
    }

    @Test
    public void flickrSearchWithEmail() throws Exception
    {
        iBeansContext.send("vm://search", "mules");

        //We have to block for a while to make sure the email gets sent with 5 photos attached
        Message msg = gmail.receiveNext(30000);
        assertNotNull(msg);

        assertEquals("Some photos of mules",  msg.getSubject());
        assertTrue(msg.getContent() instanceof MimeMultipart);
        assertEquals(6, ((MimeMultipart)msg.getContent()).getCount());

    }

}
