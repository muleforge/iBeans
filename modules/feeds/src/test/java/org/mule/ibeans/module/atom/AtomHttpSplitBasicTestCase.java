/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.module.atom;

import org.mule.ibeans.test.AbstractIBeansTestCase;

import java.util.Properties;

public class AtomHttpSplitBasicTestCase extends AbstractIBeansTestCase
{
    private SplitFeed splitFeed;

    @Override
    protected void doSetUp() throws Exception
    {
        splitFeed = new SplitFeed();
        registerBeans(splitFeed);
    }

    @Override
    protected void addStartUpProperties(Properties properties)
    {
        properties.setProperty("feed.uri", "atom:http://rossmason.blogspot.com/feeds/posts/default");
    }

    public void testConsumeFeed() throws Exception
    {
        Thread.sleep(4000);
        int count = splitFeed.getCount();
        assertTrue(count > 0);
        Thread.sleep(3000);
        //We should only receive entries once
        assertEquals(count, splitFeed.getCount());

    }
}
