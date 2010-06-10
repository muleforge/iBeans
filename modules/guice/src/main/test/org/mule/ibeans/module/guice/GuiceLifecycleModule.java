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

import com.google.inject.name.Names;

/**
 * TODO
 */
public class GuiceLifecycleModule extends AbstractMuleGuiceModule
{
    @Override
    protected void configure()
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

        bindConstant().annotatedWith(Names.named("mps-value")).to("mps");
        bindConstant().annotatedWith(Names.named("mms-value")).to("mms");
        bindConstant().annotatedWith(Names.named("mmps-value")).to("mmps");

        bind(SingletonService.class).asEagerSingleton();
        //TODO
        //bind(PooledService.class).in(MuleScopes.pooledScope());
    }

}
