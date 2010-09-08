/*
 * $Id$
 * -------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.ibeans.spi;

import java.lang.annotation.Annotation;

import org.ibeans.api.channel.MimeType;

import org.ibeans.api.IBeansException;

/**
 * TODO
 */

public interface ErrorFilterFactory
{

    public boolean isSupported(Annotation annotation);

    public ErrorFilterHolder parse(Annotation annotation) throws IBeansException;

    public static class ErrorFilterHolder
    {
        private MimeType mimeType;

        private ErrorFilter filter;

        public ErrorFilterHolder(MimeType mimeType, ErrorFilter filter)
        {
            this.mimeType = mimeType;
            this.filter = filter;
        }

        public MimeType getMimeType()
        {
            return mimeType;
        }

        public ErrorFilter getFilter()
        {
            return filter;
        }
    }
}
