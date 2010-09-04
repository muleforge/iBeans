/*
 * $Id: IBeansNotationHelper.java 87 2009-10-07 01:52:58Z ross $
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.ibeans.impl;

/**
 * TODO
 */
public class IBeansNotationHelper
{
    public static final String IBEAN_POSTFIX = "IBean";
    public static final String DEFAULT_BASE_PACKAGE = "org.mule.ibeans.";

    public static String getIBeanShortID(Class clazz)
    {
        String name = clazz.getSimpleName();
        int i = name.indexOf(IBEAN_POSTFIX);
        if (i > -1)
        {
            name = name.replaceAll("-", name);
            return name.substring(0, i).toLowerCase();
        }
        else
        {
            throw new IllegalArgumentException("iBean class is not named according to the iBeans notation where the class name should end with 'IBean', i.e. FacebookIBean. Class is: " + clazz);
        }
    }

    public static String getIBeanClassName(String id, String packageName)
    {
        if (packageName == null)
        {
            packageName = DEFAULT_BASE_PACKAGE + id;
        }

        String className = id.substring(0, 1).toUpperCase() + id.substring(1);
        return packageName + className;
    }
}
