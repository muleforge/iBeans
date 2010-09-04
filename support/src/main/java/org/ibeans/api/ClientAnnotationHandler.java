/*
 * $Id: ClientAnnotationHandler.java 290 2010-02-15 09:54:41Z ross $
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.ibeans.api;

import java.lang.reflect.Method;

/**
 * TODO
 */

public interface ClientAnnotationHandler
{
    public Response invoke(InvocationContext invocationContext) throws Exception;

    public String getScheme(Method method);
}
