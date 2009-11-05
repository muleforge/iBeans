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
import org.mule.api.endpoint.OutboundEndpoint;
import org.mule.config.annotations.endpoints.Channel;
import org.mule.ibeans.api.application.Send;
import org.mule.ibeans.config.ChannelConfigBuilder;
import org.mule.impl.endpoint.AbstractEndpointAnnotationParser;
import org.mule.impl.endpoint.AnnotatedEndpointData;
import org.mule.impl.endpoint.MEP;

import java.lang.annotation.Annotation;
import java.lang.reflect.Member;
import java.util.Collections;
import java.util.Map;

/**
 * Responsible for parsing the {@link org.mule.ibeans.api.application.Send} annotation.  This is an iBeans
 * framework class and cannot be used in any other context.
 * <p/>
 * Note that the 'split' attribute is experimental and may not be added for the final release. The 'split' annotation is
 * short form for the {@link org.mule.config.annotations.concept.Splitter} annotation.
 */
public class SendAnnotationParser extends AbstractEndpointAnnotationParser
{
    @Override
    public OutboundEndpoint parseOutboundEndpoint(Annotation annotation, Map metaInfo) throws MuleException
    {
        Send send = (Send) annotation;
        ChannelConfigBuilder builder = lookupConfig(send.config(), ChannelConfigBuilder.class);
        if (builder != null)
        {
            return builder.buildSendChannel();
        }
        else
        {
            return super.parseOutboundEndpoint(annotation, Collections.EMPTY_MAP);
        }
    }

    protected AnnotatedEndpointData createEndpointData(Annotation annotation) throws MuleException
    {
        Send send = (Send) annotation;
        AnnotatedEndpointData epd = new AnnotatedEndpointData(MEP.OutOnly);
        epd.setConnectorName(send.config());
        epd.setAddress(send.uri());
        epd.setName(send.id());
        epd.setProperties(convertProperties(send.properties()));
        if (send.split().length() > 0)
        {
            epd.getProperties().put("split-expression", send.split());
        }
        return epd;
    }

    protected String getIdentifier()
    {
        return Send.class.getAnnotation(Channel.class).identifer();
    }

    @Override
    public boolean supports(Annotation annotation, Class clazz, Member member)
    {
        if (clazz.isInterface())
        {
            //You cannot use the @Send annotation on an interface
            return false;
        }
        return super.supports(annotation, clazz, member);
    }
}