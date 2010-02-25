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

public class AjaxWithjsonBindingsTestCase extends AjaxTestSupport
{
    @Before
    public void init() throws Exception
    {
        initAjax();
        registerBeans(new ReceiveJsonAjaxBean(), new SendJsonAjaxBean());
    }

    @After
    public void dispose() throws Exception
    {
        disposeAjax();
    }
    
    @Test
    public void clientSubscribeWithJsonObjectResponse() throws Exception
    {
        final Latch latch = new Latch();

        final AtomicReference<Object> data = new AtomicReference<Object>();
        client.addListener(new MessageListener()
        {
            public void deliver(Client client, Client client1, Message message)
            {
                if (message.getData() != null)
                {
                    System.err.println("local: " + message);
                    //This simulate what the browser would receive
                    data.set((message.toString()));
                    latch.release();
                }
            }
        });

        client.subscribe("/test1");
        iBeansContext.send("vm://in", "Ross", null);
        latch.await(10, TimeUnit.SECONDS);

        assertNotNull(data.get());
        assertEquals("{\"data\":{\"name\":\"Ross\"},\"channel\":\"/test1\"}", data.get());
    }

    @Test
    public void clientPublishWithJsonObject() throws Exception
    {
        client.publish("/test2", "{\"name\":\"Ross\"}", null);
        String msg = iBeansContext.receive("vm://out", String.class, 5000);

        assertNotNull(msg);
        assertEquals("JsonBean{name='Ross'}", msg);
    }
}
