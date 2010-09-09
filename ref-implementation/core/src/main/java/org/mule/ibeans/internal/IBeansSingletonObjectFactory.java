/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.internal;

import org.mule.api.service.Service;
import org.mule.object.SingletonObjectFactory;

import java.util.Map;

/**
 * TODO
 */
public class IBeansSingletonObjectFactory extends SingletonObjectFactory implements IBeansObjectFactory
{
    public IBeansSingletonObjectFactory()
    {
    }

    public IBeansSingletonObjectFactory(String objectClassName)
    {
        super(objectClassName);
    }

    public IBeansSingletonObjectFactory(String objectClassName, Map properties)
    {
        super(objectClassName, properties);
    }

    public IBeansSingletonObjectFactory(Class objectClass)
    {
        super(objectClass);
    }

    public IBeansSingletonObjectFactory(Class<?> objectClass, Map properties)
    {
        super(objectClass, properties);
    }

    public IBeansSingletonObjectFactory(Object instance)
    {
        super(instance);
    }

    public void setService(Service service)
    {
        //do nothing, just for compatibility
    }

    @Override
    public boolean isAutoWireObject()
    {
        return false;
    }
}
