/*
 * $Id:  $
 * -------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.ibeans.spi;

import org.ibeans.api.IBeansException;

/**
 * The <code>Filter</code> interface allows MuleMessage filtering.
 */

public interface Filter<T>
{
    /**
     * Check a given message against this filter.
     *
     * @param object a non null object to filter on.
     * @return <code>true</code> if the object can pass through the filter, false otherwise
     * @throws IBeansException if there is an error while performing the evaluation
     */
    boolean accept(T object) throws IBeansException;
}