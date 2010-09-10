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

import org.mule.api.annotations.Schedule;
import org.mule.ibeans.gmail.GMailIBean;
import org.mule.module.annotationx.api.Send;

import javax.annotation.PostConstruct;
import javax.mail.Message;

import org.ibeans.annotation.IntegrationBean;

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
        gmail.init("${gmail.username}", "${gmail.password}");
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
