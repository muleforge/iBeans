/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.internal.parsers;

import org.mule.api.MuleException;
import org.mule.api.routing.filter.Filter;

import java.lang.annotation.Annotation;

/**
 * TODO
 */

public interface ErrorFilterParser
{

    public boolean isSupported(Annotation annotation);

    public ErrorFilterHolder parse(Annotation annotation) throws MuleException;

    public static class ErrorFilterHolder
    {
        private String mimeType;

        private Filter filter;

        public ErrorFilterHolder(String mimeType, Filter filter)
        {
            this.mimeType = mimeType;
            this.filter = filter;
        }

        public String getMimeType()
        {
            return mimeType;
        }

        public Filter getFilter()
        {
            return filter;
        }
    }
}
