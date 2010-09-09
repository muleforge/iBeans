/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.internal.proxies;

import org.mule.api.registry.ObjectProcessor;
import org.mule.ibeans.internal.IBeansPrototypeObjectFactory;

import net.sf.cglib.proxy.Enhancer;

/**
 * Since annotated objects define their configuration, we cannot just create a new instance each time since new configuration
 * will also be created.  Instead this Factory manages the creation of new prototypes providing a subset of injection support.
 * All application annotations will be processed only once, these include {@link org.mule.ibeans.api.application.Send}, {@link org.mule.ibeans.api.application.Receive}
 * {@link org.mule.ibeans.api.application.ReceiveAndReply} and {@link org.mule.ibeans.api.application.Schedule} annotations.
 * <p/>
 * Field level injectors such as {@link org.mule.ibeans.api.client.IntegrationBean} and JSR 330 annotations such as {@link javax.inject.Inject} will be processed.
 *
 * NOTE: This variation creates a CGLib proxy with a method interceptor instead of the actual bean class
 */
public class IBeansPrototypeProxyObjectFactory extends IBeansPrototypeObjectFactory
{

    public IBeansPrototypeProxyObjectFactory(Object source)
    {
        super(source);
    }

    public Object getInstance() throws Exception
    {
        Enhancer e = new Enhancer();
        e.setCallback(new IBeanServiceMethodInterceptor(service));
        e.setSuperclass(this.getObjectClass());
        e.setInterfaces(this.getObjectClass().getInterfaces());
        Object proxy = e.create();

        for (ObjectProcessor processor : processors)
        {
            processor.process(proxy);
        }

        return proxy;
    }
}
