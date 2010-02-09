/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.internal.parsers;

import org.mule.ibeans.internal.AnnotatedTransformerObjectProcessor;
import org.mule.ibeans.internal.IntegrationBeanAnnotatedObjectProcessor;
import org.mule.ibeans.internal.MuleiBeansAnnotatedObjectProcessor;
import org.mule.impl.endpoint.DefaultAnnotationsParserFactory;

/**
 * TODO
 */
public class IBeansAnnotationsParserFactory extends DefaultAnnotationsParserFactory
{
    protected void addDefaultParsers()
    {
        super.addDefaultParsers();
        registerEndpointParser(new ReceiveAnnotationParser());
        registerEndpointParser(new ReceiveAndReplyAnnotationParser());
        registerEndpointParser(new SendAnnotationParser());
        registerEndpointParser(new ScheduleAnnotationParser());
        registerEndpointParser(new CallAnnotationParser());

        registerExpressionParser(new MessagePayloadAnnotationParser());
        registerExpressionParser(new ReceivedHeadersAnnotationParser());
        registerExpressionParser(new ReceivedAttachmentsAnnotationParser());
        registerExpressionParser(new SendHeadersAnnotationParser());
        registerExpressionParser(new SendAttachmentsAnnotationParser());

        processors.add(new MuleiBeansAnnotatedObjectProcessor());
        processors.add(new IntegrationBeanAnnotatedObjectProcessor());
        processors.add(new AnnotatedTransformerObjectProcessor());

    }
}
