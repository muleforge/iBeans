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

import org.mule.api.DefaultMuleException;
import org.mule.api.MuleContext;
import org.mule.api.MuleException;
import org.mule.api.RouterAnnotationParser;
import org.mule.api.config.MuleProperties;
import org.mule.api.context.MuleContextAware;
import org.mule.api.endpoint.InboundEndpoint;
import org.mule.api.endpoint.OutboundEndpoint;
import org.mule.api.lifecycle.InitialisationException;
import org.mule.api.object.ObjectFactory;
import org.mule.api.routing.OutboundRouter;
import org.mule.api.service.Service;
import org.mule.component.DefaultJavaComponent;
import org.mule.config.annotations.endpoints.ChannelType;
import org.mule.config.annotations.endpoints.Reply;
import org.mule.config.annotations.routing.Router;
import org.mule.config.annotations.routing.RouterType;
import org.mule.expression.ExpressionConfig;
import org.mule.ibeans.api.client.IntegrationBean;
import org.mule.ibeans.internal.client.AnnotatedInterfaceBinding;
import org.mule.impl.annotations.AnnotatedServiceBuilder;
import org.mule.model.seda.SedaService;
import org.mule.object.AbstractObjectFactory;
import org.mule.object.PrototypeObjectFactory;
import org.mule.object.SingletonObjectFactory;
import org.mule.routing.outbound.ExpressionMessageSplitter;
import org.mule.routing.outbound.FilteringOutboundRouter;
import org.mule.routing.outbound.ListMessageSplitter;
import org.mule.transport.quartz.QuartzConnector;
import org.mule.transport.quartz.jobs.EndpointPollingJobConfig;
import org.mule.util.BeanUtils;
import org.mule.utils.AnnotationMetaData;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.inject.Singleton;

/**
 * Responsible for turning annotated objects in to services registered with Mule
 */
public class MuleiBeansAnnotatedServiceBuilder extends AnnotatedServiceBuilder
{

    public MuleiBeansAnnotatedServiceBuilder(MuleContext context) throws MuleException
    {
        super(context);
    }

    @Override
    protected ObjectFactory createObjectFactory(Object object)
    {
        AbstractObjectFactory factory;
        if (object.getClass().isAnnotationPresent(Singleton.class))
        {
            factory = new SingletonObjectFactory(object);
            factory.setMuleContext(context);
        }
        else
        {
            factory = new PrototypeObjectFactory(object.getClass(), BeanUtils.describeBean(object));
            factory.setMuleContext(context);
        }
        return factory;
    }

    @Override
    protected synchronized Service create(ObjectFactory componentFactory) throws InitialisationException
    {
        Service serviceDescriptor = new SedaService();
        serviceDescriptor.setMuleContext(context);
        componentFactory.initialise();
        //Create a default service
        serviceDescriptor.setName(componentFactory.getObjectClass().getName() + ".service");
        serviceDescriptor.setComponent(new DefaultJavaComponent(componentFactory));

        serviceDescriptor.getComponent().initialise();
        serviceDescriptor.setModel(getModel());
        return serviceDescriptor;
    }

    public org.mule.api.service.Service createService(Object object) throws MuleException
    {
        org.mule.api.service.Service serviceDescriptor = create(createObjectFactory(object));
        Class componentClass = object.getClass();

        //These are Class level annotations
        processInboundRouters(componentClass, serviceDescriptor);

        //check for  bindings (Field level annotations)
        processEndpointBindings(componentClass, serviceDescriptor);

        processServiceProxies(object, serviceDescriptor);


        for (int i = 0; i < componentClass.getMethods().length; i++)
        {
            Method method = componentClass.getMethods()[i];
            processInbound(serviceDescriptor, componentClass, method);
            processReply(serviceDescriptor, componentClass, method);
            processOutbound(serviceDescriptor, componentClass, method);
        }
        return serviceDescriptor;
    }


    protected void processInbound(org.mule.api.service.Service service, Class clazz, Method method) throws MuleException
    {

        InboundEndpoint inboundEndpoint;
        Map<String, InboundEndpoint> endpoints = new HashMap<String, InboundEndpoint>();
        for (int i = 0; i < method.getAnnotations().length; i++)
        {
            Annotation annotation = method.getAnnotations()[i];

            inboundEndpoint = tryInboundEndpointAnnotation(
                    new AnnotationMetaData(clazz, method, ElementType.METHOD, annotation), ChannelType.Inbound);
            if (inboundEndpoint != null)
            {
                inboundEndpoint.getProperties().put(MuleProperties.MULE_METHOD_PROPERTY, method.getName());
                endpoints.put(inboundEndpoint.getEndpointURI().getScheme(), inboundEndpoint);
            }
        }
        //Special handling of @Receive and @Schedule combination
        if (endpoints.containsKey("quartz") && endpoints.size() == 2)
        {
            InboundEndpoint schedule = null;
            InboundEndpoint poll = null;
            for (Map.Entry<String, InboundEndpoint> endpointEntry : endpoints.entrySet())
            {
                if (endpointEntry.getKey().equals("quartz"))
                {
                    schedule = endpointEntry.getValue();
                }
                else
                {
                    poll = endpointEntry.getValue();
                }
            }
            EndpointPollingJobConfig jobConfig = new EndpointPollingJobConfig();
            //Add the polling endpoint to the registry so the quartz job can access it
            context.getRegistry().registerEndpoint(poll);
            //Pass in the endpoint name to the quartz jub config
            jobConfig.setEndpointRef(poll.getName());
            //Set the job on the scheule endpoint
            schedule.getProperties().put(QuartzConnector.PROPERTY_JOB_CONFIG, jobConfig);
            //And finally register just the schedule endpoint with the service
            service.getInboundRouter().addEndpoint(schedule);
        }
        else
        {
            for (InboundEndpoint endpoint : endpoints.values())
            {
                service.getInboundRouter().addEndpoint(endpoint);
            }
        }
    }

    protected void processReply(org.mule.api.service.Service service, Class clazz, Method method) throws MuleException
    {
        InboundEndpoint inboundEndpoint;
        Annotation annotation = method.getAnnotation(Reply.class);
        if (annotation != null)
        {
            inboundEndpoint = tryInboundEndpointAnnotation(
                    new AnnotationMetaData(clazz, method, ElementType.METHOD, annotation), ChannelType.Reply);
            if (inboundEndpoint != null)
            {
                service.getResponseRouter().addEndpoint(inboundEndpoint);
                //Lets process the reply routers
                processReplyRouters(service, clazz, method);
            }
        }
    }

    protected void processReplyRouters(org.mule.api.service.Service service, Class clazz, Method method) throws MuleException
    {
        Collection routerParsers = context.getRegistry().lookupObjects(RouterAnnotationParser.class);
        for (int i = 0; i < method.getAnnotations().length; i++)
        {
            Annotation annotation = method.getAnnotations()[i];
            Router routerAnnotation = annotation.annotationType().getAnnotation(Router.class);
            if (routerAnnotation != null && routerAnnotation.type() == RouterType.ReplyTo)
            {
                for (Iterator iterator = routerParsers.iterator(); iterator.hasNext();)
                {
                    RouterAnnotationParser parser = (RouterAnnotationParser) iterator.next();
                    if (parser.supports(annotation, clazz, method))
                    {
                        org.mule.api.routing.Router router = parser.parseRouter(annotation);
                        //Todo, wrap lifecycle
                        if (router instanceof MuleContextAware)
                        {
                            ((MuleContextAware) router).setMuleContext(context);
                        }
                        router.initialise();
                        service.getResponseRouter().addRouter(router);
                        break;
                    }
                }
            }
        }
    }

    protected void processOutbound(org.mule.api.service.Service service, Class clazz, Method method) throws MuleException
    {
        OutboundRouter router = processOutboundRouter(clazz, method);
        Reply replyEp = method.getAnnotation(Reply.class);
        if (replyEp != null)
        {
            router.setReplyTo(replyEp.uri());
        }

        OutboundEndpoint outboundEndpoint;

        boolean outboundFound = false;
        for (int i = 0; i < method.getAnnotations().length; i++)
        {
            Annotation annotation = method.getAnnotations()[i];
            outboundEndpoint = tryOutboundEndpointAnnotation(
                    new AnnotationMetaData(clazz, method, ElementType.METHOD, annotation), ChannelType.Outbound);
            //TODO need to add a filter so that only events with the correct method header are routed via this endpoint
            if (outboundEndpoint != null)
            {
                //Handle split expressions on the @Send annotation
                String splitExpression = (String) outboundEndpoint.getProperties().remove("split-expression");
                if (splitExpression != null)
                {
                    if (splitExpression.equals("default"))
                    {
                        //Make sure the method return type is compatible with
                        if (List.class.isAssignableFrom(method.getReturnType()))
                        {
                            router = new ListMessageSplitter();
                        }
                        else
                        {
                            throw new IllegalArgumentException("A split expression has been set on a @Send annotation to 'default' which means that the retern class from " + method.toString() + " needs to be assignable from java.util.List");
                        }
                    }
                    else
                    {
                        //create and validate the expression
                        ExpressionConfig config = new ExpressionConfig();
                        config.parse(splitExpression);
                        config.validate(context.getExpressionManager());
                        router = new ExpressionMessageSplitter(config);

                    }
                }
                router.addEndpoint(outboundEndpoint);
                outboundFound = true;
            }
        }

        if (outboundFound)
        {
            if (router instanceof MuleContextAware)
            {
                ((MuleContextAware) router).setMuleContext(context);
            }
            router.initialise();
            service.getOutboundRouter().addRouter(router);
        }
    }

    protected OutboundRouter processOutboundRouter(Class clazz, Method method) throws MuleException
    {
        Collection routerParsers = context.getRegistry().lookupObjects(RouterAnnotationParser.class);
        OutboundRouter router = null;

        for (int i = 0; i < method.getAnnotations().length; i++)
        {
            Annotation annotation = method.getAnnotations()[i];
            Router routerAnnotation = annotation.annotationType().getAnnotation(Router.class);
            if (routerAnnotation != null && routerAnnotation.type() == RouterType.Outbound)
            {
                if (router != null)
                {
                    //This is only here to add some validation
                    throw new IllegalStateException("You can onnly configure one outbound router on a service");
                }
                for (Iterator iterator = routerParsers.iterator(); iterator.hasNext();)
                {
                    RouterAnnotationParser parser = (RouterAnnotationParser) iterator.next();
                    if (parser.supports(annotation, clazz, method))
                    {
                        router = (OutboundRouter) parser.parseRouter(annotation);
                        break;
                    }
                }
            }
        }
        if (router == null)
        {
            router = new FilteringOutboundRouter();
        }
        //Todo, wrap lifecycle
        if (router instanceof MuleContextAware)
        {
            ((MuleContextAware) router).setMuleContext(context);
        }
        router.initialise();
        return router;
    }

    protected void processServiceProxies(Object object, org.mule.api.service.Service service) throws MuleException
    {
        Field[] fields = object.getClass().getDeclaredFields();
        for (int i = 0; i < fields.length; i++)
        {
            Field field = fields[i];
            if (field.isAnnotationPresent(IntegrationBean.class))
            {
                AnnotatedInterfaceBinding router = new AnnotatedInterfaceBinding(service);
                router.setMuleContext(context);
                router.setInterface(field.getType());
                //No need for setter methods
                try
                {
                    field.setAccessible(true);
                    field.set(object, router.createProxy(object));
                }
                catch (IllegalAccessException e)
                {
                    throw new DefaultMuleException(e);
                }
                //Requires that there is a setter method for the service proxy field
                //((JavaComponent) service.getComponent()).getBindingCollection().addRouter(router);
            }
        }
    }
}