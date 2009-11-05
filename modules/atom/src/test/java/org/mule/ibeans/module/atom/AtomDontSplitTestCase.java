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

import org.mule.ibeans.test.AbstractIBeansTestCase;

public class AtomDontSplitTestCase extends AbstractIBeansTestCase
{
    private AtomNoSplitFeed noSplitFeed;

    @Override
    protected void doSetUp() throws Exception
    {
        noSplitFeed = new AtomNoSplitFeed();
        registerBeans(noSplitFeed);
    }

    public void testConsumeFeed() throws Exception
    {
        Thread.sleep(2000);
        int count = noSplitFeed.getCount();
        assertTrue(count > 0);
    }
}