/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.internal.ext;

import org.mule.api.lifecycle.InitialisationException;
import org.mule.config.i18n.MessageFactory;
import org.mule.object.AbstractObjectFactory;

import java.util.Map;

/**
 * Stores an instance of the object as a normal reference not a {@link java.lang.ref.SoftReference}.  Not happy about this
 * I would like to understand what causes the soft reference to get removed.
 */
public class HardReferenceSingletonObjectFactory extends AbstractObjectFactory
{
    private Object instance;

    /**
     * For Spring only
     */
    public HardReferenceSingletonObjectFactory()
    {
        super();
    }

    public HardReferenceSingletonObjectFactory(String objectClassName)
    {
        super(objectClassName);
    }

    public HardReferenceSingletonObjectFactory(String objectClassName, Map properties)
    {
        super(objectClassName, properties);
    }

    public HardReferenceSingletonObjectFactory(Class objectClass)
    {
        super(objectClass);
    }

    public HardReferenceSingletonObjectFactory(Class objectClass, Map properties)
    {
        super(objectClass, properties);
    }

    /**
     * Create the singleton based on a previously created object.
     */
    public HardReferenceSingletonObjectFactory(Object instance)
    {
        super(instance.getClass());
        this.instance = instance;
    }

    // @Override
    public void initialise() throws InitialisationException
    {
        super.initialise();
        if (instance == null)
        {
            throw new IllegalArgumentException("instancce is null");
        }
    }

    /**
     * Always returns the same instance of the object.
     */
    // @Override
    public Object getInstance() throws Exception
    {
        if (instance != null)
        {
            return instance;
        }
        else
        {
            throw new InitialisationException(
                    MessageFactory.createStaticMessage("Object factory has not been initialized."), this);
        }
    }

    // @Override
    public Class getObjectClass()
    {
        if (instance != null)
        {
            return instance.getClass();
        }
        else
        {
            return super.getObjectClass();
        }
    }

    public boolean isSingleton()
    {
        return true;
    }

}

