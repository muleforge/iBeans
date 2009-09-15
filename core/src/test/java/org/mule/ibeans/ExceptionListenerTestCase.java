/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans;

import org.mule.ibeans.api.client.IntegrationBean;
import org.mule.ibeans.test.AbstractIBeansTestCase;

import java.beans.ExceptionListener;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Tests using an exception listnenr to intercept all exceptions on the ibean.  Also test sthat parsing the ibean will not barf if
 * the ibean extends {@link org.mule.ibeans.api.client.ExceptionListenerAware}
 */
public class ExceptionListenerTestCase extends AbstractIBeansTestCase
{
    @IntegrationBean
    private TestExceptionIBean test;

    public void testException() throws Exception
    {
        final AtomicBoolean exceptionThrown = new AtomicBoolean(false);
        test.setExceptionListener(new ExceptionListener()
        {
            public void exceptionThrown(Exception e)
            {
                exceptionThrown.set(true);
            }
        });
        String data = test.doSomething("blah");
        //Exception should not be thrown, insted the listener intercepts it
        assertTrue(exceptionThrown.get());
    }

    public void testExceptionType() throws Exception
    {
        final AtomicBoolean exceptionThrown = new AtomicBoolean(false);
        test.setExceptionListener(new ExceptionListener()
        {
            public void exceptionThrown(Exception e)
            {
                exceptionThrown.set(true);
                assertTrue(e instanceof UnknownHostException);
            }
        });
        String data = test.doSomethingElse();
        //Exception should not be thrown, insted the listener intercepts it
        assertTrue(exceptionThrown.get());
    }
}

