/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.internal.parsers;

import org.mule.api.MuleException;
import org.mule.api.endpoint.InboundEndpoint;
import org.mule.config.annotations.endpoints.Channel;
import org.mule.ibeans.api.application.ReceiveAndReply;
import org.mule.ibeans.config.ChannelConfigBuilder;
import org.mule.impl.endpoint.AbstractEndpointAnnotationParser;
import org.mule.impl.endpoint.AnnotatedEndpointData;
import org.mule.impl.endpoint.MEP;

import java.lang.annotation.Annotation;
import java.lang.reflect.Member;
import java.util.Collections;
import java.util.Map;

/**
 * Responsible for parsing the {@link org.mule.ibeans.api.application.ReceiveAndReply} annotation.  This is an iBeans
 * framework class and cannot be used in any other context.
 */
public class ReceiveAndReplyAnnotationParser extends AbstractEndpointAnnotationParser
{
    @Override
    public InboundEndpoint parseInboundEndpoint(Annotation annotation, Map metaInfo) throws MuleException
    {
        ReceiveAndReply receiveAndReply = (ReceiveAndReply) annotation;
        ChannelConfigBuilder builder = lookupConfig(receiveAndReply.config(), ChannelConfigBuilder.class);
        if (builder != null)
        {
            return builder.buildReceiveAndReplyChannel();
        }
        else
        {
            return super.parseInboundEndpoint(annotation, Collections.EMPTY_MAP);
        }
    }

    protected AnnotatedEndpointData createEndpointData(Annotation annotation) throws MuleException
    {
        ReceiveAndReply receiveAndReply = (ReceiveAndReply) annotation;
        AnnotatedEndpointData epd = new AnnotatedEndpointData(MEP.InOut);
        epd.setConnectorName(receiveAndReply.config());
        epd.setAddress(receiveAndReply.uri());
        epd.setName(receiveAndReply.id());
        epd.setProperties(convertProperties(receiveAndReply.properties()));
        return epd;
    }

    protected String getIdentifier()
    {
        return ReceiveAndReply.class.getAnnotation(Channel.class).identifer();
    }

    @Override
    public boolean supports(Annotation annotation, Class clazz, Member member)
    {
        return !clazz.isInterface() && super.supports(annotation, clazz, member);
    }
}