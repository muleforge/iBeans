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
import org.mule.api.endpoint.OutboundEndpoint;
import org.mule.config.annotations.endpoints.Channel;
import org.mule.ibeans.api.client.Call;
import org.mule.ibeans.api.client.CallException;
import org.mule.ibeans.api.client.ExceptionListenerAware;
import org.mule.ibeans.internal.client.CallOutboundEndpoint;
import org.mule.ibeans.internal.client.CallRequestEndpoint;
import org.mule.impl.endpoint.AbstractEndpointAnnotationParser;
import org.mule.impl.endpoint.AnnotatedEndpointData;
import org.mule.impl.endpoint.MEP;

import java.beans.ExceptionListener;
import java.lang.annotation.Annotation;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * TODO
 */
public class CallAnnotationParser extends AbstractEndpointAnnotationParser
{
    protected AnnotatedEndpointData createEndpointData(Annotation annotation) throws MuleException
    {
        Call call = (Call) annotation;
        AnnotatedEndpointData epd = new AnnotatedEndpointData(MEP.OutIn);
        epd.setAddress(call.uri());
//        if(epd.getAddress().startsWith("{"))
//        {
//            throw new IllegalArgumentException("Illegal scheme on: " + epd.getAddress() + ". the scheme must be literal, not parameterized");
//        }
        epd.setProperties(AnnotatedEndpointData.convert(call.properties()));
        return epd;
    }

    protected String getIdentifier()
    {
        return Call.class.getAnnotation(Channel.class).identifer();
    }

    @Override
    public boolean supports(Annotation annotation, Class clazz, Member member)
    {
        //You cannot use the @Call annotation on an implementation class
        boolean supports = clazz.isInterface();
        if (supports)
        {
            supports = super.supports(annotation, clazz, member);
        }
        if (supports)
        {
            //Allow services to extend an exception listener that the user can plug in
            if (ExceptionListenerAware.class.isAssignableFrom(clazz))
            {
                supports = true;
            }
            else
            {
                Class[] exceptionTypes = ((Method) member).getExceptionTypes();
                boolean hasValidExceptionType = false;
                for (int i = 0; i < exceptionTypes.length; i++)
                {
                    Class exceptionType = exceptionTypes[i];
                    hasValidExceptionType = exceptionType.equals(Exception.class) || exceptionType.isAssignableFrom(CallException.class) || clazz.isAssignableFrom(ExceptionListener.class);
                }
                if (!hasValidExceptionType)
                {
                    //TODO i18n
                    throw new IllegalArgumentException("@Call annotated methods must have an exception declared that is either of type java.lang.Exception or is assignable fron org.mule.config.annotations.CallException. Offending method is: " + member.getName());
                }
            }
        }
        return supports;
    }

    public OutboundEndpoint parseOutboundEndpoint(Annotation annotation, Map metaInfo) throws MuleException
    {
        AnnotatedEndpointData data = createEndpointData(annotation);
        if (data.getConnectorName() == null)
        {
            data.setConnectorName((String) metaInfo.get("connectorName"));
        }
        CallOutboundEndpoint endpoint = new CallOutboundEndpoint(muleContext, data);
        return endpoint;
    }

    public InboundEndpoint parseInboundEndpoint(Annotation annotation, Map metaInfo) throws MuleException
    {
        AnnotatedEndpointData data = createEndpointData(annotation);
        if (data.getConnectorName() == null)
        {
            data.setConnectorName((String) metaInfo.get("connectorName"));
        }
        CallRequestEndpoint endpoint = new CallRequestEndpoint(muleContext, data);
        return endpoint;
    }
}