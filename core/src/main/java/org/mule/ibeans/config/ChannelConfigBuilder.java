/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.config;

import org.mule.api.MuleContext;
import org.mule.api.MuleException;
import org.mule.api.NamedObject;
import org.mule.api.endpoint.EndpointBuilder;
import org.mule.api.endpoint.InboundEndpoint;
import org.mule.api.endpoint.OutboundEndpoint;
import org.mule.api.routing.filter.Filter;
import org.mule.api.security.EndpointSecurityFilter;
import org.mule.api.transformer.Transformer;
import org.mule.api.transport.Connector;
import org.mule.ibeans.IBeansException;
import org.mule.impl.registry.RegistryMap;
import org.mule.routing.filters.logic.AndFilter;
import org.mule.routing.filters.logic.NotFilter;
import org.mule.routing.filters.logic.OrFilter;
import org.mule.util.TemplateParser;

import java.util.Arrays;

/**
 * TODO
 */
public class ChannelConfigBuilder implements NamedObject
{
    protected TemplateParser parser = TemplateParser.createAntStyleParser();


    private EndpointBuilder endpointBuilder;

    private String channelId;

    private MuleContext muleContext;

    public ChannelConfigBuilder(String channelId, String uri, MuleContext muleContext) throws IBeansException
    {
        super();
        this.channelId = channelId;
        this.muleContext = muleContext;
        try
        {
            endpointBuilder = muleContext.getRegistry().lookupEndpointFactory()
                    .getEndpointBuilder(getPropertyValue(uri, muleContext));
            endpointBuilder.setMuleContext(muleContext);
            endpointBuilder.setName(channelId);
        }
        catch (MuleException e)
        {
            throw new IBeansException(e);
        }
    }

    public ChannelConfigBuilder setConnector(Connector connector)
    {
        try
        {
            connector.setName(channelId + ".connector");
            muleContext.getRegistry().registerConnector(connector);
        }
        catch (MuleException e)
        {
            //TODo Fix
            throw new RuntimeException(e);
        }
        endpointBuilder.setConnector(connector);
        return this;
    }

    public ChannelConfigBuilder addTransformer(Transformer transformer)
    {
        endpointBuilder.addTransformer(transformer);
        return this;
    }

    public ChannelConfigBuilder addTransformers(Transformer... transformer)
    {
        endpointBuilder.setTransformers(Arrays.asList(transformer));
        return this;
    }

    public ChannelConfigBuilder addResponseTransformer(Transformer transformer)
    {
        endpointBuilder.addResponseTransformer(transformer);
        return this;
    }

    public ChannelConfigBuilder addResponseTransformers(Transformer... transformer)
    {
        endpointBuilder.setResponseTransformers(Arrays.asList(transformer));
        return this;
    }


    public ChannelConfigBuilder andFilters(Filter... filters)
    {
        endpointBuilder.setFilter(new AndFilter(filters));
        return this;
    }

    public ChannelConfigBuilder orFilter(Filter... filters)
    {
        endpointBuilder.setFilter(new OrFilter(filters));
        return this;
    }

    public ChannelConfigBuilder notFilter(Filter filter)
    {
        endpointBuilder.setFilter(new NotFilter(filter));
        return this;
    }

    public ChannelConfigBuilder filter(Filter filter)
    {
        endpointBuilder.setFilter(filter);
        return this;
    }

    public ChannelConfigBuilder setSecurityFilter(EndpointSecurityFilter filter)
    {
        endpointBuilder.setSecurityFilter(filter);
        return this;
    }

    public ChannelConfigBuilder setEncoding(String encoding)
    {
        endpointBuilder.setEncoding(encoding);
        return this;
    }

    public ChannelConfigBuilder addProperty(String key, Object value)
    {
        endpointBuilder.setProperty(key, value);
        return this;
    }

    public InboundEndpoint buildReceiveChannel() throws IBeansException
    {
        try
        {
            endpointBuilder.setSynchronous(false);
            InboundEndpoint ep = endpointBuilder.buildInboundEndpoint();
            muleContext.getRegistry().registerEndpoint(ep);
            return ep;
        }
        catch (MuleException e)
        {
            throw new IBeansException(e);
        }
    }

    public InboundEndpoint buildReceiveAndReplyChannel() throws IBeansException
    {
        try
        {
            endpointBuilder.setSynchronous(true);
            InboundEndpoint ep = endpointBuilder.buildInboundEndpoint();
            muleContext.getRegistry().registerEndpoint(ep);
            return ep;
        }
        catch (MuleException e)
        {
            throw new IBeansException(e);
        }
    }

    public OutboundEndpoint buildSendChannel() throws IBeansException
    {
        try
        {
            endpointBuilder.setSynchronous(false);
            OutboundEndpoint ep = endpointBuilder.buildOutboundEndpoint();
            muleContext.getRegistry().registerEndpoint(ep);
            return ep;
        }
        catch (MuleException e)
        {
            throw new IBeansException(e);
        }
    }

    protected String getPropertyValue(String key, MuleContext context)
    {
        return parser.parse(new RegistryMap(context.getRegistry()), key);
    }

    public void setName(String name)
    {
        throw new UnsupportedOperationException("setName");
    }

    public String getName()
    {
        return channelId + ".builder";
    }
}

