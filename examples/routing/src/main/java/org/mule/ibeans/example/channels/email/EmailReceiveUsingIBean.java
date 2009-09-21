/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.example.channels.email;

import org.mule.ibeans.api.application.Schedule;
import org.mule.ibeans.api.application.Send;
import org.mule.ibeans.api.client.IntegrationBean;
import org.mule.ibeans.gmail.GMailIBean;
import org.mule.api.lifecycle.Initialisable;

import javax.annotation.PostConstruct;
import javax.mail.Message;
import javax.inject.Singleton;

/**
 * TODO
 */
public class EmailReceiveUsingIBean
{
    @IntegrationBean
    private GMailIBean gmail;

    @PostConstruct
    public void initialise()
    {
        gmail.init("muletestinbox@gmail.com", "mule12345678");
    }

    @Schedule(interval = 2000)
    @Send(uri = "vm://result", id = "result")
    public Object process() throws Exception
    {
        Message message = gmail.receiveNext(3000);
        if (message == null)
        {
            return null;
        }
        return message.getContent();
    }

}
