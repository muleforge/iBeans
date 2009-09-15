/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.impl.endpoint;

import org.mule.api.MuleException;
import org.mule.config.annotations.endpoints.Channel;
import org.mule.config.annotations.endpoints.Out;

import java.lang.annotation.Annotation;
import java.lang.reflect.Member;

/**
 * TODO
 */
public class OutAnnotationParser extends AbstractEndpointAnnotationParser
{
    protected AnnotatedEndpointData createEndpointData(Annotation annotation) throws MuleException
    {
        Out out = (Out) annotation;
        AnnotatedEndpointData epd = new AnnotatedEndpointData(out.mep());
        epd.setEncoding(out.encoding());
        epd.setProperties(convertProperties(out.properties()));
        epd.setConnectorName(out.connector());
        epd.setAddress(out.uri());
        epd.setFilter(out.filter());
        epd.setTransformers(out.transformers());
        epd.setName(out.id());
        return epd;
    }

    protected String getIdentifier()
    {
        return Out.class.getAnnotation(Channel.class).identifer();
    }

    @Override
    public boolean supports(Annotation annotation, Class clazz, Member member)
    {
        if (clazz.isInterface())
        {
            //You cannot use the @Out annotation on a interface
            return false;
        }
        return super.supports(annotation, clazz, member);
    }
}