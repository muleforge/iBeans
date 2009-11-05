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
 * Common Channel properties
 */
public interface CHANNEL
{
    /**
     * Determines the maximum number of threads a channel can use to process messages
     */
    public static final String MAX_THREADS = "threads";

    /**
     * When performing 'receive' operations such as polling an email inbox, the timeout property can
     * be used to specify how long in milliseconds to wait on a request before timing out
     */
    public static final String TIMEOUT = "timeout";
}
