/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import static org.junit.Assert.fail;

/**
 * A Unit test that extends the {@link IBeansRITestSupport} to load properties from a
 * location specified by a system property. These properties reside on the local machine (usually contain sensitive
 * information.
 * <p/>
 * In order to use this class for testing in Maven, you will need to  configure the sure-fire plugin to regconise
 * the {@link #IBEANS_TEST_PROPERTIES} property since sure-fire will fork tests in their own JVM.  To do this, add
 * the following to the &lt;build&gt;&lt;plugins&gt; section of the pom.xml -
 * <p/>
 * <code>
 * &lt;plugin&gt;
 * &lt;groupId&gt;org.apache.maven.plugins&lt;/groupId&gt;
 * &lt;artifactId&gt;maven-surefire-plugin&lt;/artifactId&gt;
 * <p/>
 * &lt;configuration&gt;
 * &lt;systemProperties&gt;
 * &lt;property&gt;
 * &lt;name&gt;ibeans.test.properties&lt;/name&gt;
 * &lt;value&gt;${ibeans.test.properties}&lt;/value&gt;
 * &lt;/property&gt;
 * &lt;/systemProperties&gt;
 * &lt;/configuration&gt;
 * &lt;/plugin&gt;
 * </code>
 */
public class ExternalPropsIBeansTestSupport extends IBeansRITestSupport
{
    public static final String IBEANS_TEST_PROPERTIES = "ibeans.test.properties";
    public static final String DEFAULT_PROPERTIES_FILENAME = ".ibeans.properties";

    @Override
    protected void addStartUpProperties(Properties properties)
    {
        String path = System.getProperty(IBEANS_TEST_PROPERTIES, null);
        if (path == null)
        {
            path = System.getProperty("user.home") + File.separator + DEFAULT_PROPERTIES_FILENAME;
        }


        File f = new File(path);
        if (!f.exists())
        {
            fail("Test properties not found at: " + f.getAbsolutePath() + ". " + IBEANS_TEST_PROPERTIES + " set to: " + path);
        }
        try
        {
            properties.load(new FileInputStream(f));
        }
        catch (IOException e)
        {
            fail("could not load properties for test: " + f.getAbsolutePath() + ", " + e.getMessage());
        }

    }

    protected String get(String key)
    {
        Object result = iBeansContext.getConfig().get(key);
        if(result==null)
        {
            return null;
        }
        else if(result instanceof String)
        {
            return (String)result;
        }
        else
        {
            fail("Property with key: " + key + " returned a value that is not a String. Value is: " + result);
            return null;
        }
    }
}
