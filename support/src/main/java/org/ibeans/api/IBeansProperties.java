/*
 * $Id: IBeansProperties.java 273 2010-02-09 22:28:36Z ross $
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.ibeans.api;

/**
 * Constants for system properties or other properties used by iBeans
 */
public interface IBeansProperties
{
    public static final String IBEANS_PREFIX = "ibeans.";

    //System properties
    public static final String PROPERTY_USERNAME = IBEANS_PREFIX + "central.username";
    public static final String PROPERTY_PASSWORD = IBEANS_PREFIX + "central.password";

    public static final String DEBUG_PROXY_HOST = IBEANS_PREFIX + "debug.proxy.host";
    public static final String DEBUG_PROXY_PORT = IBEANS_PREFIX + "debug.proxy.port";

    public static final String LOG_RESPONSES_DIR = IBEANS_PREFIX + "log.responses";

    //Internal properties
    public static final String ENDPOINT_METHOD = IBEANS_PREFIX + "endpoint.method";

    /**
     * Default values *
     */
    public static final String DEFAULT_PROXY_HOST = "127.0.0.1";

}
