/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.config;

/**
 * TODO
 */
public interface IBeansProperties
{
    public static final String IBEANS_PREFIX = "ibeans.";
    public static final String PROPERTY_USERNAME = IBEANS_PREFIX + "central.username";
    public static final String PROPERTY_PASSWORD = IBEANS_PREFIX + "central.password";

    public static final String DEBUG_PROXY_HOST = IBEANS_PREFIX + "debug.proxy.host";
    public static final String DEBUG_PROXY_PORT = IBEANS_PREFIX + "debug.proxy.port";


    /**
     * Default values *
     */
    public static final String DEFAULT_PROXY_HOST = "127.0.0.1";

}