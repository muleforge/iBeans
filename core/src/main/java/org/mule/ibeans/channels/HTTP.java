/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.channels;

import org.mule.transport.http.HttpConnector;

/**
 * Defines the properties you can set on HTTP channels. These can be used in the {@link org.mule.ibeans.api.client.Call} annotation
 * for configuring the 'properties' attribute. i.e.
 * <code>
 * &amp;#064;Call(uri = "http://foo.com/{bar}", properties = {HTTP.METHOD_POST, HTTP.FOLLOW_REDIRECTS})
 * public String getStuff(@UriParam("bar") String bar) throws CallException;
 * </code>
 * <p/>
 * This class can be used as a static import.
 */

public interface HTTP
{
    /**
     * The HTTP method property key
     */
    public static final String METHOD_KEY = HttpConnector.HTTP_METHOD_PROPERTY;
    /**
     * Perform an HTTP POST call
     */
    public static final String POST = METHOD_KEY + "=POST";
    /**
     * Perform an HTTP GET call
     */
    public static final String GET = METHOD_KEY + "=GET";
    /**
     * Perform an HTTP PUT call
     */
    public static final String PUT = METHOD_KEY + "=PUT";
    /**
     * Perform an HTTP DELETE call
     */
    public static final String DELETE = METHOD_KEY + "=DELETE";
    /**
     * Perform an HTTP HEAD call
     */
    public static final String HEAD = METHOD_KEY + "=HEAD";
    /**
     * Perform an HTTP OPTIONS call
     */
    public static final String OPTIONS = METHOD_KEY + "=OPTIONS";

    /**
     * Whether redirects should be followed.  Redirects return an HTTP status code in the range of 300-304. The HTTP spec
     * defines response headers that can be used to redirect to the next page.
     */
    public static final String FOLLOW_REDIRECTS = "followRedirects=true";

}
