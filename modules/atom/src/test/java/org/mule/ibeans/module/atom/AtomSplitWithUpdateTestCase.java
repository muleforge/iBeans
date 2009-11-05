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

public class AtomSplitWithUpdateTestCase extends AbstractIBeansTestCase
{
    private AtomSplitFeed splitFeed;
    private AtomSplitFeedWithLastUpdate splitFeedWithLastUpdate;

    @Override
    protected void doSetUp() throws Exception
    {
        splitFeed = new AtomSplitFeed();
        splitFeedWithLastUpdate = new AtomSplitFeedWithLastUpdate();
        registerBeans(splitFeed, splitFeedWithLastUpdate);
    }

    public void testConsumeFeed() throws Exception
    {
        Thread.sleep(2000);
        int count = splitFeed.getCount();
        int withUpdateCount = splitFeedWithLastUpdate.getCount();
        assertTrue(count > 0);
        assertTrue(withUpdateCount > 0);
        assertTrue(count > withUpdateCount);
        Thread.sleep(3000);
        //We should only receive entries once
        assertEquals(count, splitFeed.getCount());
        assertEquals(withUpdateCount, splitFeedWithLastUpdate.getCount());

    }
}