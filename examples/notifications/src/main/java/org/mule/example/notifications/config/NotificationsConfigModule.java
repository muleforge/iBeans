/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.example.notifications.config;

import org.mule.agent.EndpointNotificationLoggerAgent;
import org.mule.api.MuleContext;
import org.mule.api.MuleException;
import org.mule.api.context.notification.ComponentMessageNotificationListener;
import org.mule.context.notification.ComponentMessageNotification;
import org.mule.context.notification.SecurityNotification;
import org.mule.example.notifications.DummySecurityFilter;
import org.mule.example.notifications.EchoService;
import org.mule.example.notifications.HeartbeatAgent;
import org.mule.example.notifications.HeartbeatNotification;
import org.mule.ibeans.config.ChannelConfigBuilder;
import org.mule.ibeans.config.guice.AbstractGuiceIBeansModule;
import org.mule.module.json.transformers.ObjectToJson;
import org.mule.routing.filters.PayloadTypeFilter;

import com.google.inject.Provides;

/**
 * Configures the Notification Agent, Heartbeat agent and the Echo Service
 */
public class NotificationsConfigModule extends AbstractGuiceIBeansModule
{
    @Override
    public void configureMuleContext(MuleContext muleContext)
    {
        //Need to enable component level notifications for this example
        muleContext.getNotificationManager().addInterfaceToType(
                ComponentMessageNotificationListener.class, ComponentMessageNotification.class);
    }

    protected void doConfigure() throws Exception
    {
        //The echo chanel used by the echo service
        bind(channelBuilder("echo-channel", "ajax:///ibeans/services/echo").setSecurityFilter(new DummySecurityFilter()));

        bind(EchoService.class).asEagerSingleton();
        bind(HeartbeatAgent.class).asEagerSingleton();
    }


    //For more complex agents, using the @Provider annotation seems a bit cleaner.
    @Provides
    public EndpointNotificationLoggerAgent provideNotificationAgent() throws MuleException
    {
        EndpointNotificationLoggerAgent agent = new EndpointNotificationLoggerAgent();
        agent.setIgnoreConnectionNotifications(true);

        ChannelConfigBuilder builder = channelBuilder("notifications-channel", "ajax:///ibeans/services/notifications");
        ObjectToJson trans = new ObjectToJson();
        trans.setExcludeProperties("muleContext");
        //Since this object is not bound by Guice and nested on an endpoint it will not get configured without calling this
        //TODO Maybe the builder should take responsibility for this
        initialiseObject(trans);
        builder.addTransformer(trans);
        builder.orFilter(
                new PayloadTypeFilter(HeartbeatNotification.class),
                new PayloadTypeFilter(SecurityNotification.class),
                new PayloadTypeFilter(ComponentMessageNotification.class));

        agent.setEndpoint(builder.buildSendChannel());
        return agent;
    }
}
