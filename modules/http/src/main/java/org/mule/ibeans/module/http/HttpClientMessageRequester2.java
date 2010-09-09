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

import org.mule.DefaultMuleMessage;
import org.mule.api.MuleMessage;
import org.mule.api.endpoint.InboundEndpoint;
import org.mule.api.transport.ReceiveException;
import org.mule.transport.NullPayload;
import org.mule.transport.http.HttpClientMessageRequester;
import org.mule.transport.http.HttpConstants;
import org.mule.transport.http.HttpMuleMessageFactory;
import org.mule.transport.http.i18n.HttpMessages;
import org.mule.util.MapUtils;
import org.mule.util.StringUtils;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;

/**
 * An improved implementation of the HttpClientMessageRequester that honours ETags and authentication
 */
public class HttpClientMessageRequester2 extends HttpClientMessageRequester
{
    protected String etag = null;
    private boolean checkEtag = false;

    public HttpClientMessageRequester2(InboundEndpoint endpoint)
    {
        super(endpoint);
        this.receiveTransformer.setMuleContext(getConnector().getMuleContext());
        checkEtag = MapUtils.getBooleanValue(endpoint.getProperties(), "checkEtag", false);
    }

    /**
     * Make a specific request to the underlying transport
     *
     * @param timeout the maximum time the operation should block before returning.
     *                The call should return immediately if there is data available. If
     *                no data becomes available before the timeout elapses, null will be
     *                returned
     * @return the result of the request wrapped in a MuleMessage object. Null will be
     *         returned if no data was avaialable
     * @throws Exception if the call to the underlying protocal cuases an exception
     */
    protected MuleMessage doRequest(long timeout) throws Exception
    {
        HttpMethod httpMethod = new GetMethod(endpoint.getEndpointURI().getAddress());

        if (endpoint.getProperties().containsKey(HttpConstants.HEADER_AUTHORIZATION))
        {
            httpMethod.setDoAuthentication(true);
            client.getParams().setAuthenticationPreemptive(true);
            httpMethod.setRequestHeader(HttpConstants.HEADER_AUTHORIZATION, (String) endpoint.getProperty(HttpConstants.HEADER_AUTHORIZATION));
        }

        boolean releaseConn = false;
        try
        {
            HttpClient client = new HttpClient();

            if (etag != null && checkEtag)
            {
                httpMethod.setRequestHeader(HttpConstants.HEADER_IF_NONE_MATCH, etag);
            }
            client.executeMethod(httpMethod);

            if (httpMethod.getStatusCode() < 400)
            {
                MuleMessage message = new HttpMuleMessageFactory(connector.getMuleContext()).create(httpMethod, null /* encoding */);
                etag = message.getInboundProperty(HttpConstants.HEADER_ETAG, null);

                if (httpMethod.getStatusCode() == HttpStatus.SC_OK || (httpMethod.getStatusCode() != HttpStatus.SC_NOT_MODIFIED || !checkEtag))
                {
                    if (StringUtils.EMPTY.equals(message.getPayload()))
                    {
                        releaseConn = true;
                    }
                    return message;
                }
                else
                {
                    //Not modified, we should really cache the whole message and return it
                    return new DefaultMuleMessage(NullPayload.getInstance(), getConnector().getMuleContext());
                }
            }
            else
            {
                releaseConn = true;
                throw new ReceiveException(
                        HttpMessages.requestFailedWithStatus(httpMethod.getStatusLine().toString()),
                        endpoint, timeout);
            }

        }
        catch (ReceiveException
                e)
        {
            releaseConn = true;
            throw e;
        }
        catch (Exception
                e)
        {
            releaseConn = true;
            throw new ReceiveException(endpoint, timeout, e);
        }
        finally
        {
            if (releaseConn)
            {
                httpMethod.releaseConnection();
            }
        }
    }
}
