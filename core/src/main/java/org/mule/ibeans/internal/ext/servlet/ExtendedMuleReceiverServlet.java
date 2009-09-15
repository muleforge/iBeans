/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.internal.ext.servlet;

import org.mule.DefaultMuleMessage;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.api.endpoint.EndpointException;
import org.mule.api.endpoint.EndpointURI;
import org.mule.api.endpoint.InboundEndpoint;
import org.mule.api.lifecycle.InitialisationException;
import org.mule.api.transport.MessageReceiver;
import org.mule.api.transport.NoReceiverForEndpointException;
import org.mule.endpoint.DynamicURIInboundEndpoint;
import org.mule.endpoint.MuleEndpointURI;
import org.mule.routing.filters.WildcardFilter;
import org.mule.transport.http.HttpConnector;
import org.mule.transport.http.HttpMessageReceiver;
import org.mule.transport.http.i18n.HttpMessages;
import org.mule.transport.servlet.HttpRequestMessageAdapter;
import org.mule.transport.servlet.MuleReceiverServlet;

import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * TODO
 */
public class ExtendedMuleReceiverServlet extends MuleReceiverServlet
{

    protected String getServletMethod(String httpMethod)
    {
        StringBuilder builder = new StringBuilder();
        builder.append("do");
        builder.append(httpMethod.substring(0, 1));
        builder.append(httpMethod.toLowerCase().substring(1));
        return builder.toString();
    }

    protected MuleMessage doMethod(HttpServletRequest request, HttpServletResponse response, String method)
            throws MuleException
    {
        MessageReceiver receiver = getReceiverForURI(request);
        MuleMessage requestMessage;
//        if (receiver.getService().getComponent() instanceof ServletComponent)
//        {
//            requestMessage = new DefaultMuleMessage(new Object[]{request, response}, muleContext);
//            requestMessage.setProperty(MuleProperties.MULE_METHOD_PROPERTY, getServletMethod(method));
//            requestMessage.setProperty(HttpConnector.HTTP_METHOD_PROPERTY, method);
//            setupRequestMessage(request, requestMessage, receiver);
//
//            routeMessage(receiver, requestMessage, request);
//            return null;
//        }
//        else
//        {
        requestMessage = new DefaultMuleMessage(new HttpRequestMessageAdapter(request), muleContext);

        requestMessage.setProperty(HttpConnector.HTTP_METHOD_PROPERTY, method);
        setupRequestMessage(request, requestMessage, receiver);

        return routeMessage(receiver, requestMessage, request);
        //   }
    }


    protected MessageReceiver getReceiverForURI(HttpServletRequest httpServletRequest)
            throws EndpointException
    {
        String uri = getReceiverName(httpServletRequest);
        if (uri == null)
        {
            throw new EndpointException(
                    HttpMessages.unableToGetEndpointUri(httpServletRequest.getRequestURI()));
        }

        MessageReceiver receiver = (MessageReceiver) getReceivers().get(uri);

        // Lets see if the uri matches up with the last part of
        // any of the receiver keys.
        if (receiver == null)
        {
            receiver = HttpMessageReceiver.findReceiverByStem(connector.getReceivers(), uri);
        }

        if (receiver == null)
        {
            //Now match wilcards
            for (Iterator<String> iterator = getReceivers().keySet().iterator(); iterator.hasNext();)
            {
                String key = iterator.next();
                if (new WildcardFilter(key).accept(uri))
                {
                    receiver = (MessageReceiver) connector.getReceivers().get(key);
                }
            }
            if (receiver == null)
            {
                throw new NoReceiverForEndpointException("No receiver found for endpointUri: " + uri);
            }
        }

        InboundEndpoint endpoint = receiver.getEndpoint();

        // Ensure that this receiver is using a dynamic (mutable) endpoint
        if (!(endpoint instanceof DynamicURIInboundEndpoint))
        {
            endpoint = new DynamicURIInboundEndpoint(receiver.getEndpoint());
            receiver.setEndpoint(endpoint);
        }

        // Tell the dynamic endpoint about our new URL
        //Note we don't use the servlet: prefix since we need to be dealing with the raw endpoint here
        EndpointURI epURI = new MuleEndpointURI(getRequestUrl(httpServletRequest), muleContext);

        try
        {
            epURI.initialise();
            epURI.getParams().setProperty("servlet.endpoint", "true");
            ((DynamicURIInboundEndpoint) endpoint).setEndpointURI(epURI);
        }
        catch (InitialisationException e)
        {
            throw new EndpointException(e);
        }
        return receiver;
    }
}
