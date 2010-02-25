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

import org.mule.api.lifecycle.InitialisationException;
import org.mule.api.registry.RegistrationException;
import org.mule.registry.AbstractRegistry;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A registry facade for a Jndi Context.  JNDI registries in Mule will always be treated as read-only. Since Mule is a
 * runtime container it would be bad practice to create object in JNDI stores.
 */
public class JndiRegistry extends AbstractRegistry
{
    /**
     * logger used by this class
     */
    protected transient final Log logger = LogFactory.getLog(JndiRegistry.class);
    
    private String contextLookup;

    protected Context context;

    public JndiRegistry(String id, String contextLookup)
    {
        super(id);
        this.contextLookup = contextLookup;
    }

    protected void doInitialise() throws InitialisationException
    {
        try
        {
            context = new InitialContext();
            if (contextLookup != null)
            {
                context = (Context) context.lookup(contextLookup);
            }
        }
        catch (NamingException e)
        {
            throw new InitialisationException(e, this);
        }

    }

    protected void doDispose()
    {
        try
        {
            context.close();
        }
        catch (NamingException e)
        {
            logger.error(e.getMessage());
        }
    }

    public Object lookupObject(String key)
    {
        try
        {
            return context.lookup(key);
        }
        catch (NamingException e)
        {
            logger.debug("Object '" + key + "' not found in Registry: " + getRegistryId());
            return null;
        }
    }

    public <T> Map<String, T> lookupByType(Class<T> tClass)
    {
        throw new UnsupportedOperationException("lookupByType");
    }

    public Collection lookupObjects(Class type)
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Looking up objects by class type is not supported by JNDI registries");
        }
        return Collections.emptyList();
    }

    ////////////////////////////////////////////////////////////////////////////////////
    // Registry is read-only
    ////////////////////////////////////////////////////////////////////////////////////

    public final void registerObject(String key, Object value) throws RegistrationException
    {
        throw new UnsupportedOperationException("Registry is read-only so objects cannot be registered or unregistered.");
    }

    public final void registerObject(String key, Object value, Object metadata) throws RegistrationException
    {
        throw new UnsupportedOperationException("Registry is read-only so objects cannot be registered or unregistered.");
    }

    public final void registerObjects(Map objects) throws RegistrationException
    {
        throw new UnsupportedOperationException("Registry is read-only so objects cannot be registered or unregistered.");
    }

    public final void unregisterObject(String key)
    {
        throw new UnsupportedOperationException("Registry is read-only so objects cannot be registered or unregistered.");
    }

    public final void unregisterObject(String key, Object metadata) throws RegistrationException
    {
        throw new UnsupportedOperationException("Registry is read-only so objects cannot be registered or unregistered.");
    }

    ////////////////////////////////////////////////////////////////////////////////////
    // Registry meta-data
    ////////////////////////////////////////////////////////////////////////////////////

    public final boolean isReadOnly()
    {
        return true;
    }

    public boolean isRemote()
    {
        return true;
    }
}
