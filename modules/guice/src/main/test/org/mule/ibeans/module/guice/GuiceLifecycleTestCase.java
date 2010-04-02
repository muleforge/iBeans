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
        testComponentLifecycle("MuleSingletonService",
            "[setProperty, setMuleContext, setService, initialise, start, stop, dispose]");
    }

    /**
     * ASSERT:
     * - Mule lifecycle methods invoked
     * - Service and muleContext injected (Component implements ServiceAware/MuleContextAware)
     * @throws Exception
     */
    @Test
    public void testMulePrototypeServiceLifecycle() throws Exception
    {
        testComponentLifecycle("MulePrototypeService",
            "[setProperty, setMuleContext, setService, initialise, start, stop, dispose]");
    }

    /**
     * ASSERT:
     * - Mule lifecycle methods invoked each time singleton is used to create new object in pool
     * - Service and muleContext injected each time singleton is used to create new object in pool (Component implements ServiceAware/MuleContextAware)
     * @throws Exception
     */
    @Test
    public void testMulePooledSingletonServiceLifecycle() throws Exception
    {
        //Initialisation policy not enabled in iBeans
        //testComponentLifecycle("MulePooledSingletonService", "[setProperty, setMuleContext, setService, initialise, initialise, initialise, start, start, start, stop, stop, stop, dispose, dispose, dispose]");
        testComponentLifecycle("MulePooledSingletonService", "[setProperty, setMuleContext, setService, initialise, start, stop, dispose]");
    }

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