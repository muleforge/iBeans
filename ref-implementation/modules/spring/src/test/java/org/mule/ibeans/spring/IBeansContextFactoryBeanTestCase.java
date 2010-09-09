/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.spring;

import org.mule.ibeans.IBeansContext;

import junit.framework.TestCase;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class IBeansContextFactoryBeanTestCase extends TestCase
{
    public void testContext() throws Exception
    {
        ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext(
                new String[]{"test-applicationContext.xml"});

        IBeansContext ctx = (IBeansContext) appContext.getBean("ibeansContext");
        assertNotNull(ctx);

        //Test Spring injection
        DummyBean1 bean1 = (DummyBean1) appContext.getBean("test1");
        assertNotNull(bean1);
        assertNotNull(bean1.getIBeansContext());

        //Test Mule annotations in Spring
        DummyBean2 bean2 = (DummyBean2) appContext.getBean("test2");
        assertNotNull(bean2);
        assertNotNull(bean2.getIBeansContext());
        assertNotNull(bean2.getDummy());
    }
}
