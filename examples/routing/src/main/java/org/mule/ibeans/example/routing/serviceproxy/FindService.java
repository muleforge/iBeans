/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.example.routing.serviceproxy;

import org.mule.ibeans.api.client.Call;
import org.mule.ibeans.api.client.CallException;
import org.mule.ibeans.api.client.params.UriParam;

/**
 * A client interface youed to interact with 3 different search engines.
 */
public interface FindService
{
    //deliberate 404 error
    @Call(uri = "http://www.google.com/searchX?q={term}")
    public String searchGoogle(@UriParam("term") String searchTerm) throws CallException;

    @Call(uri = "http://search.yahoo.com/search?p={term}")
    public String searchYahoo(@UriParam("term") String searchTerm) throws Exception;

    @Call(uri = "http://www.ask.com/web?q={term}&search=search")
    public String searchAsk(@UriParam("term") String searchTerm) throws CallException;
}
