/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.twitter;

import org.mule.ibeans.api.client.ExceptionListenerAware;
import org.mule.ibeans.api.client.State;
import org.mule.ibeans.api.client.Usage;
import org.mule.ibeans.api.client.authentication.HttpBasicAuthentication;
import org.mule.ibeans.api.client.filters.JsonErrorFilter;
import org.mule.ibeans.api.client.filters.XmlErrorFilter;
import org.mule.ibeans.api.client.params.ReturnType;
import org.mule.ibeans.api.client.params.UriParam;

/**
 * TODO
 */
@Usage("The Twitter iBean provides a simple bean interface to the Twitter REST API.  Currently it only supports")
@JsonErrorFilter(expr = "error!=0", errorCode = "error")
@XmlErrorFilter(expr = "/status/error > 0", errorCode = "/status/error")
public interface TwitterBase extends HttpBasicAuthentication, ExceptionListenerAware
{
    public enum FORMAT
    {
        JSON("json"),
        XML("xml"),
        ATOM("atom"),
        RSS("rss");

        private String value;

        private FORMAT(String value)
        {
            this.value = value;
        }

        public String toString()
        {
            return value;
        }
    }

    @UriParam("format")
    public static final FORMAT DEFAULT_TWITTER_FORMAT = FORMAT.JSON;

    @ReturnType
    public static final Class DEFAULT_RETURN_TYPE = String.class;

    @State
    public void setFormat(@UriParam("format") FORMAT format, @ReturnType Class returnType);
}