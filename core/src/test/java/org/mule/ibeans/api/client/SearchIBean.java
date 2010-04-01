/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.api.client;

import org.mule.ibeans.api.client.params.UriParam;

import java.net.URL;

public interface SearchIBean
{
    //deliberate 404 error
    @Call(uri = "http://www.google.com/searchX?q={term}")
    public String searchGoogle(@UriParam("term") String searchTerm) throws CallException;

    @Call(uri = "http://search.yahoo.com/search?p={term}")
    public String searchYahoo(@UriParam("term") String searchTerm) throws Exception;

    @Call(uri = "http://www.ask.com/web?q={term}&search=search")
    public String searchAsk(@UriParam("term") String searchTerm) throws CallException;


    @Call(uri = "http://www.ask.com/web?q={term}&search=search")
    @Return("header:ibeans.call.uri")
    public String searchAskAndReturnURLString(@UriParam("term") String searchTerm) throws CallException;

    @Call(uri = "http://www.ask.com/web?q={term}&search=search")
    @Return("header:ibeans.call.uri")
    public URL searchAskAndReturnURL(@UriParam("term") String searchTerm) throws CallException;

    //IBEANS-184 : make sure we can handle void methods
    @Call(uri = "http://www.ask.com/web?q={term}&search=search")
    public void searchAskAndReturnVoid(@UriParam("term") String searchTerm) throws CallException;
}
