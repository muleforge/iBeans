/*
 * $Id: IBeansMessages.java 2 2009-09-15 10:51:49Z ross $
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.ibeans.impl.i18n;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Properties;

/**
 * Ibeans internationalised messages
 */
public class IBeansMessages
{
    private static final IBeansMessages factory = new IBeansMessages();

    private static Properties bundle;
    protected IBeansMessages()
    {
        bundle = new Properties();
        try
        {
            bundle.load(getClass().getResourceAsStream("/META-INF/i18n/ibeans-messages.properties"));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    protected String createMessage(int index, Object... args)
    {
        String message = bundle.getProperty(String.valueOf(index));
        if(message==null)
        {
            throw new IllegalArgumentException("This is no message in bundle for id: " + index);
        }
        return String.format(message, args);
    }

    public static String versionNotSet()
    {
        return factory.createMessage(1);
    }

    public static String serverStartedAt(long startDate)
    {
        return factory.createMessage(2, new Date(startDate));
    }

    public static String serverShutdownAt(Date date)
    {
        return factory.createMessage(3, date);
    }

    public static String modulesLoaded(String modules)
    {
        return factory.createMessage(4, modules);
    }

    public static String notSet()
    {
        return factory.createMessage(5);
    }

//    public static String version()
//    {
//        String version = StringUtils.defaultString(MuleManifest.getProductVersion(), notSet().getMessage());
//        return factory.createMessage(6, version);
//    }
//
//    public static String shutdownNormally(Date date)
//    {
//        return factory.createMessage(7, date);
//    }
//
//    public static String serverWasUpForDuration(long duration)
//    {
//        String formattedDuration = DateUtils.getFormattedDuration(duration);
//        return factory.createMessage(8, formattedDuration);
//    }


    public static String parameterNotOptional(Annotation annotation, Method method)
    {
        return factory.createMessage(9, annotation.toString(), method.toString());
    }

    public static String onlySingleEvalParamSupported(Method method)
    {
        return factory.createMessage(10, method.toString());
    }

    public static String failedToSendMessageUsingUri(String uri)
    {
        return factory.createMessage(11, uri);
    }
}