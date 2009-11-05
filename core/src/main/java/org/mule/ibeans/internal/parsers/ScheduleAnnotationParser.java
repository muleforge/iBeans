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
import org.mule.ibeans.api.application.Schedule;
import org.mule.ibeans.channels.CHANNEL;
import org.mule.ibeans.config.ScheduleConfigBuilder;
import org.mule.impl.endpoint.AbstractEndpointAnnotationParser;
import org.mule.impl.endpoint.AnnotatedEndpointData;
import org.mule.impl.endpoint.MEP;
import org.mule.transport.quartz.QuartzConnector;
import org.mule.transport.quartz.jobs.EventGeneratorJobConfig;
import org.mule.util.StringUtils;
import org.mule.util.UUID;

import java.lang.annotation.Annotation;
import java.lang.reflect.Member;
import java.util.Collections;
import java.util.Map;

/**
 * Creates a Quartz inbound endpoint for a service
 */
public class ScheduleAnnotationParser extends AbstractEndpointAnnotationParser
{

    @Override
    public InboundEndpoint parseInboundEndpoint(Annotation annotation, Map metaInfo) throws MuleException
    {
        Schedule schedule = (Schedule) annotation;
        ScheduleConfigBuilder builder = lookupConfig(schedule.config(), ScheduleConfigBuilder.class);
        if (builder != null)
        {
            return builder.buildScheduler();
        }
        else
        {
            return super.parseInboundEndpoint(annotation, Collections.EMPTY_MAP);
        }
    }

    protected AnnotatedEndpointData createEndpointData(Annotation annotation) throws MuleException
    {
        //This will only get called if there is no config builder configured
        Schedule schedule = (Schedule) annotation;

        String uri = "quartz://schedule" + UUID.getUUID();
        AnnotatedEndpointData epData = new AnnotatedEndpointData(MEP.InOnly);

        epData.setProperties(convertProperties(getProperties(schedule)));
        //By default the scheduler should only use a single thread
        String threads = (String) epData.getProperties().get(CHANNEL.MAX_THREADS);
        if (threads == null)
        {
            threads = "1";
            epData.getProperties().put(CHANNEL.MAX_THREADS, threads);
        }
        epData.setAddress(uri);
        epData.setConnector(getConnector(schedule));
        //Create event generator job
        EventGeneratorJobConfig config = new EventGeneratorJobConfig();
        config.setStateful(threads.equals("1"));
        epData.getProperties().put(QuartzConnector.PROPERTY_JOB_CONFIG, config);
        return epData;
    }


    protected String getProperties(Schedule schedule) throws MuleException
    {
        StringBuilder sb = new StringBuilder();
        if (StringUtils.isNotBlank(schedule.cron()))
        {
            sb.append(QuartzConnector.PROPERTY_CRON_EXPRESSION).append("=").append(schedule.cron());
        }
        else if (schedule.interval() > -1)
        {
            sb.append(QuartzConnector.PROPERTY_REPEAT_INTERVAL).append("=").append(schedule.interval());

            if (schedule.startDelay() > -1)
            {
                sb.append(",").append(QuartzConnector.PROPERTY_START_DELAY).append("=").append(schedule.startDelay());
            }
        }
        else
        {
            throw new IllegalArgumentException("cron or repeatInterval must be set");
        }
        return sb.toString();

    }

    protected String getIdentifier()
    {
        return Schedule.class.getAnnotation(Channel.class).identifer();
    }

    protected QuartzConnector getConnector(Schedule schedule) throws MuleException
    {
        QuartzConnector connector = new QuartzConnector();
        connector.setName("scheduler." + connector.hashCode());
        muleContext.getRegistry().registerConnector(connector);
        return connector;
    }

    /**
     * Features like the {@link org.mule.ibeans.api.client.IntegrationBean} annotation can be used to define an service proxy
     * configuration where the annotations are configured on the interface methods.  However, it is illegal to configure
     * the @Schedule annotation in this way.
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
            //You cannot use the @Schedule annotation on a interface
            return false;
        }
        return super.supports(annotation, clazz, member);
    }
}