/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.module.http;

import org.mule.transport.AbstractMessageAdapter;
import org.mule.transport.NullPayload;
import org.mule.transport.http.HttpConstants;
import org.mule.transport.http.ReleasingInputStream;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpMethod;

/**
 * TODO
 */
public class HttpClientMessageAdapter extends AbstractMessageAdapter
{
    private Object message;

    public HttpClientMessageAdapter(HttpMethod method) throws IOException
    {
        InputStream is;
        is = method.getResponseBodyAsStream();

        if (is == null)
        {
            message = NullPayload.getInstance();
        }
        else
        {
            message = new ReleasingInputStream(is, method);
        }

        // Standard headers
        Map<String, String> headerProps = new HashMap<String, String>();
        Header[] headers = method.getResponseHeaders();
        String name;
        for (int i = 0; i < headers.length; i++)
        {
            name = headers[i].getName();
            if (name.startsWith(HttpConstants.X_PROPERTY_PREFIX))
            {
                name = name.substring(2);
            }
            headerProps.put(name, headers[i].getValue());
        }
        // Set Mule Properties
        addInboundProperties(headerProps);
    }

    public Object getPayload()
    {
        return message;
    }
}
