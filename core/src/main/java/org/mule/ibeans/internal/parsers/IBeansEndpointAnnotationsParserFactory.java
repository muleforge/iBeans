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

import org.mule.impl.endpoint.DefaultEndpointAnnotationsParserFactory;

/**
 * TODO
 */
public class IBeansEndpointAnnotationsParserFactory extends DefaultEndpointAnnotationsParserFactory
{
    protected void addDefaultParsers()
    {
        super.addDefaultParsers();
        registerEndpointParser(new ReceiveAnnotationParser());
        registerEndpointParser(new ReceiveAndReplyAnnotationParser());
        registerEndpointParser(new SendAnnotationParser());
        registerEndpointParser(new ScheduleAnnotationParser());
        registerEndpointParser(new ReceiveAnnotationParser());
        registerEndpointParser(new CallAnnotationParser());
    }
}