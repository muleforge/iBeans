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
import org.mule.api.lifecycle.InitialisationException;
import org.mule.api.transformer.DiscoverableTransformer;
import org.mule.api.transformer.TransformerException;
import org.mule.expression.transformers.ExpressionTransformer;
import org.mule.ibeans.api.application.params.MessagePayload;
import org.mule.transformer.AbstractMessageAwareTransformer;
import org.mule.transport.NullPayload;
import org.mule.utils.AnnotationUtils;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
import java.util.WeakHashMap;

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
    private ExpressionTransformer paramTransformer = null;
    private Collection<ObjectResolver> resolvers;
    private Map<Class, Object> cachedObjects = new WeakHashMap<Class, Object>();
    private boolean sourceAnnotated = false;

    public AnnotatedTransformerProxy(int weighting, Object proxy, Method transformMethod, Class[] additionalSourceTypes) throws TransformerException, InitialisationException
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
        messageAware = MuleMessage.class.isAssignableFrom(transformMethod.getParameterTypes()[0]);
        this.transformMethod = transformMethod;
        if (additionalSourceTypes.length > 0)
        {
            if (messageAware)
            {
                logger.error("Transformer: " + getName() + " is MuleMessage aware, this means additional source types configured on the transformer will be ignorred. Source types are: " + additionalSourceTypes);
            }
            else
            {
                for (int i = 0; i < additionalSourceTypes.length; i++)
                {
                    registerSourceType(additionalSourceTypes[i]);

                }
            }
        }
        //The first Param is the always the object to transform
        Class source = transformMethod.getParameterTypes()[0];
        registerSourceType(source);
        sourceAnnotated = (transformMethod.getParameterAnnotations()[0].length > 0 &&
                transformMethod.getParameterAnnotations()[0][0] instanceof MessagePayload);

        setName(proxy.getClass().getSimpleName() + "." + transformMethod.getName());

    }

    @Override
    public void initialise() throws InitialisationException
    {
        super.initialise();
        if (AnnotationUtils.methodHasParamAnnotations(transformMethod))
        {
            try
            {
                paramTransformer = AnnotationUtils.getTransformerForMethodWithAnnotations(transformMethod, muleContext);
            }
            catch (TransformerException e)
            {
                throw new InitialisationException(e, this);
            }
        }
        resolvers = muleContext.getRegistry().lookupObjects(ObjectResolver.class);
    }

    public Object transform(MuleMessage message, String outputEncoding) throws TransformerException
    {
        Object firstArg = null;
        Object[] params = new Object[transformMethod.getParameterTypes().length];
        int paramCounter = 0;

        //the param treansformer will convert the message to one or more objects depending on the annotations on the method
        //parameter
        if (paramTransformer != null)
        {
            Object paramArgs = paramTransformer.transform(message, outputEncoding);

            if (paramArgs != null && paramArgs.getClass().isArray())
            {
                Object[] temp = (Object[]) paramArgs;
                //if the source is annotated, the paramTransformer will figure out the first parameter
                if (!sourceAnnotated)
                {
                    paramCounter++;
                }
                for (int i = 0; i < temp.length; i++)
                {
                    params[paramCounter++] = temp[i];
                }
            }
            else
            {
                //if the source is annotated, the paramTransformer will figure out the first parameter
                if (!sourceAnnotated)
                {
                    paramCounter++;
                }
                params[paramCounter++] = paramArgs;
            }
        }

        if (messageAware)
        {
            firstArg = message;
        }
        else if (!sourceAnnotated)
        {
            //This will perform any additional transformation from the source type to the method parameter type
            firstArg = message.getPayload(transformMethod.getParameterTypes()[0]);
        }

        //if the source is annotated, the paramTransformer will figure out the first parameter
        if (!sourceAnnotated)
        {
            params[0] = firstArg;
            if (paramCounter == 0)
            {
                paramCounter++;
            }
        }
        //Lets see if there are any context objects to inject (i.e. JAXB)
        if (paramCounter < transformMethod.getParameterTypes().length)
        {
            for (int i = paramCounter; i < transformMethod.getParameterTypes().length; i++)
            {
                Object o;
                Class type = transformMethod.getParameterTypes()[i];
                o = cachedObjects.get(type);
                if (o != null)
                {
                    params[paramCounter++] = o;
                    continue;
                }
                for (ObjectResolver resolver : resolvers)
                {
                    try
                    {
                        o = resolver.findObject(type, transformMethod, muleContext);
                        if (o != null)
                        {
                            params[paramCounter++] = o;
                            cachedObjects.put(type, o);
                            break;
                        }
                    }
                    catch (Exception e)
                    {
                        throw new TransformerException(this, e);
                    }

                }
                if (o == null)
                {
                    throw new IllegalArgumentException("Failed to find resolver for object type: " + type + " for transform method: " + transformMethod);
                }
            }
        }
        try
        {
            return transformMethod.invoke(proxy, params);
        }
        catch (Exception e)
        {
            e.printStackTrace();
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
    protected Object checkReturnClass(Object object) throws TransformerException
    {
        if (object instanceof NullPayload)
        {
            return null;
        }
        return super.checkReturnClass(object);
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
