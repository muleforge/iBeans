/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.api.client;

import org.mule.ibeans.test.IBeansRITestSupport;

import org.ibeans.annotation.IntegrationBean;
import org.junit.Test;

public class CallVoidReturnTestCase extends IBeansRITestSupport
{
    @IntegrationBean
    private SearchIBean search;

    @Test
    public void returnVoid() throws Exception
    {
        //IBEANS-184 : we just need to test that the call doesn't fail
        search.searchAskAndReturnVoid("foo");
    }

}