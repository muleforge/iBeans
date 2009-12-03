/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.channels;

/**
 * Channel properties and specific header names supported by the ATOM channel
 */
public interface ATOM
{
    /**
     * Whether to split the incoming feed into entries. By default this is true and for most usecases, splitting is what you
     * want.  Note that the Feed object itself is available as a header called {@link #FEED_HEADER}.
     */
    public static final String DONT_SPLIT_FEED = "splitFeed=false";

    /**
     * Can be set on a channel to filter out any feed entries before the given date/time.  This is only used if the feed
     * is split.  the format for the date is is: yyyy-MM-dd HH:MM:ss, for example 2008-12-25 13:00:00.  If only the date
     * is important you can omit the time part.
     */
    public static final String LAST_UPDATE_DATE = "lastUpdate";

    /**
     * The header name used to store the Feed object on the incoming message.  This is only set if {@link #DONT_SPLIT_FEED} is
     * not used.
     */
    public static final String FEED_HEADER = "AtomFeed";
}
