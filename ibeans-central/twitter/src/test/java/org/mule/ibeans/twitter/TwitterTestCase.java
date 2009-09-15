/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.twitter;

import static org.mule.ibeans.IBeansSupport.selectValue;
import org.mule.ibeans.api.client.CallException;
import org.mule.ibeans.api.client.IntegrationBean;
import org.mule.ibeans.api.client.views.TextUsageView;
import org.mule.ibeans.test.AbstractIBeansTestCase;
import org.mule.module.json.JsonData;
import org.mule.util.UUID;

import java.beans.ExceptionListener;
import java.util.concurrent.atomic.AtomicBoolean;

import org.w3c.dom.Document;


/**
 * Tests a simple Twitter client that can update stutuses. Switch between JSON and XML
 */
public class TwitterTestCase extends AbstractIBeansTestCase
{
    @IntegrationBean
    private TwitterIBean twitter;


    public void testTwitterJson() throws Exception
    {

        String status = "test from Mule: " + UUID.getUUID();
        twitter.setCredentials("muletest", "mule1234");
        twitter.setFormat(TwitterBase.FORMAT.JSON, JsonData.class);
        JsonData json = twitter.statusesUpdate(status);
        assertNotNull(json);

        assertEquals(status, json.get("text"));
        assertEquals("Mule Test", json.get("user->name"));
    }

    public void testTwitterFriendTimeline() throws Exception
    {
        twitter.setCredentials("muletest", "mule1234");
        twitter.setFormat(TwitterBase.FORMAT.JSON, JsonData.class);
        JsonData data = twitter.getFriendTimeline(5);
        assertNotNull(data);
        assertTrue(data.isArray());
        assertEquals(5, data.size());

        assertNotNull(data.get("[0]->text"));
        assertNotNull(data.get("[4]->user->name"));
    }

    public void testTwitterPublicimeline() throws Exception
    {
        //Make sure public timeline works with defaults
        String string = twitter.getPublicTimeline();
        assertNotNull(string);

        twitter.setFormat(TwitterBase.FORMAT.JSON, JsonData.class);
        //should return 20 entries
        JsonData data = twitter.getPublicTimeline();
        assertNotNull(data);
        assertTrue(data.isArray());
        assertEquals(20, data.size());

        assertNotNull(data.get("[0]->text"));
        assertNotNull(data.get("[19]->user->name"));
    }

    public void testTwitterShowWithoutAuthentication() throws Exception
    {
        twitter.setFormat(TwitterBase.FORMAT.JSON, JsonData.class);
        //This doesn't require authentication.  iBeans should also automatically switch to HTTP GET
        JsonData data = twitter.statusesShow("2837116608");
        assertNotNull(data);
        assertEquals("Ross Mason", data.get("user->name"));
    }

    public void testTwitterXML() throws Exception
    {
        String status = "test from Mule: " + UUID.getUUID();
        twitter.setCredentials("muletest", "mule1234");
        twitter.setFormat(TwitterBase.FORMAT.XML, Document.class);
        Document doc = twitter.statusesUpdate(status);
        assertNotNull(doc);

        assertEquals(status, selectValue("/status/text", doc));
        assertEquals("Mule Test", selectValue("/status/user/name", doc));
    }

    public void testTwitterJsonWithError() throws Exception
    {
        twitter.setFormat(TwitterBase.FORMAT.JSON, JsonData.class);
        //This doesn't require authentication.  iBeans should also automatically switch to HTTP GET
        try
        {
            JsonData data = twitter.statusesShow("-1");
            System.out.println(data);
            fail("An exception should have been thrown because the status does not exist");
        }
        catch (CallException e)
        {
            //expected
        }

    }

    public void testTwitterJsonWithErrorListener() throws Exception
    {
        final AtomicBoolean exceptionThrown = new AtomicBoolean(false);
        twitter.setFormat(TwitterBase.FORMAT.JSON, JsonData.class);
        twitter.setExceptionListener(new ExceptionListener()
        {
            public void exceptionThrown(Exception e)
            {
                exceptionThrown.set(true);
            }
        });
        //This doesn't require authentication.  iBeans should also automatically switch to HTTP GET
        JsonData data = twitter.statusesShow("-1");
        //Exception should not be thrown, insted the listener intercepts it
        assertTrue(exceptionThrown.get());

    }

    //Just test that the view generator works
    public void testUsageView() throws Exception
    {
        TextUsageView view = new TextUsageView();
        String string = view.createView(TwitterIBean.class);
        System.out.println(string);
    }
}
