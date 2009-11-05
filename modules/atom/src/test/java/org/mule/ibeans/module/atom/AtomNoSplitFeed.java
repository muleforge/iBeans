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

import org.mule.ibeans.api.application.Receive;
import org.mule.ibeans.api.application.Schedule;
import org.mule.ibeans.channels.ATOM;

import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Singleton;

import org.apache.abdera.model.Feed;

@Singleton
public class AtomNoSplitFeed
{
    private AtomicInteger count = new AtomicInteger(0);

    @Schedule(interval = 6000)
    @Receive(uri = "atom:http://rossmason.blogspot.com/feeds/posts/default", properties = ATOM.DONT_SPLIT_FEED)
    public void readFeed(Feed feed) throws Exception
    {
        count.getAndAdd(feed.getEntries().size());
    }

    public int getCount()
    {
        return count.get();
    }
}