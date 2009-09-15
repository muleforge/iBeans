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
import org.mule.ibeans.api.application.Receive;
import org.mule.ibeans.config.ChannelConfigBuilder;
import org.mule.impl.endpoint.AbstractEndpointAnnotationParser;
import org.mule.impl.endpoint.AnnotatedEndpointData;
import org.mule.impl.endpoint.MEP;

import java.lang.annotation.Annotation;
import java.lang.reflect.Member;

/**
 * Responsible for parsing the {@link org.mule.ibeans.api.application.Receive} annotation.  This is an iBeans
 * framework class and cannot be used in any other context.
 */
public class ReceiveAnnotationParser extends AbstractEndpointAnnotationParser
{
    @Override
    public InboundEndpoint parseInboundEndpoint(Annotation annotation) throws MuleException
    {
        Receive receive = (Receive) annotation;
        ChannelConfigBuilder builder = lookupConfig(receive.config(), ChannelConfigBuilder.class);
        if (builder != null)
        {
            return builder.buildReceiveChannel();
        }
        else
        {
            return super.parseInboundEndpoint(annotation);
        }
    }

    protected AnnotatedEndpointData createEndpointData(Annotation annotation) throws MuleException
    {
        Receive receive = (Receive) annotation;
        AnnotatedEndpointData epd = new AnnotatedEndpointData(MEP.InOnly);
        epd.setConnectorName(receive.config());
        epd.setAddress(receive.uri());
        epd.setName(receive.id());
        epd.setProperties(this.convertProperties(receive.properties()));
        return epd;
    }

    protected String getIdentifier()
    {
        return Receive.class.getAnnotation(Channel.class).identifer();
    }

    /**
     * Features like the {@link org.mule.ibeans.api.client.IntegrationBean} annotation can be used to define an service proxy
     * configuration where the annotations are configured on the interface methods.  However, it is illegal to configure
     * the @Subscribe annotation in this way.
     *
     * @param annotation the annotation being processed
     * @param clazz      the class on which the annotation was found
     * @param member     the member on which the annotation was found inside the class.  this is only set when the annotation
     *                   was either set on a {@link java.lang.reflect.Method}, {@link java.lang.reflect.Field} or {@link java.lang.reflect.Constructor}
     *                   class members, otherwise this value is null.
     * @return tue if this parser supports the current annotation and the clazz is not an interface
     * @throws IllegalArgumentException if the class parameter is an interface
     */
    @Override
    public boolean supports(Annotation annotation, Class clazz, Member member)
    {
        if (clazz.isInterface())
        {
            //You cannot use the @Receive annotation on an interface
            return false;
        }
        return super.supports(annotation, clazz, member);
    }
}