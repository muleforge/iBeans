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

import javax.annotation.PostConstruct;

import org.ibeans.annotation.IntegrationBean;

public class EmailSendUsingIBean
{
    @IntegrationBean
    private GMailIBean gmail;

    @PostConstruct
    public void initialise()
    {
        gmail.init("${gmail.username}", "${gmail.password}");
    }

    @Schedule(interval = 2000)
    public void process() throws Exception
    {
        gmail.send("${gmail.username}", "A Scheduled Email", "This is a test dude");
    }

}