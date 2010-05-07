/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.web;

import org.mule.api.MuleContext;
import org.mule.api.MuleException;
import org.mule.api.config.ConfigurationBuilder;
import org.mule.api.config.ConfigurationException;
import org.mule.api.config.MuleProperties;
import org.mule.api.context.MuleContextFactory;
import org.mule.api.lifecycle.InitialisationException;
import org.mule.config.DefaultMuleConfiguration;
import org.mule.config.builders.DefaultsConfigurationBuilder;
import org.mule.config.i18n.CoreMessages;
import org.mule.context.DefaultMuleContextBuilder;
import org.mule.ibeans.IBeansContext;
import org.mule.ibeans.config.IBeanHolderConfigurationBuilder;
import org.mule.ibeans.config.PropertiesConfigurationBuilder;
import org.mule.ibeans.internal.config.IBeansMuleContextBuilder;
import org.mule.ibeans.internal.config.IBeansMuleContextFactory;
import org.mule.util.ClassUtils;
import org.mule.util.StringUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;


/**
 * This class is responsible for activating MuleCat in a WebApp.  Add this listener to the web.xml of a WebApp to have
 * Mule running as part of the the application.
 * <p/>
 * This will configure MuleCat for the current environment and load any annotated services and activate them.
 */
public class IBeansServletContextListener implements ServletContextListener
{
    public static final String DEFAULT_CONFIG_BUILDER = "ibeans";

    public static final String SERVICE_PATH = "META-INF/services/org/mule/config/";

    public static final String PROPERTIES = "config-builders.properties";

    private String configBuilder = DEFAULT_CONFIG_BUILDER;

    private MuleContext muleContext;

    public IBeansServletContextListener()
    {
        //default
    }

    public IBeansServletContextListener(String configBuilder)
    {
        this.configBuilder = configBuilder;
    }

    public void contextInitialized(ServletContextEvent event)
    {
        initialize(event.getServletContext());
    }

    public void initialize(ServletContext context)
    {
        try
        {
            muleContext = createMuleContext(context);
            muleContext.initialise();
            context.setAttribute(MuleProperties.MULE_CONTEXT_PROPERTY, muleContext);
            IBeansContext iBeansContext = muleContext.getRegistry().lookupObject(IBeansContext.class);
            context.setAttribute(IBeansContext.CONTEXT_PROPERTY, iBeansContext);
            muleContext.start();
        }
        catch (MuleException ex)
        {
            context.log(ex.getMessage(), ex);
            // Logging is not configured OOTB for Tomcat, so we'd better make a
            // start-up failure plain to see.
            ex.printStackTrace();
        }
        catch (Error error)
        {
            // doesn't always report the java.lang.Error, log it
            context.log(error.getMessage(), error);
            // Logging is not configured OOTB for Tomcat, so we'd better make a
            // start-up failure plain to see.
            error.printStackTrace();
            throw error;
        }
    }

    /**
     * Creates the MuleContext based on the configuration resource(s) and possibly
     * init parameters for the Servlet.
     */
    protected MuleContext createMuleContext(ServletContext context)
            throws ConfigurationException, InitialisationException
    {
        try
        {
            Properties builderMappings = getBuilderMappings();
            if (builderMappings.containsKey(configBuilder))
            {
                configBuilder = builderMappings.getProperty(configBuilder);
            }
            else
            {
                throw new ConfigurationException(CoreMessages.createStaticMessage("Unknown builder name: " + configBuilder));
            }
        }
        catch (IOException e)
        {
            throw new ConfigurationException(e);
        }
        final String serverId = StringUtils.defaultIfEmpty(context.getInitParameter("mule.serverId"), null);

        List<ConfigurationBuilder> builders = new ArrayList<ConfigurationBuilder>();
        addConfigurationBuilders(builders);

        MuleContextFactory muleContextFactory = new IBeansMuleContextFactory();

        DefaultMuleConfiguration muleConfiguration = new DefaultMuleConfiguration();
        if (serverId != null)
        {
            muleConfiguration.setId(serverId);
        }
        DefaultMuleContextBuilder muleContextBuilder = new IBeansMuleContextBuilder();
        muleContextBuilder.setMuleConfiguration(muleConfiguration);

        return muleContextFactory.createMuleContext(builders, muleContextBuilder);
    }

    protected void addConfigurationBuilders(List<ConfigurationBuilder> builders) throws ConfigurationException
    {
        //Load ibeans-app.properties
        builders.add(new PropertiesConfigurationBuilder());

        builders.add(new DefaultsConfigurationBuilder());
        try
        {
            ConfigurationBuilder builder = (ConfigurationBuilder) ClassUtils.instanciateClass(configBuilder, ClassUtils.NO_ARGS);
            builders.add(builder);
        }
        catch (Exception e)
        {
            throw new ConfigurationException(e);
        }
        //Discover client iBeans
        builders.add(new IBeanHolderConfigurationBuilder());
    }


    public void contextDestroyed(ServletContextEvent event)
    {
        destroy();
    }

    protected void destroy()
    {
        if (muleContext != null)
        {
            if (!muleContext.isDisposing() || !muleContext.isDisposed())
            {
                muleContext.dispose();
            }
        }
    }

    MuleContext getMuleContext()
    {
        return muleContext;
    }

    protected Properties getBuilderMappings() throws IOException
    {
        Properties p = new Properties();
        Enumeration e = ClassUtils.getResources(SERVICE_PATH + PROPERTIES, getClass());
        while (e.hasMoreElements())
        {
            URL url = (URL) e.nextElement();
            p.load(url.openStream());
        }
        return p;
    }
}
