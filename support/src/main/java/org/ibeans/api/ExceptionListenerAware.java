/*
 * $Id: ExceptionListenerAware.java 2 2009-09-15 10:51:49Z ross $
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.ibeans.api;

import java.beans.ExceptionListener;

/**
 * Can be used by IntegrationBean interfaces to add an exceptionListener to the client.
 */
public interface ExceptionListenerAware
{
    /**
     * @param el
     */
    public void setExceptionListener(ExceptionListener el);
}
