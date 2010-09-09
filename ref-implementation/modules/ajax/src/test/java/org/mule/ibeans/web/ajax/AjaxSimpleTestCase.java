/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.web.ajax;

import org.mule.api.MuleMessage;
import org.mule.api.client.LocalMuleClient;
import org.mule.util.concurrent.Latch;

import java.util.concurrent.atomic.AtomicReference;

import edu.emory.mathcs.backport.java.util.concurrent.TimeUnit;
import org.cometd.Client;
import org.cometd.Message;
import org.cometd.MessageListener;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class AjaxSimpleTestCase extends AjaxTestSupport
{
    @Before
    public void init() throws Exception
    {
        initAjax();
        registerBeans(new ReceiveStringAjaxBean(), new SendStringAjaxBean());
    }

    @After
    public void dispose() throws Exception
    {
        disposeAjax();
    }
    
    @Test
    public void clientSubscribeWithString() throws Exception
    {
        final Latch latch = new Latch();

        final AtomicReference<Object> data = new AtomicReference<Object>();
        client.addListener(new MessageListener()
        {
            public void deliver(Client client, Client client1, Message message)
            {
                if (message.getData() != null)
                {
                    //This simulate what the browser would receive
                    data.set((message.toString()));
                    latch.release();
                }
            }
        });
        client.subscribe("/test1");

        LocalMuleClient muleClient = muleContext.getClient();
        muleClient.dispatch("vm://in", "Ross", null);
        latch.await(10, TimeUnit.SECONDS);

        assertNotNull(data.get());
        assertEquals("{\"data\":\"Ross Received\",\"channel\":\"/test1\"}", data.get());
    }

    @Test
    public void clientPublishWithString() throws Exception
    {
        client.publish("/test2", "Ross", null);
        LocalMuleClient muleClient = muleContext.getClient();        
        MuleMessage msg = muleClient.request("vm://out", 5000L);

        assertNotNull(msg);
        assertEquals("Ross Received", msg.getPayloadAsString());
    }

}