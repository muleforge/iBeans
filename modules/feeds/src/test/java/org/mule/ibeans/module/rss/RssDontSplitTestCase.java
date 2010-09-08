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

import static org.junit.Assert.assertTrue;

public class RssDontSplitTestCase extends IBeansRITestSupport
{
    private DontSplitFeed noSplitFeed;

    @Before
    public void init() throws IBeansException
    {
        noSplitFeed = new DontSplitFeed();
        registerBeans(noSplitFeed);
    }

    @Override
    protected void addStartUpProperties(Properties properties)
    {
        properties.setProperty("feed.uri", "http://feeds.feedburner.com/muleblog?format=xml");
    }

    @Test
    public void consumeFeed() throws Exception
    {
        Thread.sleep(3000);
        int count = noSplitFeed.getCount();
        assertTrue(count > 0);
    }
}