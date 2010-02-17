/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.config.jndi;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * TODO
 */
public class IBeansInitialContextFactory implements InitialContextFactory
{
    private static final transient Log log = LogFactory.getLog(IBeansInitialContextFactory.class);

    private static Map cache = new HashMap();

    private static Context singleton;

    /**
     * A factory method which can be used to initialise a singleton JNDI context from
     * inside a Spring.xml such that future calls to new InitialContext() will reuse
     * it
     */
    public static Context makeInitialContext()
    {
        singleton = new IBeansJndiContext();
        return singleton;
    }

    public Context getInitialContext(Hashtable environment) throws NamingException
    {
        if (singleton != null)
        {
            return singleton;
        }

        //URLClassLoader u = new URLClassLoader();

        Object value = environment.get(Context.PROVIDER_URL);

        return null;
    }

}
