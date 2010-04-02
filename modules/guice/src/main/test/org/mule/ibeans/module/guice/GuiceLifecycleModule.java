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

import org.mule.module.guice.AbstractMuleGuiceModule;
import org.mule.module.guice.AnnotatedService;

import com.google.inject.Provides;
import com.google.inject.Singleton;

/**
 * TODO
 */
public class GuiceLifecycleModule extends AbstractMuleGuiceModule
{
    @Override
    protected void doConfigure() throws Exception
    {
        /*
        <service name="MulePrototypeService">
            <inbound>
                <vm:inbound-endpoint path="MulePrototypeService.In" />
            </inbound>
            <component>
                <prototype-object class="org.mule.test.integration.components.LifecycleTrackerComponent">
                    <property key="property" value="mps" />
                </prototype-object>
            </component>
        </service>
        */

//        PrototypeService service = new PrototypeService();
//        service.setProperty("mps");
//        bind(PrototypeService.class).to(PrototypeService.class);
        /*

        <service name="MulePooledPrototypeService">
            <inbound>
                <vm:inbound-endpoint path="MulePooledPrototypeService.In" />
            </inbound>
            <pooled-component>
                <prototype-object class="org.mule.test.integration.components.LifecycleTrackerComponent">
                    <property key="property" value="mpps" />
                </prototype-object>
                <pooling-profile maxActive="3" initialisationPolicy="INITIALISE_ALL" />
            </pooled-component>
        </service>

        <service name="MulePooledSingletonService">
            <inbound>
                <vm:inbound-endpoint path="MulePooledSingletonService.In" />
            </inbound>
            <pooled-component>
                <singleton-object class="org.mule.test.integration.components.LifecycleTrackerComponent">
                    <property key="property" value="mpps" />
                </singleton-object>
                <pooling-profile maxActive="3" initialisationPolicy="INITIALISE_ALL" />
            </pooled-component>
        </service>
        */
//        PooledService pservice = new PooledService();
//        pservice.setProperty("mpps");
//        bind(PooledService.class).toInstance(pservice);

/*
        <service name="MuleSingletonService">
            <inbound>
                <vm:inbound-endpoint path="MuleSingletonService.In" />
            </inbound>
            <component>
                <singleton-object class="org.mule.test.integration.components.LifecycleTrackerComponent">
                    <property key="property" value="mss" />
                </singleton-object>
            </component>
        </service>
         */
//        SingletonService sservice = new SingletonService();
//        sservice.setProperty("mms");
//        bind(SingletonService.class).toInstance(sservice);

        //bind(SingletonService.class).asEagerSingleton();
//        bind(PrototypeService.class).asEagerSingleton();
//        bind(PooledService.class).();
    }

    @Provides @AnnotatedService
    public PrototypeService createPrototypeService()
    {
        PrototypeService service = new PrototypeService();
        service.setProperty("mps");
        return service;
    }

    @Provides @AnnotatedService @Singleton
    public SingletonService createSingletonService()
    {
        SingletonService service = new SingletonService();
        service.setProperty("mms");
        return service;
    }

    @Provides @AnnotatedService
    public PooledService createPooledService()
    {
        PooledService service = new PooledService();
        service.setProperty("mmps");
        return service;
    }


}