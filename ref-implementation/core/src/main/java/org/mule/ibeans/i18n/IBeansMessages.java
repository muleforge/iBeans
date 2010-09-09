/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.i18n;

import org.mule.config.MuleManifest;
import org.mule.config.i18n.Message;
import org.mule.config.i18n.MessageFactory;
import org.mule.util.DateUtils;
import org.mule.util.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Date;

/**
 * Ibeans internationalised messages
 */
public class IBeansMessages extends MessageFactory
{
    private static final IBeansMessages factory = new IBeansMessages();

    private static final String BUNDLE_PATH = getBundlePath("ibeans");

    public static Message versionNotSet()
    {
        return factory.createMessage(BUNDLE_PATH, 1);
    }

    public static Message serverStartedAt(long startDate)
    {
        return factory.createMessage(BUNDLE_PATH, 2, new Date(startDate));
    }

    public static Message serverShutdownAt(Date date)
    {
        return factory.createMessage(BUNDLE_PATH, 3, date);
    }

    public static Message modulesLoaded(String modules)
    {
        return factory.createMessage(BUNDLE_PATH, 4, modules);
    }

    public static Message notSet()
    {
        return factory.createMessage(BUNDLE_PATH, 5);
    }

    public static Message version()
    {
        String version = StringUtils.defaultString(MuleManifest.getProductVersion(), notSet().getMessage());
        return factory.createMessage(BUNDLE_PATH, 6, version);
    }

    public static Message shutdownNormally(Date date)
    {
        return factory.createMessage(BUNDLE_PATH, 7, date);
    }

    public static Message serverWasUpForDuration(long duration)
    {
        String formattedDuration = DateUtils.getFormattedDuration(duration);
        return factory.createMessage(BUNDLE_PATH, 8, formattedDuration);
    }


    public static Message parameterNotOptional(Annotation annotation, Method method)
    {
        return factory.createMessage(BUNDLE_PATH, 9, annotation.toString(), method.toString());
    }

    public static Message onlySingleEvalParamSupported(Method method)
    {
        return factory.createMessage(BUNDLE_PATH, 10, method.toString());
    }

    public static Message failedToSendMessageUsingUri(String uri)
    {
        return factory.createMessage(BUNDLE_PATH, 11, uri);
    }
}