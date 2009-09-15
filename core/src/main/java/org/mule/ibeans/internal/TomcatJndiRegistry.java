/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.internal;

/**
 * A Registry facade that provides read-only access tot he Tomcat JNDI context where users can configure environemnt properties
 * and JNDI objects such as DataSources or JavaMail sessions. Tomcat JNDI objects can be configured globally (for a Tomcat
 * instance) using the &lt;GlobalNamingResources&gt; element, or per web app on the &lt;Context&gt; element.
 * <p/>
 * TODO add a link for more information
 */
public class TomcatJndiRegistry extends JndiRegistry
{
    public TomcatJndiRegistry()
    {
        super("tomcat-jndi-registry", "java:comp/env");
    }

    @Override
    public boolean isRemote()
    {
        return false;
    }
}
