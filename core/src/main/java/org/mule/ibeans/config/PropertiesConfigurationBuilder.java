/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.config;

import org.mule.api.MuleContext;
import org.mule.config.builders.AbstractConfigurationBuilder;
import org.mule.util.ClassUtils;
import org.mule.util.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * TODO
 */
public class PropertiesConfigurationBuilder extends AbstractConfigurationBuilder
{
    /**
     * logger used by this class
     */
    protected transient final Log logger = LogFactory.getLog(PropertiesConfigurationBuilder.class);

    public static final String IBEANS_PROPERTIES = "META-INF/ibeans-app.properties";

    private boolean loadFromUserHome = false;

    public boolean isLoadFromUserHome()
    {
        return loadFromUserHome;
    }

    public void setLoadFromUserHome(boolean loadFromUserHome)
    {
        this.loadFromUserHome = loadFromUserHome;
    }

    @Override
    protected void doConfigure(MuleContext muleContext) throws Exception
    {
        Properties props = new Properties();

        URL url = ClassUtils.getResource(IBEANS_PROPERTIES, getClass());
        if (url != null)
        {
            InputStream in = null;
            try
            {
                in = url.openStream();
                props.load(in);
            }
            catch (IOException e)
            {
                IOUtils.closeQuietly(in);
            }
        }
        else
        {
            logger.info("No application properties found at: " + IBEANS_PROPERTIES);
        }
        if (isLoadFromUserHome())
        {
            File f = new File(System.getProperty("user.home") + File.separator + ".ibeans.properties");
            logger.info("Attempting to load iBeans properties from user home: " + f.getAbsolutePath());
            if (f.exists())
            {
                logger.info("iBeans properties found in user home");
                InputStream in = null;
                try
                {
                    in = new FileInputStream(f);
                    props.load(in);
                }
                catch (IOException e)
                {
                    IOUtils.closeQuietly(in);
                }
            }
        }

        if (props.size() > 0)
        {
            //TOOD We should create a new registry for properties to make it easier to hide application properties
            //if we ever expose the registry over a remote API i.e. REST
            //TransientRegistry reg = new TransientRegistry("ibeans-app.properties", muleContext);
            for (Map.Entry<Object, Object> entry : props.entrySet())
            {
                muleContext.getRegistry().registerObject(entry.getKey().toString(), entry.getValue());                
            }
            //muleContext.addRegistry(reg);
        }
        else
        {
            logger.info("No properties found to load by the PropertiesConfigurationBuilder");
        }
    }
}
