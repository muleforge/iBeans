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
import java.util.Properties;

/**
 * A Unit test that extends the {@link org.mule.ibeans.test.AbstractIBeansTestCase} to load properties from a
 * location specified by a system property. These properties reside on the local machine (usually contain sensitive
 * information.
 * <p/>
 * Implementors of this class should call {@link #checkProperties()} at the beginning of every test call
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
public class AbstractExternalPropsIBeansTestCase extends AbstractIBeansTestCase
{
    public static final String IBEANS_TEST_PROPERTIES = "ibeans.test.properties";

    private Properties properties;


    @Override
    protected void doSetUp() throws Exception
    {
        String path = System.getProperty(IBEANS_TEST_PROPERTIES);
        if (path == null)
        {
            String variableName = IBEANS_TEST_PROPERTIES.toUpperCase().replace(".", "_");
            path = System.getenv(variableName);
        }
        if (path == null)
        {
            path = "build.properties";
        }

        File f = new File(path);
        if (!f.exists())
        {
            fail("Test properties not found at: " + f.getAbsolutePath() + ". " + IBEANS_TEST_PROPERTIES + " set to: " + path);
        }
        properties = new Properties();
        properties.load(new FileInputStream(f));

    }

    protected void checkProperties()
    {
        if (properties == null)
        {
            fail("Local build.properties containing test config is not present, contact Ross");
        }
    }

    protected Properties getProps()
    {
        return properties;
    }

    protected String get(String key)
    {
        checkProperties();
        return properties.getProperty(key);
    }
}
