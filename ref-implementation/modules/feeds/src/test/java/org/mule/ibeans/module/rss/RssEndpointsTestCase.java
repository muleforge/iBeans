/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.module.rss;

import org.mule.api.endpoint.EndpointURI;
import org.mule.endpoint.MuleEndpointURI;
import org.mule.tck.AbstractMuleTestCase;

public class RssEndpointsTestCase extends AbstractMuleTestCase
{
    public void testEndpoint() throws Exception
    {
        String url = "rss:http://foobar.blogspot.com/entries";
        EndpointURI endpointUri = new MuleEndpointURI(url, muleContext);
        endpointUri.initialise();

        assertEquals("rss", endpointUri.getSchemeMetaInfo());
        // it's up to the client to actually strip off the method name if necessary
        assertEquals("http://foobar.blogspot.com/entries", endpointUri.getAddress());

        //Make sure this is compatible with the http transport
        endpointUri = new MuleEndpointURI("http://foobar.blogspot.com/entries", muleContext);
        endpointUri.initialise();
        assertEquals("http://foobar.blogspot.com/entries", endpointUri.getAddress());


    }
}