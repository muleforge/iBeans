/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.internal;

import org.mule.api.MuleContext;
import org.mule.api.MuleException;
import org.mule.api.lifecycle.InitialisationException;
import org.mule.api.registry.ObjectProcessor;
import org.mule.api.service.Service;
import org.mule.module.annotationx.jsr330.InjectAnnotationProcessor;
import org.mule.module.annotationx.jsr330.NamedAnnotationProcessor;
import org.mule.module.ibeans.config.IntegrationBeanAnnotatedObjectProcessor;
import org.mule.object.AbstractObjectFactory;
import org.mule.util.BeanUtils;
import org.mule.util.ClassUtils;

import java.lang.ref.SoftReference;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Since annotated objects define their configuration, we cannot just create a new instance each time since new configuration
 * will also be created.  Instead this Factory manages the creation of new prototypes providing a subset of injection support.
 * All application annotations will be processed only once, these include {@link org.mule.ibeans.api.application.Send}, {@link org.mule.module.annotationx.api.Receive}
 * {@link org.mule.module.annotationx.api.ReceiveAndReply} and {@link org.mule.transport.quatz.Schedule} annotations.
 * <p/>
 * Field level injectors such as {@link org.ibeans.annotation.IntegrationBean} and JSR 330 annotations such as {@link javax.inject.Inject} will be processed.
 */
public class IBeansPrototypeObjectFactory extends AbstractObjectFactory implements IBeansObjectFactory
{
    protected Set<ObjectProcessor> processors;

    /**
     * this is a reference to a created instance of the object that can be pre-configured
     * the bean properties on this object can be copied to the prototype object created by this factory
     */
    protected SoftReference<Object> template;

    protected Service service;

    public void setService(Service service)
    {
        this.service = service;
    }

    public IBeansPrototypeObjectFactory(Object template)
    {
        super(template.getClass());
        this.template = new SoftReference<Object>(template);
    }

    @Override
    public Object getInstance(MuleContext muleContext) throws Exception
    {
        initProcessors(muleContext);
        Object o = ClassUtils.instanciateClass(this.getObjectClass(), ClassUtils.NO_ARGS);

        for (ObjectProcessor processor : processors)
        {
            processor.process(o);
        }

        return o;
    }


    public void initProcessors(MuleContext muleContext) throws InitialisationException
    {
        if(processors==null)
        {
            processors = new HashSet<ObjectProcessor>();

            try
            {
                processors.add(new AnnotatedPrototypeIntegrationBeanObjectProcessor());
                processors.add(initProcessor(new InjectAnnotationProcessor(), muleContext));
                processors.add(initProcessor(new NamedAnnotationProcessor(), muleContext));
            }
            catch (MuleException e)
            {
                throw new InitialisationException(e, this);
            }
        }
    }

    protected ObjectProcessor initProcessor(ObjectProcessor processor, MuleContext muleContext) throws MuleException
    {
        muleContext.getRegistry().applyProcessors(processor);
        return processor;
    }

    @Override
    public boolean isExternallyManagedLifecycle()
    {
        return false;
    }

    private class AnnotatedPrototypeIntegrationBeanObjectProcessor extends IntegrationBeanAnnotatedObjectProcessor
    {
        public Object process(Object object)
        {
            String key = null;
            Object value ;
            try
            {
                Map props = BeanUtils.describe(template.get());
                for (Iterator iterator = props.keySet().iterator(); iterator.hasNext();)
                {
                    key = (String) iterator.next();
                    value = props.get(key);
                    //we do this because BeanUtils.describe() will include all null fields too
                    //the impact is that users cannot nullify a field that is not null
                    if(value==null) continue;
                    Field field = object.getClass().getDeclaredField(key);
                    if(!Modifier.isFinal(field.getModifiers()))
                    {
                        field.setAccessible(true);
                        field.set(object, value);
                    }
                }
            }
            catch (Exception e)
            {
                throw new RuntimeException("Failed to set field '" + key + " on " + object);
            }

            return object;
        }
    }

    public boolean isAutoWireObject()
    {
        return false;
    }
}
