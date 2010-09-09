/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.module.rss;

import org.mule.ibeans.test.IBeansRITestSupport;

import java.util.Properties;

import org.ibeans.api.IBeansException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RssHttpSplitBasicTestCase extends IBeansRITestSupport
{
    private SplitFeed splitFeed;

    @Before
    public void init() throws IBeansException
    {
        splitFeed = new SplitFeed();
        registerBeans(splitFeed);
    }

    @Override
    protected void addStartUpProperties(Properties properties)
    {
        properties.setProperty("feed.uri", "rss:http://blogs.mulesoft.org/feed/");
    }

    @Test
    public void consumeFeed() throws Exception
    {
        final int sleepTime = 3000;
        Thread.sleep(sleepTime);
        int count = splitFeed.getCount();
        assertTrue(count > 0);
        Thread.sleep(sleepTime);
        //We should only receive entries once
        assertEquals(count, splitFeed.getCount());

    }
}