/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.internal;

import org.mule.api.MuleMessage;
import org.mule.api.transformer.DiscoverableTransformer;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractMessageAwareTransformer;

import java.lang.reflect.Method;

/**
 * Creates a Mule {@link org.mule.api.transformer.Transformer} proxy around a transform method. The
 * transformer will be given a generated name which is the short name of the class and the method name
 * separated with a '.' i.e. 'MyTransformers.fooToBar'
 */
class AnnotatedTransformerProxy extends AbstractMessageAwareTransformer implements DiscoverableTransformer
{
    private int weighting;

    private Object proxy;

    private Method transformMethod;
    private boolean messageAware = false;

    public AnnotatedTransformerProxy(int weighting, Object proxy, Method transformMethod)
    {
        this.weighting = weighting;
        this.proxy = proxy;
        if (transformMethod.getReturnType().equals(Void.TYPE))
        {
            throw new IllegalArgumentException("Method not a valid transform method, void return type: " + transformMethod.getName());
        }
        this.transformMethod = transformMethod;
        setReturnClass(transformMethod.getReturnType());

        if (transformMethod.getParameterTypes().length == 0)
        {
            throw new IllegalArgumentException("Method not a valid transform method, no parameters: " + transformMethod.getName());
        }
        else if (transformMethod.getParameterTypes().length > 1)
        {

            throw new IllegalArgumentException("Method not a valid transform method, can only have 1 parameter: " + transformMethod.getName());

        }
        messageAware = MuleMessage.class.isAssignableFrom(transformMethod.getParameterTypes()[0]);
        this.transformMethod = transformMethod;
        registerSourceType(transformMethod.getParameterTypes()[0]);
        setName(proxy.getClass().getSimpleName() + "." + transformMethod.getName());
    }

    public Object transform(MuleMessage message, String outputEncoding) throws TransformerException
    {
        Object args;
        if (messageAware)
        {
            args = message;
        }
        else
        {
            args = message.getPayload(transformMethod.getParameterTypes()[0]);
        }
        try
        {
            return transformMethod.invoke(proxy, args);
        }
        catch (Exception e)
        {
            throw new TransformerException(this, e);
        }
    }

    public int getPriorityWeighting()
    {
        return weighting;
    }

    public void setPriorityWeighting(int weighting)
    {
        throw new UnsupportedOperationException("setPriorityWeighting");
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }

        AnnotatedTransformerProxy that = (AnnotatedTransformerProxy) o;

        if (messageAware != that.messageAware)
        {
            return false;
        }
        if (weighting != that.weighting)
        {
            return false;
        }
        if (proxy != null ? !proxy.equals(that.proxy) : that.proxy != null)
        {
            return false;
        }
        if (transformMethod != null ? !transformMethod.equals(that.transformMethod) : that.transformMethod != null)
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = weighting;
        result = 31 * result + (proxy != null ? proxy.hashCode() : 0);
        result = 31 * result + (transformMethod != null ? transformMethod.hashCode() : 0);
        result = 31 * result + (messageAware ? 1 : 0);
        return result;
    }
}
