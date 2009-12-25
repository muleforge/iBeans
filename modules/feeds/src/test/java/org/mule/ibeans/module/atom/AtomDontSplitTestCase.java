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

public class AtomDontSplitTestCase extends AbstractIBeansTestCase
{
    private DontSplitFeed noSplitFeed;

    @Override
    protected void doSetUp() throws Exception
    {
        noSplitFeed = new DontSplitFeed();
        registerBeans(noSplitFeed);
    }

    @Override
    protected void addStartUpProperties(Properties properties)
    {
        properties.setProperty("feed.uri", "atom:http://rossmason.blogspot.com/feeds/posts/default");
    }

    public void testConsumeFeed() throws Exception
    {
        Thread.sleep(2000);
        int count = noSplitFeed.getCount();
        assertTrue(count > 0);
    }
}