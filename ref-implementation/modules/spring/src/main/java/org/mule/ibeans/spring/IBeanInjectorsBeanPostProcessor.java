/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.spring;

import org.mule.api.MuleContext;
import org.mule.api.MuleException;
import org.mule.api.service.Service;
import org.mule.ibeans.IBeansContext;
import org.mule.module.annotationx.jsr330.InjectAnnotationProcessor;
import org.mule.module.annotationx.parsers.AnnotatedServiceObjectProcessor;
import org.mule.module.ibeans.config.IntegrationBeanAnnotatedObjectProcessor;

import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;

/**
 * TODO
 */
public class IBeanInjectorsBeanPostProcessor implements BeanPostProcessor
{
    private InjectAnnotationProcessor injectDelegate;
    private AnnotatedServiceObjectProcessor applicationAnnotationsProcessor;
    private IntegrationBeanAnnotatedObjectProcessor ibeanAnnotationProcessor;
    private MuleContext muleContext;


    public IBeanInjectorsBeanPostProcessor(MuleContext context)
    {
        muleContext = context;
        injectDelegate = new InjectAnnotationProcessor(muleContext);
        applicationAnnotationsProcessor = new AnnotatedServiceObjectProcessor(muleContext);
        ibeanAnnotationProcessor = new IntegrationBeanAnnotatedObjectProcessor(muleContext);
    }

    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException
    {

        bean = ibeanAnnotationProcessor.process(bean);
        bean = injectDelegate.process(bean);
        Object service = applicationAnnotationsProcessor.process(bean);
        if (service instanceof Service)
        {
            try
            {
                muleContext.getRegistry().registerService((Service) service);
            }
            catch (MuleException e)
            {
                throw new BeanCreationException("Failed to register iBeans service", e);
            }
        }
        return bean;
    }

    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException
    {
        return bean;
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        Map beans = applicationContext.getBeansOfType(IBeansContext.class);
        IBeansContext ctx = (IBeansContext) beans.values().iterator().next();
        muleContext = (MuleContext) ctx.getConfig().get(MuleContext.class.getName());
        injectDelegate = new InjectAnnotationProcessor(muleContext);
        applicationAnnotationsProcessor = new AnnotatedServiceObjectProcessor(muleContext);
    }
}
