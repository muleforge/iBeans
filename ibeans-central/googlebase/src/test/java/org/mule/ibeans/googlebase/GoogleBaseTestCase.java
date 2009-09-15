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

import static org.mule.ibeans.IBeansSupport.select;
import org.mule.ibeans.api.client.IntegrationBean;
import org.mule.ibeans.test.AbstractIBeansTestCase;

import java.util.List;

import org.apache.abdera.model.Feed;
import org.w3c.dom.Document;
import org.w3c.dom.Node;


public class GoogleBaseTestCase extends AbstractIBeansTestCase
{
    /**
     * Insert here the developer key obtained for an "installed application" at
     * http://code.google.com/apis/base/signup.html
     */
    protected static final String DEVELOPER_KEY = "ABQIAAAAA2GeN5kTxvuEUlb_9YIPSRT2yXp_ZAY8_ufC3CFXhHIE1NvwkxRPxBkLmXdQWBM-y-mSe3u3JtOBXw";

    /**
     * The query that is sent over to the Google Base data API server.
     */
    protected static final String QUERY = "Mule ESB";

    @IntegrationBean
    private GoogleBaseIBean googlebase;

    @Override
    protected void doSetUp() throws Exception
    {
        googlebase.init(DEVELOPER_KEY, Feed.class);
    }

    /**
     * Accessor for the ibean so that it can be overridden in a Mock test case
     *
     * @return the ibean proxy instance
     */
    protected GoogleBaseIBean getIBean()
    {
        return googlebase;
    }


    public void testSearch() throws Exception
    {
        Feed reply = getIBean().search(QUERY);
        assertNotNull(reply);
        assertEquals("http://www.w3.org/2005/Atom", reply.getNamespaces().get(""));

        assertEquals(25, reply.getEntries().size());
    }

    public void testMaxResultsWithDocument() throws Exception
    {
        Feed reply = getIBean().search(QUERY, 100);
        assertNotNull(reply);
        assertEquals(100, reply.getEntries().size());
    }

    public void testNamespace() throws Exception
    {
        Feed reply = getIBean().search(QUERY);
        assertNotNull(reply);
        assertEquals("http://base.google.com/ns/1.0", reply.getNamespaces().get("g"));
        assertTrue(reply.getAuthors().size() > 0);
    }

    public void testReturnTypes() throws Exception
    {
        getIBean().init(DEVELOPER_KEY, Document.class);
        Document doc = getIBean().search(QUERY);
        assertNotNull(doc);

        List<Node> nodes = select("//:entry/:title", doc);
        assertEquals(25, nodes.size());

        getIBean().init(DEVELOPER_KEY, String.class);
        String xml = getIBean().search(QUERY);
        assertNotNull(xml);
        assertTrue(xml.length() > 0);
    }
}