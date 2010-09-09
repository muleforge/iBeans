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

import org.mule.api.config.MuleProperties;
import org.mule.ibeans.test.IBeansRITestSupport;
import org.mule.transport.ajax.container.MuleAjaxServlet;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.mortbay.cometd.client.BayeuxClient;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.client.Address;
import org.mortbay.jetty.client.HttpClient;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;

public class AjaxTestSupport extends IBeansRITestSupport
{
    public static final int SERVER_PORT = 58080;

    protected BayeuxClient client;
    protected Server httpServer;

    public final void initAjax() throws Exception
    {
        httpServer = new Server(SERVER_PORT);

        Context c = new Context(httpServer, "/", Context.SESSIONS);
        c.addServlet(new ServletHolder(new MuleAjaxServlet()), "/ajax/*");
        c.addEventListener(new ServletContextListener() {
            public void contextInitialized(ServletContextEvent sce)
            {
                sce.getServletContext().setAttribute(MuleProperties.MULE_CONTEXT_PROPERTY, muleContext);
            }

            public void contextDestroyed(ServletContextEvent sce) { }
        });

        httpServer.start();

        HttpClient http = new HttpClient();
        http.setConnectorType(HttpClient.CONNECTOR_SELECT_CHANNEL);

        client = new BayeuxClient(http, new Address("localhost", SERVER_PORT), "/ajax/cometd");
        http.start();
        //need to start the client before you can add subscriptions
        client.start();
    }

    public void disposeAjax() throws Exception
    {
        //9 times out of 10 this throws a "ava.lang.IllegalStateException: Not running" exception, it can be ignored
        client.stop();
        if(httpServer!=null) httpServer.stop();
    }


}