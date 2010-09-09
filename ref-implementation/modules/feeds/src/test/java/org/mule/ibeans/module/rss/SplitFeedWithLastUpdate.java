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

import org.mule.api.annotations.Schedule;
import org.mule.api.annotations.param.Payload;
import org.mule.ibeans.channels.FEED;
import org.mule.module.annotationx.api.Receive;

import com.sun.syndication.feed.synd.SyndEntry;

import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Singleton;

@Singleton
public class SplitFeedWithLastUpdate
{
    private AtomicInteger count = new AtomicInteger(0);

    @Schedule(interval = 1000)
    @Receive(uri = "${feed.uri}", properties = FEED.LAST_UPDATE_DATE + "=2009-03-01")
    public void readFeed(@Payload SyndEntry entry) throws Exception
    {
        count.getAndIncrement();
    }

    public int getCount()
    {
        return count.get();
    }
}