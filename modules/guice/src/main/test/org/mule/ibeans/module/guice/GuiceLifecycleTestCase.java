/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.module.guice;

import org.mule.api.config.ConfigurationBuilder;
import org.mule.ibeans.test.IBeansTestSupport;
import org.mule.module.client.MuleClient;
import org.mule.registry.AbstractLifecycleTracker;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

//TODO Current Issues
//1. Dispose gets called twice since eager singletons get bound in the Binding and JitBinding collections in the Injector
// I noticed that the Guice code for 2.1 seems to have added a reliable method for getting all bindings from an injector
//2. SetService gets called after initialise which is not desirable since all injections should occur before initialise.  One way to avaoid this
//would be to bind the service

//ONLY SINGLETON IS SUPPORTED CURRENTLY
public class GuiceLifecycleTestCase extends IBeansTestSupport
{

    @Override
    protected void addBuilders(List<ConfigurationBuilder> builders)
    {
        builders.add(new IBeansGuiceConfigurationBuilder(new GuiceLifecycleModule()));
    }

    /**
     * ASSERT:
     * - Mule lifecycle methods invoked
     * - Service and muleContext injected (Component implements ServiceAware/MuleContextAware)
     * @throws Exception
     */
    @Test
    public void testSingletonServiceLifecycle() throws Exception
    {
      // TODO, Ideal lifecycle  testComponentLifecycle("MuleSingletonService",
//            "[setProperty, setService, setMuleContext, initialise, start, stop, dispose]");
        testComponentLifecycle("MuleSingletonService",
            "[setProperty, setMuleContext, initialise, setService, start, stop, dispose, dispose]");
    }

    /**
     * ASSERT:
     * - Mule lifecycle methods invoked
     * - Service and muleContext injected (Component implements ServiceAware/MuleContextAware)
     * @throws Exception
     */
//    @Test
//    public void testMulePrototypeServiceLifecycle() throws Exception
//    {
////        testComponentLifecycle("MulePrototypeService",
////            "[setProperty, setService, setMuleContext, initialise, start, stop, dispose]");
//        testComponentLifecycle("MulePrototypeService",
//            "[setProperty, setMuleContext, initialise, setService, start, stop, dispose, dispose]");
//    }

    /**
     * ASSERT:
     * - Mule lifecycle methods invoked each time singleton is used to create new object in pool
     * - Service and muleContext injected each time singleton is used to create new object in pool (Component implements ServiceAware/MuleContextAware)
     * @throws Exception
     */
//    @Test
//    public void testMulePooledSingletonServiceLifecycle() throws Exception
//    {
//        //Initialisation policy not enabled in iBeans
//        //testComponentLifecycle("MulePooledSingletonService", "[setProperty, setMuleContext, setService, initialise, initialise, initialise, start, start, start, stop, stop, stop, dispose, dispose, dispose]");
//        //testComponentLifecycle("MulePooledSingletonService", "[setProperty, setService, setMuleContext, initialise, start, stop, dispose]");
//        testComponentLifecycle("MulePooledSingletonService", "[setProperty, setMuleContext, initialise, setService, start, stop, dispose, dispose]");
//    }

    private void testComponentLifecycle(final String serviceName, final String expectedLifeCycle)
        throws Exception
    {

        final AbstractLifecycleTracker tracker = exerciseComponent(serviceName);

        muleContext.dispose();

        Assert.assertEquals(serviceName, expectedLifeCycle, tracker.getTracker().toString());
    }

    private AbstractLifecycleTracker exerciseComponent(final String serviceName) throws Exception
    {
        MuleClient muleClient = new MuleClient();
        final AbstractLifecycleTracker ltc = (AbstractLifecycleTracker) muleClient.send(
            "vm://" + serviceName + ".In", null, null).getPayload();

        Assert.assertNotNull(ltc);

        return ltc;
    }
}