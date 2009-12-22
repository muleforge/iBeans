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

import org.mule.ibeans.test.AbstractIBeansTestCase;

import java.util.Properties;

public class RssSplitWithUpdateTestCase extends AbstractIBeansTestCase
{
    private SplitFeed splitFeed;
    private SplitFeedWithLastUpdate splitFeedWithLastUpdate;

    @Override
    protected void doSetUp() throws Exception
    {
        splitFeed = new SplitFeed();
        splitFeedWithLastUpdate = new SplitFeedWithLastUpdate();
        registerBeans(splitFeed, splitFeedWithLastUpdate);
    }

    @Override
    protected void addStartUpProperties(Properties properties)
    {
        properties.setProperty("feed.uri", "rss:http://blogs.mulesoft.org/feed/");
    }

    public void testConsumeFeed() throws Exception
    {
        Thread.sleep(3000);
        int count = splitFeed.getCount();
        int withUpdateCount = splitFeedWithLastUpdate.getCount();
        assertTrue(count > 0);
        assertTrue(withUpdateCount > 0);
        assertTrue(count >= withUpdateCount);
        Thread.sleep(3000);
        //We should only receive entries once
        assertEquals(count, splitFeed.getCount());
        assertEquals(withUpdateCount, splitFeedWithLastUpdate.getCount());

    }
}