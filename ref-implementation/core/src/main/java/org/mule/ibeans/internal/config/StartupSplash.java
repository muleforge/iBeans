/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.internal.config;

import org.mule.api.MuleContext;
import org.mule.config.StartupContext;
import org.mule.ibeans.i18n.IBeansMessages;
import org.mule.util.SplashScreen;
import org.mule.util.StringUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.jar.Manifest;

/**
 * Displayed in the logs when the Mule iBeans container starts up
 */
public class StartupSplash extends SplashScreen
{
    protected void doHeader(MuleContext context)
    {
        String notset = IBeansMessages.notSet().getMessage();

        // Mule Version, Timestamp, and Server ID
        Manifest mf = IBeansInfo.getManifest();
        Map att = mf.getMainAttributes();
        if (att.values().size() > 0)
        {
            header.add(StringUtils.defaultString(IBeansInfo.getProductDescription(), notset));
            header.add(IBeansMessages.version().getMessage() + " Build: "
                    + StringUtils.defaultString(IBeansInfo.getBuildNumber(), notset));

            header.add(StringUtils.defaultString(IBeansInfo.getVendorName(), notset));
            header.add(StringUtils.defaultString(IBeansInfo.getProductMoreInfo(), notset));
        }
        else
        {
            header.add(IBeansMessages.versionNotSet().getMessage());
        }
        header.add(" ");
        if (context.getStartDate() > 0)
        {
            header.add(IBeansMessages.serverStartedAt(context.getStartDate()).getMessage());
        }
        header.add("Server ID: " + context.getConfiguration().getId());

        // JDK, Encoding, OS, and Host
        header.add("JDK: " + System.getProperty("java.version") + " ("
                + System.getProperty("java.vm.info") + ")");
        header.add("OS encoding: " + System.getProperty("file.encoding")
                + ", iBeans encoding: " + context.getConfiguration().getDefaultEncoding());
        String patch = System.getProperty("sun.os.patch.level", null);
        header.add("OS: " + System.getProperty("os.name")
                + (patch != null && !"unknown".equalsIgnoreCase(patch) ? " - " + patch : "") + " ("
                + System.getProperty("os.version") + ", " + System.getProperty("os.arch") + ")");
        try
        {
            InetAddress host = InetAddress.getLocalHost();
            header.add("Host: " + host.getHostName() + " (" + host.getHostAddress() + ")");
        }
        catch (UnknownHostException e)
        {
            // ignore
        }

        // Dev/Production mode
        final boolean productionMode = StartupContext.get().getStartupOptions().containsKey("production");
        header.add("Mode: " + (productionMode ? "Production" : "Development"));

        header.add(" ");
    }
}
