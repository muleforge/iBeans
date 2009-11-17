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

import org.mule.api.MuleContext;

import java.lang.reflect.Method;

/**
 * Used by the transformer proxy to find or create context objects such as JAXB to be passed into a transform method
 */
public interface ObjectResolver
{
    Object findObject(Class type, Method method, MuleContext context) throws Exception;
}
