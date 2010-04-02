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

import org.mule.api.MuleContext;
import org.mule.api.MuleException;
import org.mule.ibeans.IBeansRuntimeException;
import org.mule.ibeans.internal.AnnotatedTransformerObjectProcessor;
import org.mule.ibeans.internal.IBeansAnnotatedEndpointHelper;
import org.mule.ibeans.internal.IntegrationBeanAnnotatedObjectProcessor;
import org.mule.ibeans.internal.MuleiBeansAnnotatedObjectProcessor;
import org.mule.impl.annotations.processors.DirectBindAnnotationProcessor;
import org.mule.impl.annotations.processors.InjectAnnotationProcessor;
import org.mule.impl.annotations.processors.NamedAnnotationProcessor;
import org.mule.impl.endpoint.AnnotatedEndpointHelper;
import org.mule.impl.endpoint.DefaultAnnotationsParserFactory;

/**
 * TODO
 */
public class IBeansAnnotationsParserFactory extends DefaultAnnotationsParserFactory
{
    protected AnnotatedEndpointHelper createAnnotatedEndpointHelper(MuleContext muleContext) throws MuleException
    {
        return new IBeansAnnotatedEndpointHelper(muleContext);
    }

    @Override
    public void setMuleContext(MuleContext context)
    {
        try
        {
            AnnotatedEndpointHelper annotatedEndpointHelper = createAnnotatedEndpointHelper(context);
            context.getRegistry().registerObject("_" + annotatedEndpointHelper.getClass().getSimpleName(), annotatedEndpointHelper);
        }
        catch (MuleException e)
        {
            throw new IBeansRuntimeException("failed to create AnnotatedEndpointHelper", e);
        }

        super.setMuleContext(context);
    }

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
    }

    protected void addDefaultProcessors()
    {
        //Processors
        registerObjectProcessor(new MuleiBeansAnnotatedObjectProcessor());
        registerObjectProcessor(new DirectBindAnnotationProcessor());
        registerObjectProcessor(new IntegrationBeanAnnotatedObjectProcessor());
        registerObjectProcessor(new AnnotatedTransformerObjectProcessor());
        registerObjectProcessor(new InjectAnnotationProcessor());
        registerObjectProcessor(new NamedAnnotationProcessor());

        registerObjectProcessor(new InjectAnnotationProcessor());//Add support for JSR-330
        registerObjectProcessor(new NamedAnnotationProcessor());//Add support for JSR-330
    }
}
