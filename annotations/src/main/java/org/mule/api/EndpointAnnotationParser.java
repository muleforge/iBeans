/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.api;

import org.mule.expression.ExpressionConfig;
import org.mule.expression.transformers.ExpressionArgument;
import org.mule.api.endpoint.ImmutableEndpoint;
import org.mule.api.endpoint.OutboundEndpoint;
import org.mule.api.endpoint.InboundEndpoint;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Member;

/**
 * An SPI interface that will process an Endpoint annotation. Note that the Annotation must be annotated with the {@link @Endpoint }
 * annotation.
 */
public interface EndpointAnnotationParser
{
    /**
     * Creates an outbound endpoint from the annotation.
     *
     * @param annotation the annotation to process
     * @return a new {@link org.mule.api.endpoint.OutboundEndpoint} object configured according to the annotation
     * @throws MuleException if the outbound endpoint cannot be created. A Mule-specific error will be thrown.
     */
    public OutboundEndpoint parseOutboundEndpoint(Annotation annotation) throws MuleException;

    /**
     * Creates an inbound endpoint from the annotation.
     *
     * @param annotation the annotation to process
     * @return a new {@link org.mule.api.endpoint.InboundEndpoint} object configured according to the annotation
     * @throws MuleException if the inbound endpoint cannot be created. A Mule-specific error will be thrown.
     */
    public InboundEndpoint parseInboundEndpoint(Annotation annotation) throws MuleException;

    /**
     * Determines whether this parser can process the current annotation.  The clazz and member params are passed in
     * so that further validation be done on the location, type or name of these elements.
     *
     * @param annotation the annotation being processed
     * @param clazz      the class on which the annotation was found
     * @param member     the member on which the annotation was found inside the class. This is only set when the annotation
     *                   was either set on a {@link java.lang.reflect.Method}, {@link java.lang.reflect.Field} or {@link java.lang.reflect.Constructor}
     *                   class member, otherwise this value is null.
     * @return true if this parser supports the current annotation, false otherwise
     */
    public boolean supports(Annotation annotation, Class clazz, Member member);
}