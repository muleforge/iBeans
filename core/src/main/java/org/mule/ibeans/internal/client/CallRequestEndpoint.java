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
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.api.endpoint.EndpointException;
import org.mule.api.endpoint.InboundEndpoint;
import org.mule.api.transformer.Transformer;
import org.mule.endpoint.DefaultInboundEndpoint;
import org.mule.endpoint.MuleEndpointURI;
import org.mule.ibeans.internal.ext.DynamicRequestEndpoint;
import org.mule.impl.endpoint.AnnotatedEndpointData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A dynamic inbound endpoint used for request calls defined using the {@link org.mule.ibeans.api.client.Call} annotation.
 * Note that call requests look the same as normal call endpoints except request calls do not define any headers or payload.
 * <p/>
 * The endpoint scheme is the only part of the URI that cannot be replaced at runtime.
 *
 * @see org.mule.ibeans.internal.client.CallOutboundEndpoint
 */
public class CallRequestEndpoint extends DynamicRequestEndpoint
{
    public static final String URI_PARAM_PROPERTIES = "mule.uri.params";

    /**
     * logger used by this class
     */
    protected transient final Log logger = LogFactory.getLog(CallRequestEndpoint.class);

    private static final long serialVersionUID = 1861985949279708458L;

    //This is a hack to create a ref to the transformers collection, then we add the transformers once the endpoint type has been
    //determined
    private static List<Transformer> transformers = new ArrayList<Transformer>();
    private static List<Transformer> responseTransformers = new ArrayList<Transformer>();

    public CallRequestEndpoint(MuleContext context, AnnotatedEndpointData epData)
    {
        super(createInboundEndpoint(context, epData), epData.getAddress());
    }

    private static InboundEndpoint createInboundEndpoint(MuleContext context, AnnotatedEndpointData epData)
    {
        try
        {
            return new DefaultInboundEndpoint(epData.getConnector(), new MuleEndpointURI("dynamic://null", context), transformers, responseTransformers, epData.getName(),
                    (epData.getProperties() == null ? new HashMap() : epData.getProperties()), null, null, true, null, epData.isSynchronous(),
                    context.getConfiguration().getDefaultResponseTimeout(), null,
                    context.getConfiguration().getDefaultEncoding(),
                    null, context, null);
        }
        catch (EndpointException e)
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
            try
            {
                transformers.addAll(getLocalConnector().getDefaultInboundTransformers());
                for (Transformer tran : transformers)
                {
                    tran.setEndpoint(this);
                    tran.setMuleContext(getMuleContext());
                    tran.initialise();
                }
            }
            catch (MuleException e)
            {
                throw new RuntimeException(e);
            }
        }
        return transformers;
    }

    @Override
    public List getResponseTransformers()
    {
        if (responseTransformers.size() == 0)
        {
            try
            {
                responseTransformers.addAll(getLocalConnector().getDefaultResponseTransformers());
                for (Transformer tran : responseTransformers)
                {
                    tran.setEndpoint(this);
                    tran.setMuleContext(getMuleContext());
                    tran.initialise();
                }
            }
            catch (MuleException e)
            {
                throw new RuntimeException(e);
            }
        }
        return transformers;
    }
}