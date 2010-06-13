/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.config;

import org.mule.api.MuleContext;
import org.mule.ibeans.api.IBeansNotationHelper;
import org.mule.ibeans.api.client.views.TextUsageView;
import org.mule.ibeans.internal.client.AnnotatedInterfaceBinding;

/**
 * Holds a reference to an iBeans class in the registry. An iBean instance can be created from this object as well as reporting
 * its usage and short ID.
 */
public class IBeanHolder implements Comparable
{
    private Class ibean;
    private String usage;

    public IBeanHolder(Class ibean)
    {
        this.ibean = ibean;
    }

    public int compareTo(Object o)
    {
        IBeanHolder to = (IBeanHolder) o;
        return getId().compareTo(to.getId());
    }

    public Class getIbeanClass()
    {
        return ibean;
    }

    public Object create(MuleContext context)
    {
        AnnotatedInterfaceBinding router = new AnnotatedInterfaceBinding(context);
        router.setInterface(ibean);
        return router.createProxy(new Object());
    }

    public String getId()
    {
        return IBeansNotationHelper.getIBeanShortID(ibean);
    }

    public String getUsage()
    {
        if (usage == null)
        {
            TextUsageView view = new TextUsageView();
            usage = view.createView(ibean);
        }
        return usage;
    }

    @Override
    public String toString()
    {
        return "IBean: " + getId() + " : " + ibean.getName();
    }
}