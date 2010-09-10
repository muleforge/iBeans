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

import org.mule.ibeans.gmail.GMailIBean;
import org.mule.ibeans.test.IBeansRITestSupport;

import org.ibeans.annotation.IntegrationBean;
import org.ibeans.api.IBeansException;
import org.junit.Before;

public abstract class AbstractEmailTestCase extends IBeansRITestSupport
{
    public static final int RECEIVE_TIMEOUT = 10000;

    //We still use the GMailIBean for testing but SendEmailPOJO we're testing does not
    @IntegrationBean
    protected GMailIBean gmail;

    protected AbstractEmailTestCase()
    {
        //Since the send email is on a timer we need to only start the context once we have cleared the inbox
        setStartContext(false);
    }



    @Before
    public void init() throws Exception
    {
        addBeans();

        gmail.init("${gmail.username}", "${gmail.password}");
        //drain the box
        while (gmail.receiveNext(3000) != null)
        {
        }
        //Since the send email is on a timer we need to only start the context once we have cleared the inbox
        startContext();
    }

    protected abstract void addBeans() throws IBeansException;
}