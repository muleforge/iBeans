/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.googlebase;

import org.mule.ibeans.api.client.MockIntegrationBean;

import static org.mockito.Mockito.when;


public class MockGoogleBaseTestCase extends GoogleBaseTestCase
{
    @MockIntegrationBean
    private GoogleBaseIBean googlebase;

    @Override
    protected GoogleBaseIBean getIBean()
    {
        return googlebase;
    }

    @Override
    protected void doSetUp() throws Exception
    {
        super.doSetUp();
        //Add expected calls and test data, the tests are all the same!
        when(googlebase.search(QUERY)).thenAnswer(withAtomData("mule-esb-search.atom", googlebase));
        when(googlebase.search(QUERY, 100)).thenAnswer(withAtomData("mule-esb-100-search.atom", googlebase));
    }


}