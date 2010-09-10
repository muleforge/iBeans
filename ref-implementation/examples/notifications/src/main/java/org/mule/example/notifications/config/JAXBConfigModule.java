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

import org.mule.ibeans.module.guice.AbstractGuiceIBeansModule;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

/**
 * Configures the Notification Agent, Heartbeat agent and the Echo Service
 */
public class JAXBConfigModule extends AbstractGuiceIBeansModule
{
    protected void configure()
    {
        try
        {
            JAXBContext jaxb = JAXBContext.newInstance("com.foo, com.bar");
            bind(JAXBContext.class).toInstance(jaxb);
        }
        catch (JAXBException e)
        {
            addError("Failed to create new JaxB Context", e);
        }
    }
}