/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.internal.client;

import org.mule.api.MuleContext;
import org.mule.api.MuleMessage;
import org.mule.api.endpoint.EndpointURI;
import org.mule.api.endpoint.OutboundEndpoint;
import org.mule.api.transformer.Transformer;
import org.mule.endpoint.DefaultOutboundEndpoint;
import org.mule.endpoint.MuleEndpointURI;
import org.mule.ibeans.internal.ext.DynamicOutboundEndpoint;
import org.mule.impl.endpoint.AnnotatedEndpointData;
import org.mule.transport.AbstractConnector;
import org.mule.transport.service.TransportFactory;
import org.mule.util.BeanUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A dynamic outbound endpoint defined when using the {@link org.mule.ibeans.api.client.Call} annotation.  A CallOutboundEndpoint
 * is generated when the Call method has a one or more payloads defined using {@link org.mule.ibeans.api.client.params.Payload} or {@link org.mule.ibeans.api.client.params.PayloadParam} annotations
 * or one or more headers defined using the {@link org.mule.ibeans.api.client.params.HeaderParam} annotation.
 * annotations.
 * <p/>
 * The endpoint scheme is the only part of the URI that cannot be replaced at runtime.
 *
 * @see org.mule.ibeans.internal.client.CallRequestEndpoint
 */
public class CallOutboundEndpoint extends DynamicOutboundEndpoint
{
    public static final String URI_PARAM_PROPERTIES = "mule.uri.params";

    /**
     * logger used by this class
     */
    protected transient final Log logger = LogFactory.getLog(CallOutboundEndpoint.class);
    private static final long serialVersionUID = 1861985949279708638L;

    //Need read/write transformers
    private List<Transformer> transformers = new ArrayList<Transformer>();
    private List<Transformer> responseTransformers = new ArrayList<Transformer>();

    public CallOutboundEndpoint(MuleContext context, AnnotatedEndpointData epData)
    {
        super(createOutboundEndpoint(context, epData), epData.getAddress());
    }

    private static OutboundEndpoint createOutboundEndpoint(MuleContext context, AnnotatedEndpointData epData)
    {
        try
        {
            String address = epData.getAddress();
            int i = address.indexOf(":/");
            String scheme;
            if (i > -1)
            {
                scheme = address.substring(0, i);
                address = scheme + "://dynamic";
                //This is used for creating the connector, since we don't know if the actual URI address is a vaild URI.
                EndpointURI tempUri = new MuleEndpointURI(address, context);
                AbstractConnector cnn = (AbstractConnector) new TransportFactory(context).createConnector(tempUri);
                //Not needed anymore
                //tempUri = null;
                context.getRegistry().registerConnector(cnn);
                //This allows connector properties to be set as propertieds on the endpoint
                Map props = epData.getProperties();
                if (props == null)
                {
                    props = new HashMap();
                }
                else
                {
                    BeanUtils.populateWithoutFail(cnn, props, false);
                }
                return new DefaultOutboundEndpoint(cnn, tempUri, null, null,
                        epData.getName(), props, null, null, true, null, epData.isSynchronous(),
                        context.getConfiguration().getDefaultResponseTimeout(), null,
                        context.getConfiguration().getDefaultEncoding(),
                        null, context, null);
            }
            else
            {
                throw new IllegalArgumentException("When defining a dynamic endpoint the endpoint scheme must be set i.e. http://{dynamic}");
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected Map<String, Object> getPropertiesForTemplate(MuleMessage message)
    {
        Map<String, Object> props = (Map) message.removeProperty(URI_PARAM_PROPERTIES);
        if (props == null)
        {
            throw new IllegalStateException(URI_PARAM_PROPERTIES + " not set on message");
        }
        return props;
    }

    @Override
    public List getTransformers()
    {
        if (transformers.size() == 0)
        {
            List temp = ((AbstractConnector) getConnector()).getDefaultOutboundTransformers();
            if (temp != null)
            {
                transformers.addAll(((AbstractConnector) getConnector()).getDefaultOutboundTransformers());
                for (Transformer transformer : transformers)
                {
                    transformer.setEndpoint(this);
                }
            }
        }
        return transformers;
    }

    @Override
    public List getResponseTransformers()
    {
        if (responseTransformers.size() == 0)
        {
            List temp = ((AbstractConnector) getConnector()).getDefaultResponseTransformers();
            if (temp != null)
            {
                responseTransformers.addAll(temp);
                for (Transformer transformer : responseTransformers)
                {
                    transformer.setEndpoint(this);
                }
            }
        }
        return responseTransformers;
    }
}