/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans;

import org.mule.api.MuleContext;
import org.mule.api.config.MuleConfiguration;
import org.mule.api.registry.RegistrationException;

import java.util.Collection;

/**
 * A simple config Manager facade used to query the iBeans Registry
 */
public class ConfigManager
{
    private MuleContext muleContext;

    public ConfigManager(MuleContext muleContext)
    {
        this.muleContext = muleContext;
    }

    public Object get(String name)
    {
        return muleContext.getRegistry().lookupObject(name);
    }

    public void put(String name, Object object) throws RegistrationException
    {
         put(name, object, null);
    }

    public void put(String name, Object object, Object metadata) throws RegistrationException
    {
         muleContext.getRegistry().registerObject(name, object, metadata);
    }

    public <T> Collection<T> getObjectsByType(Class<T> type)
    {
        return muleContext.getRegistry().lookupObjects(type);
    }

    public <T> T getObjectByType(Class<T> type) throws RegistrationException
    {
        return muleContext.getRegistry().lookupObject(type);
    }

    /**
     * Provides access to the {@link org.mule.api.config.MuleConfiguration} objects which holds info such as default
     * encoding and the server id.
     *
     * @return the configuration instance for this context
     */
    public MuleConfiguration getEnvironemntConfig()
    {
        return muleContext.getConfiguration();
    }
}
