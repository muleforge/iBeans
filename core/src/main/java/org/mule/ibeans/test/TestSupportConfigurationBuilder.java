/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.test;

import org.mule.config.AnnotationsConfigurationBuilder;
import org.mule.config.AnnotationsParserFactory;
import org.mule.ibeans.internal.parsers.IBeansAnnotationsParserFactory;

/**
 * We need to register the {@link org.mule.ibeans.internal.parsers.IBeansAnnotationsParserFactory} with the iBeans
 * context since the tests do not run with any of the iBeans builders (users register the objects they want to test)
 */
public class TestSupportConfigurationBuilder extends AnnotationsConfigurationBuilder
{
    @Override
    protected AnnotationsParserFactory createAnnotationsParserFactory()
    {
        return new IBeansAnnotationsParserFactory();
    }
}
