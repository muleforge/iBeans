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

import org.mule.api.MessageProcessorAnnotationParser;
import org.mule.api.MuleContext;
import org.mule.api.MuleException;
import org.mule.api.annotations.meta.ChannelType;
import org.mule.api.annotations.meta.Router;
import org.mule.api.annotations.meta.RouterType;
import org.mule.api.component.JavaComponent;
import org.mule.api.config.MuleProperties;
import org.mule.api.config.ThreadingProfile;
import org.mule.api.context.MuleContextAware;
import org.mule.api.endpoint.InboundEndpoint;
import org.mule.api.endpoint.OutboundEndpoint;
import org.mule.api.lifecycle.InitialisationException;
import org.mule.api.object.ObjectFactory;
import org.mule.api.processor.MessageProcessor;
import org.mule.api.routing.OutboundRouter;
import org.mule.api.routing.OutboundRouterCollection;
import org.mule.api.service.ServiceAware;
import org.mule.api.source.CompositeMessageSource;
import org.mule.component.DefaultJavaComponent;
import org.mule.component.PooledJavaComponent;
import org.mule.config.AnnotationsParserFactory;
import org.mule.config.ChainedThreadingProfile;
import org.mule.config.PoolingProfile;
import org.mule.endpoint.EndpointURIEndpointBuilder;
import org.mule.expression.ExpressionConfig;
import org.mule.model.seda.SedaService;
import org.mule.module.annotationx.api.Reply;
import org.mule.module.annotationx.api.Service;
import org.mule.module.annotationx.config.AnnotatedServiceBuilder;
import org.mule.module.annotationx.config.ObjectScope;
import org.mule.routing.outbound.ExpressionMessageSplitter;
import org.mule.routing.outbound.FilteringOutboundRouter;
import org.mule.routing.outbound.ListMessageSplitter;
import org.mule.service.ServiceCompositeMessageSource;
import org.mule.transport.AbstractConnector;
import org.mule.transport.quartz.QuartzConnector;
import org.mule.transport.quartz.jobs.EndpointPollingJobConfig;
import org.mule.util.annotation.AnnotationMetaData;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.inject.Singleton;

import org.ibeans.api.IBeansProperties;

/**
 * Responsible for turning annotated objects in to services registered with Mule
 */
public class MuleiBeansAnnotatedServiceBuilder extends AnnotatedServiceBuilder
{

    public MuleiBeansAnnotatedServiceBuilder(MuleContext context) throws MuleException
    {
        super(context);
        this.parserFactory = context.getRegistry().lookupObject(AnnotationsParserFactory.class);
        assert parserFactory!=null;
    }

    @Override
    protected ObjectFactory createObjectFactory(final Object object)
    {
        IBeansObjectFactory factory;

        if (object.getClass().isAnnotationPresent(Service.class))
        {
            Service service = object.getClass().getAnnotation(Service.class);
            if (service.scope().equals(ObjectScope.SINGLETON) || service.scope().equals(ObjectScope.SINGLETON_THREADSAFE))
            {
                return new IBeansSingletonObjectFactory(object);
            }
            else
            {
                factory = new IBeansPrototypeObjectFactory(object);
            }
        }
        else if (object.getClass().isAnnotationPresent(Singleton.class))
        {
            factory = new IBeansSingletonObjectFactory(object);
        }
        else
        {
            factory = new IBeansPrototypeObjectFactory(object);
        }

        return factory;
    }

    @Override
    protected synchronized org.mule.api.service.Service create(ObjectFactory componentFactory) throws InitialisationException
    {
        JavaComponent component;
        SedaService serviceDescriptor = new SedaService(context);
        serviceDescriptor.setModel(this.getModel());
        ServiceConfig config;

        if(componentFactory instanceof ServiceAware)
        {
            ((ServiceAware)componentFactory).setService(serviceDescriptor);
        }

        componentFactory.initialise();

        if (componentFactory.getObjectClass().isAnnotationPresent(Service.class))
        {
            config = new ServiceConfig(componentFactory.getObjectClass().getAnnotation(Service.class));
        }
        else
        {
            //Default to pooled since it handles concurrency better since each object does not need to be threadsafe
            config = new ServiceConfig(ObjectScope.POOLED, 8, null);
        }

        if (config.getName() == null)
        {
            config.generateName(componentFactory.getObjectClass());
        }

        serviceDescriptor.setName(config.getName());

        if (config.getScope() == ObjectScope.POOLED)
        {
            PoolingProfile pp = new PoolingProfile();
            pp.setMaxActive(config.getMaxThreads());
            pp.setMaxIdle(config.getMaxThreads());
            PooledJavaComponent comp = new PooledJavaComponent(componentFactory, pp);
            serviceDescriptor.setComponent(comp);
            comp.setEntryPointResolverSet(new IBeansEntrypointResolverSet());
        }
        else
        {
            component = new DefaultJavaComponent(componentFactory);
            serviceDescriptor.setComponent(component);
            if (config.getScope() == ObjectScope.SINGLETON_THREADSAFE)
            {
                //Create a resolver set which will synchronise all method calls
                component.setEntryPointResolverSet(new IBeansEntrypointResolverSet(true));
            }
            else
            {
                component.setEntryPointResolverSet(new IBeansEntrypointResolverSet());
            }
        }

        ThreadingProfile tp = new ChainedThreadingProfile(context.getDefaultServiceThreadingProfile(), true);
        tp.setMaxThreadsActive(config.getMaxThreads());
        tp.setMaxThreadsIdle(config.getMaxThreads());
        tp.setPoolExhaustedAction(ThreadingProfile.WHEN_EXHAUSTED_WAIT);
        serviceDescriptor.setThreadingProfile(tp);
       // serviceDescriptor.getComponent().initialise();
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
        //processEndpointBindings(componentClass, serviceDescriptor);

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

            int threads = ((AbstractConnector) poll.getConnector()).getReceiverThreadingProfile().getMaxThreadsActive();
            EndpointPollingJobConfig jobConfig = new EndpointPollingJobConfig();
            //This may seem odd. Create a blocking job, that only gets fired once at t a time, Only quartz stateful jobs will
            //block before executing the next job until the first has finished
            jobConfig.setStateful(threads == 1);

            //Add the polling endpoint to the registry so the quartz job can access it
            context.getRegistry().registerEndpointBuilder(poll.getName(), new EndpointURIEndpointBuilder(poll));
            //Pass in the endpoint name to the quartz jub config
            jobConfig.setEndpointRef(poll.getName());
            //Set the job on the scheule endpoint
            schedule.getProperties().put(QuartzConnector.PROPERTY_JOB_CONFIG, jobConfig);
            //And finally register just the schedule endpoint with the service
            ((CompositeMessageSource)service.getMessageSource()).addSource(schedule);

            for (MessageProcessor processor : poll.getMessageProcessors())
            {
                ((ServiceCompositeMessageSource)service.getMessageSource()).addMessageProcessor(processor);
            }

            //TODO it doesn't feel right that I have to make this check here
//            if (poll instanceof InboundEndpointDecorator)
//            {
//                ((InboundEndpointDecorator) poll).onListenerAdded(service);
//            }
        }
        else
        {
            for (InboundEndpoint endpoint : endpoints.values())
            {
                ((CompositeMessageSource)service.getMessageSource()).addSource(endpoint);
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
                service.getAsyncReplyMessageSource().addSource(inboundEndpoint);
                //Lets process the reply routers
                processReplyRouters(service, clazz, method);
            }
        }
    }

    protected void processReplyRouters(org.mule.api.service.Service service, Class clazz, Method method) throws MuleException
    {
        Collection routerParsers = context.getRegistry().lookupObjects(MessageProcessorAnnotationParser.class);
        for (int i = 0; i < method.getAnnotations().length; i++)
        {
            Annotation annotation = method.getAnnotations()[i];
            Router routerAnnotation = annotation.annotationType().getAnnotation(Router.class);
            if (routerAnnotation != null && routerAnnotation.type() == RouterType.ReplyTo)
            {
                for (Iterator iterator = routerParsers.iterator(); iterator.hasNext();)
                {
                    MessageProcessorAnnotationParser parser = (MessageProcessorAnnotationParser) iterator.next();
                    if (parser.supports(annotation, clazz, method))
                    {
                        MessageProcessor router = parser.parseMessageProcessor(annotation);
                        //Todo, wrap lifecycle
                        if (router instanceof MuleContextAware)
                        {
                            ((MuleContextAware) router).setMuleContext(context);
                        }
                        service.getAsyncReplyMessageSource().addMessageProcessor(router);
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
                outboundEndpoint.getProperties().put(IBeansProperties.ENDPOINT_METHOD, method.toString());
                router.addRoute(outboundEndpoint);
                outboundFound = true;
            }
        }

        if (outboundFound)
        {
            router.setMuleContext(context);
            router.initialise();
            ((OutboundRouterCollection)service.getOutboundMessageProcessor()).addRoute(router);
        }
    }

    protected OutboundRouter processOutboundRouter(Class clazz, Method method) throws MuleException
    {
        Collection routerParsers = context.getRegistry().lookupObjects(MessageProcessorAnnotationParser.class);
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
                    MessageProcessorAnnotationParser parser = (MessageProcessorAnnotationParser) iterator.next();
                    if (parser.supports(annotation, clazz, method))
                    {
                        router = (OutboundRouter) parser.parseMessageProcessor(annotation);
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
        router.setMuleContext(context);
        router.initialise();
        return router;
    }

    private class ServiceConfig
    {
        private ObjectScope scope;
        private int maxThreads;
        private String name;

        private ServiceConfig(Service config)
        {
            scope = config.scope();
            maxThreads = config.maxAsyncThreads();
            name = (config.name().length() == 0 ? null : config.name());
        }

        private ServiceConfig(ObjectScope scope, int maxThreads, String name)
        {
            this.scope = scope;
            this.maxThreads = maxThreads;
            this.name = (name == null || name.length() == 0 ? null : name);
        }

        public ObjectScope getScope()
        {
            return scope;
        }

        public int getMaxThreads()
        {
            return maxThreads;
        }

        public String getName()
        {
            return name;
        }

        public void generateName(Class serviceClass)
        {
            name = serviceClass.getSimpleName() + ".service";
        }
    }
}
