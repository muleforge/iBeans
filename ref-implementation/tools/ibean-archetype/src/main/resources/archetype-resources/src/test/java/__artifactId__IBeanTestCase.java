#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
/*
 * ${symbol_dollar}Id:  ${symbol_dollar}
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package ${package};

import org.ibeans.api.CallException;
import org.ibeans.annotation.IntegrationBean;
import org.ibeans.api.IBeansException;
import org.mule.ibeans.test.IBeansRITestSupport;

/**
 * TODO describe the test
 */
public class ${artifactId}IBeanTestCase extends IBeansRITestSupport
{
    @IntegrationBean
    private ${artifactId}IBean ibean;

    @Before
    public ${artifactId}IBeanTestCase()
    {
        setStartContext(true);
    }

    @Test
    public void testTheIBean() throws Exception
    {
         //TODO call methods on your ibean and assert the results
         String result = ibean.updateFoo("bar");
         assertNotNull(result);
         assertEquals("http://www.foo.com/update/bar", result);

         //NOTE: you have access to the IBeansContext within this test, use 'ibeansContext'
    }
}
