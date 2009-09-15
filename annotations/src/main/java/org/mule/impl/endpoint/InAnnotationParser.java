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
import org.mule.config.annotations.endpoints.In;

import java.lang.annotation.Annotation;
import java.lang.reflect.Member;

/**
 * TODO
 */
public class InAnnotationParser extends AbstractEndpointAnnotationParser
{
    protected AnnotatedEndpointData createEndpointData(Annotation annotation) throws MuleException
    {
        In in = (In) annotation;
        AnnotatedEndpointData epd = new AnnotatedEndpointData(in.mep());
        epd.setEncoding(in.encoding());
        epd.setProperties(convertProperties(in.properties()));
        epd.setConnectorName(in.connector());
        epd.setAddress(in.uri());
        epd.setFilter(in.filter());
        epd.setTransformers(in.transformers());
        epd.setName(in.id());
        return epd;
    }

    protected String getIdentifier()
    {
        return In.class.getAnnotation(Channel.class).identifer();
    }

    /**
     * Features like the {@link org.mule.ibeans.api.client.ServiceProxy} annotation can be used to define a service proxy
     * configuration where the annotations are configured on the interface methods. However, it is illegal to configure
     * the @In annotation in this way.
     *
     * @param annotation the annotation being processed
     * @param clazz      the class on which the annotation was found
     * @param member     the member on which the annotation was found inside the class. This is only set when the annotation
     *                   was either set on a {@link java.lang.reflect.Method}, {@link java.lang.reflect.Field}, or {@link java.lang.reflect.Constructor}
     *                   class members, otherwise this value is null.
     * @return true if this parser supports the current annotation and the clazz is not an interface.
     * @throws IllegalArgumentException if the class parameter is an interface.
     */
    @Override
    public boolean supports(Annotation annotation, Class clazz, Member member)
    {
        if (clazz.isInterface())
        {
            //You cannot use the @In annotation on a interface
            return false;
        }
        return super.supports(annotation, clazz, member);
    }
}