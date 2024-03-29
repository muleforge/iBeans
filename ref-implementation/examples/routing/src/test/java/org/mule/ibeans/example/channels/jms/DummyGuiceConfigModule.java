/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.example.channels.jms;

import org.mule.ibeans.module.guice.AbstractGuiceIBeansModule;
import org.mule.transport.jms.activemq.ActiveMQJmsConnector;

/**
 * A Guice config module that configures two JMS channels (the second is not used, but tests multiple channel configs)
 * And configures the JmsScheduleSendBean using Guice.
 */
public class DummyGuiceConfigModule extends AbstractGuiceIBeansModule
{
    protected void configure()
    {
        //We use this in the Send annotation on the JmsScheduleSendBean object
        bind(channelBuilder("jms-publish", "jms://publish").setConnector(new ActiveMQJmsConnector(muleContext)));

        //This isn't used but wanted to test configuring two ChannelConfigBuilders
        bind(channelBuilder("jms-publish2", "jms://publish2").setConnector(new ActiveMQJmsConnector(muleContext)));

        //Bind our test bean
        bind(JmsScheduleSendBean.class).asEagerSingleton();
    }

}
