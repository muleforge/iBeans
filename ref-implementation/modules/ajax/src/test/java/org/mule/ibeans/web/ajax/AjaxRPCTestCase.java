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

public class AjaxRPCTestCase extends AjaxTestSupport
{
    public static final String TEST_JSON_MESSAGE = "{\"data\" : {\"value1\" : \"foo\", \"value2\" : \"bar\"}, \"replyTo\" : \"/response\"}";

    @Before
    public void init() throws Exception
    {
        initAjax();
        registerBeans(new AjaxRpcBean());
    }

    @After
    public void dispose() throws Exception
    {
        disposeAjax();
    }
    
    @Test
    public void testDispatchReceiveSimple() throws Exception
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
                    data.set((message.getData()));
                    latch.release();
                }
            }
        });
        //The '/response' channel is set on the request message
        client.subscribe("/response");
        //Simulates dispatching from the browser
        client.publish("/request", TEST_JSON_MESSAGE, null);
        latch.await(10, TimeUnit.SECONDS);

        assertNotNull(data.get());
        assertEquals("{\"value1\":\"foo\",\"value2\":\"bar\"}", data.get());
    }
}