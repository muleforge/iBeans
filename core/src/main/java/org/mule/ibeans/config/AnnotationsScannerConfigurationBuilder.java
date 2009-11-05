/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.config;

import org.mule.api.MuleContext;
import org.mule.api.config.ConfigurationException;
import org.mule.api.lifecycle.LifecycleManager;
import org.mule.config.annotations.Service;
import org.mule.config.annotations.endpoints.Channel;
import org.mule.config.builders.AbstractConfigurationBuilder;
import org.mule.ibeans.internal.TomcatJndiRegistry;
import org.mule.util.ClassUtils;
import org.mule.util.scan.ClasspathScanner;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * This builder discovers objects to load on the classpath by scanning for objects with methods annotated with iBeans annotations.
 */
public class AnnotationsScannerConfigurationBuilder extends AbstractConfigurationBuilder
{
    private ClassLoader classLoader;

    public AnnotationsScannerConfigurationBuilder()
    {
        this.classLoader = Thread.currentThread().getContextClassLoader();
    }

    public AnnotationsScannerConfigurationBuilder(ClassLoader classLoader)
    {
        this.classLoader = classLoader;
    }

    protected void doConfigure(MuleContext muleContext) throws Exception
    {
//        DefaultJmxSupportAgent agent = new DefaultJmxSupportAgent();
//        agent.setLoadMx4jAgent(false);
//        muleContext.getRegistry().registerAgent(agent);

        try
        {
            TomcatJndiRegistry registry = new TomcatJndiRegistry();
            registry.initialise();
            muleContext.addRegistry(registry);
        }
        catch (Exception e)
        {
            logger.error("Not running in Tcat/Tomcat, context configuration features will not work");
        }

        Map<String, Object> services = findServices();
        for (Map.Entry<String, Object> entry : services.entrySet())
        {
            muleContext.getRegistry().registerObject(entry.getKey(), entry.getValue());
        }

        //Load any classes that contain @transformer annotations.  The registry will take care of initializing the
        //actual transformer objects
        ClasspathScanner scanner = new ClasspathScanner(classLoader, "");
        Set<Class> transformerClasses = scanner.scanFor(org.mule.ibeans.api.application.Transformer.class);

        for (Class aClass : transformerClasses)
        {
            Object trans = ClassUtils.instanciateClass(aClass, ClassUtils.NO_ARGS);
            muleContext.getRegistry().registerObject("_transformer." + aClass.getSimpleName(), trans);
        }
    }

    protected void applyLifecycle(LifecycleManager lifecycleManager) throws Exception
    {
        //do nothing
    }

    protected Map<String, Object> findServices() throws ConfigurationException
    {
        ClasspathScanner scanner = new ClasspathScanner(classLoader, "");
        Set<Class> serviceClasses;
        try
        {
            serviceClasses = scanner.scanFor(Channel.class);
        }
        catch (IOException e)
        {
            throw new ConfigurationException(e);
        }

        Map<String, Object> services = new HashMap<String, Object>(serviceClasses.size());
        for (Class serviceClass : serviceClasses)
        {
            if (serviceClass.isInterface())
            {
                continue;
            }
            try
            {
                Object service = ClassUtils.instanciateClass(serviceClass, ClassUtils.NO_ARGS);
                String name = service.getClass().getName();
                //We don't document the @Service attribute since it's not recommended by iBeans for simplification purposes
                //but lets support it here because Mule supports it
                Service serviceAnn = (Service) serviceClass.getAnnotation(Service.class);

                if (serviceAnn != null)
                {
                    name = serviceAnn.name();
                }
                services.put(name, service);
            }
            catch (Exception e)
            {
                throw new ConfigurationException(e);
            }
        }
        return services;
    }
}
