/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.googlebase;

import org.mule.ibeans.api.client.Call;
import org.mule.ibeans.api.client.CallException;
import org.mule.ibeans.api.client.State;
import org.mule.ibeans.api.client.params.ReturnType;
import org.mule.ibeans.api.client.params.UriParam;

public interface GoogleBaseIBean
{
    /**
     * By default search methods will return the Atom XML data as a string
     */
    @ReturnType
    public static final Class DEFAULT_RETURN_TYPE = String.class;

    /**
     * Initialize the bean for use.
     *
     * @param developerKey obtained for an "installed application" at
     *                     http://code.google.com/apis/base/signup.html
     */
    @State
    public void init(@UriParam("developer_key") String developerKey);

    /**
     * Initialize the bean for use.
     *
     * @param developerKey obtained for an "installed application" at
     *                     http://code.google.com/apis/base/signup.html
     * @param returnType   the default return type to use when the method has a variable Type T return type
     */
    @State
    public void init(@UriParam("developer_key") String developerKey, @ReturnType Class returnType);

    /**
     * Search the Google database, returns the top 25 results.
     *
     * @param query - the search term (same thing you would enter in the text box at www.google.com)
     * @return an ATOM document with the results of the search
     */
    @Call(uri = "http://base.google.com/base/feeds/snippets?q={query}&key={developer_key}")
    public <T> T search(@UriParam("query") String query) throws CallException;

    /**
     * Search the Google database.
     *
     * @param query   - the search term (same thing you would enter in the text box at www.google.com)
     * @param results - number of results desired
     * @return an ATOM document with the results of the search
     */
    @Call(uri = "http://base.google.com/base/feeds/snippets?q={query}&max-results={results}&key={developer_key}")
    public <T> T search(@UriParam("query") String query, @UriParam("results") int results) throws CallException;
}