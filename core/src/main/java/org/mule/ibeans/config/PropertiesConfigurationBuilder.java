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
 * This configuration builder is used by iBeans to load application properties from known locations.  these
 * properties can be used by either accessing the config object from the {@link org.mule.ibeans.IBeansContext#getConfig()}
 * object.
 * <p/>
 * Additionally these properties can also be accessed through property placeholders in in strings such as URI or arguments
 * to iBean methods. For example the following examples are valid -
 * <code>
 * twitter.setCredentials("${twitter.username}", "${twitter.password}");
 * <p/>
 * &amp;#064;Receive(uri = "jms://${queue_name}")
 * </code>
 *
 * Properties can be read from 3 locations -
 * <ol>
 * <li>Your application can contain a properties file called 'ibeans-app.properties' located in the META-INF directory</li>
 * <li>The location of a properties file can be set as a system property using '-Dibeans.properties.location=(full path to properties file)'</li>
 * <li>An properties file called 'ibeans.properties' can be located in the users home directory.
 * </ol>
 *
 */
public class PropertiesConfigurationBuilder extends AbstractConfigurationBuilder
{
    /**
     * logger used by this class
     */
    protected transient final Log logger = LogFactory.getLog(PropertiesConfigurationBuilder.class);

    public static final String IBEANS_PROPERTIES_LOCATION = "ibeans.properties.location";

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

        //Check for application properties first
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

        String propsLocation = System.getProperty(IBEANS_PROPERTIES_LOCATION, null);

        if (propsLocation != null)
        {
            File f = new File(propsLocation);
            loadProperties(f, props);
        }

        if (isLoadFromUserHome())
        {
            File f = new File(System.getProperty("user.home") + File.separator + ".ibeans.properties");
            loadProperties(f, props);
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

    /**
     * Load properties into an existing properties map form the given file. If the file does not exist it will
     * be ignored.  If the file contains a property that already exists in the properties map, the value in the map will
     * be overwritten
     *
     * @param file the properties file to load into the properties map
     * @param props the properties map which holds any properties loaded from the file
     */
    protected void loadProperties(File file, Properties props)
    {
        logger.info("Attempting to load iBeans properties from: " + file.getAbsolutePath());
        if (file.exists())
        {
            logger.info("iBeans properties found properties in: " + file.getAbsolutePath());
            InputStream in = null;
            try
            {
                in = new FileInputStream(file);
                props.load(in);
            }
            catch (IOException e)
            {
                IOUtils.closeQuietly(in);
            }
        }
    }
}
