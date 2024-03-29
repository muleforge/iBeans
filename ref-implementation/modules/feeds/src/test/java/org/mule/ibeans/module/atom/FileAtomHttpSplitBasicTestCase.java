/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.module.atom;

import java.util.Properties;

public class FileAtomHttpSplitBasicTestCase extends AtomHttpSplitBasicTestCase
{
    protected void addProperties(Properties properties)
    {
        properties.setProperty("feed.uri", "atom:file://./src/test/resources/blog.atom");
    }
}