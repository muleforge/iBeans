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

import org.mule.api.lifecycle.InitialisationException;
import org.mule.api.service.Service;
import org.mule.ibeans.internal.IBeansObjectFactory;
import org.mule.object.AbstractObjectFactory;

import java.lang.ref.SoftReference;

import net.sf.cglib.proxy.Enhancer;

/**
 * A singleton factory that creates a CGLib proxy object that can honour annotations per method call.
 * This issue with this class is that we cannot easily decorate an existing instance to proxy its method calls.
 * We can create a wrapper but the would require the component object to define an interface for all methods,
 * which puts some constraints on the developer. Right now this implementation recreates the singleton object as
 * a proxy.  We'd need to overwrite the existing instance in the registry not to have two singleton instances around
 * So, if you are looking at this class bare in mind its a work in progress and currently not used. 
 */
public class IBeansSingletonProxyObjectFactory extends AbstractObjectFactory implements IBeansObjectFactory
{
    protected Service service;
    protected SoftReference instance;

    public IBeansSingletonProxyObjectFactory(Object object)
    {
        super(object.getClass());
        this.instance = new SoftReference<Object>(object);
    }

    public void setService(Service service)
    {
        this.service = service;
    }

    @Override
    public void initialise() throws InitialisationException
    {
        super.initialise();
        //Object proxy = Proxy.newProxyInstance(getClass().getClassLoader(), instance.getClass().getInterfaces(), new IBeanServiceInvocationHandler(service, instance));
        Enhancer e = new Enhancer();
        e.setCallback(new IBeanServiceMethodInterceptor(service));
        e.setInterfaces(instance.get().getClass().getInterfaces());
        e.setSuperclass(instance.get().getClass());
        Object proxy = e.create();
        try
        {
            instance = new SoftReference<Object>(proxy);
        }
        catch (Exception ex)
        {
            throw new InitialisationException(ex, this);
        }
    }

    public boolean isAutoWireObject()
    {
        return false; //don't autowire singletons since the instance has already been processed
    }

    @Override
    public Object getInstance() throws Exception
    {
        return instance.get();
    }
}
