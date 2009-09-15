/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.internal.client;

import org.mule.api.EndpointAnnotationParser;
import org.mule.api.MessagingException;
import org.mule.api.MuleMessage;
import org.mule.api.MuleRuntimeException;
import org.mule.api.MuleSession;
import org.mule.api.endpoint.ImmutableEndpoint;
import org.mule.api.endpoint.InboundEndpoint;
import org.mule.api.endpoint.OutboundEndpoint;
import org.mule.api.routing.InterfaceBinding;
import org.mule.api.routing.OutboundRouter;
import org.mule.api.service.Service;
import org.mule.config.annotations.endpoints.Channel;
import org.mule.config.i18n.CoreMessages;
import org.mule.ibeans.api.client.Call;
import org.mule.ibeans.api.client.Template;
import org.mule.ibeans.api.client.params.HeaderParam;
import org.mule.ibeans.api.client.params.Payload;
import org.mule.ibeans.api.client.params.PayloadParam;
import org.mule.management.stats.RouterStatistics;
import org.mule.model.seda.SedaService;
import org.mule.routing.AbstractRouter;
import org.mule.routing.binding.DefaultInterfaceBinding;
import org.mule.routing.outbound.OutboundPassThroughRouter;
import org.mule.transport.http.HttpConnector;
import org.mule.utils.AnnotationMetaData;
import org.mule.utils.AnnotationUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * TODO
 */
public class AnnotatedInterfaceBinding extends AbstractRouter implements InterfaceBinding
{

    private static final Log logger = LogFactory.getLog(AnnotatedInterfaceBinding.class);

    private Class interfaceClass;

    // The router used to actually dispatch the message
    protected OutboundRouter outboundRouter;

    protected Service service;

    public AnnotatedInterfaceBinding()
    {
        this(new SedaService());
    }

    public AnnotatedInterfaceBinding(Service service)
    {
        setRouterStatistics(new RouterStatistics(RouterStatistics.TYPE_BINDING));
        this.service = service;
    }

    public String getMethod()
    {
        throw new UnsupportedOperationException();
    }

    public void setMethod(String method)
    {
        throw new UnsupportedOperationException();
    }

    public MuleMessage route(MuleMessage message, MuleSession session) throws MessagingException
    {
        return outboundRouter.route(message, session);
    }

    public void setInterface(Class interfaceClass)
    {
        this.interfaceClass = interfaceClass;
    }

    public Class getInterface()
    {
        return interfaceClass;
    }

    public Object createProxy(Object target)
    {
        Map<String, String> evals = new HashMap<String, String>();

        try
        {
            IntegrationBeanInvocationHandler handler = new IntegrationBeanInvocationHandler(interfaceClass, service, muleContext);

            List<AnnotationMetaData> annos = AnnotationUtils.getAllMethodAnnotations(getInterface());
            for (AnnotationMetaData metaData : annos)
            {
                Channel channel = metaData.getAnnotation().annotationType().getAnnotation(Channel.class);
                if (channel != null)
                {
                    Collection c = muleContext.getRegistry().lookupObjects(EndpointAnnotationParser.class);
                    for (Iterator iterator = c.iterator(); iterator.hasNext();)
                    {
                        EndpointAnnotationParser parser = (EndpointAnnotationParser) iterator.next();
                        if (parser.supports(metaData.getAnnotation(), metaData.getClazz(), metaData.getMember()))
                        {
                            InterfaceBinding binding;
                            Method method = (Method) metaData.getMember();
                            boolean callChannel = false;
                            Annotation ann;
                            //This is a little messy, but we need to detect whether we are doing a Mule 'send' or Mule 'request' call.
                            //Request calls get data from a resource such as DB, email inbox or message queue. These types of request will
                            //not have any payload or headers defined.
                            //The other way to handle this is to introduce a new annotation to explicitly handle this (See the Get annotation).
                            //The issue is it may be difficult for the user to understand the difference between @Call and @Get. Instead we figure it out
                            //here.
                            boolean http = ((Call) metaData.getAnnotation()).uri().startsWith("http");
                            for (int i = 0; i < method.getParameterAnnotations().length; i++)
                            {
                                ann = method.getParameterAnnotations()[i][0];
                                if (ann.annotationType().equals(Payload.class) ||
                                        ann.annotationType().equals(PayloadParam.class) ||
                                        ann.annotationType().equals(HeaderParam.class) ||
                                        http)
                                {
                                    //TODO URGENT remove the HTTP hack above. Its required becuase HTTP request on the dispatcher
                                    //don't honour authenitcation for some reason.  Also even though there may not be any headers
                                    //defined we still need to attach some headers to the HTTP method. This is very difficult when
                                    //using request
                                    callChannel = true;

                                    break;
                                }
                            }
                            if (callChannel)
                            {
                                OutboundEndpoint endpoint = parser.parseOutboundEndpoint(metaData.getAnnotation());
                                binding = new DefaultInterfaceBinding();
                                binding.setEndpoint(endpoint);
                            }
                            else
                            {
                                InboundEndpoint endpoint = parser.parseInboundEndpoint(metaData.getAnnotation());
                                binding = new DynamicRequestInterfaceBinding();
                                binding.setEndpoint(endpoint);
                            }
                            //Another HTTP hack.  We need to differenciate between GET and POST
                            if (http)
                            {
                                List<AnnotationMetaData> temp = AnnotationUtils.getParamAnnotations(method);
                                boolean post = false;
                                for (AnnotationMetaData data : temp)
                                {
                                    if (data.getAnnotation().annotationType().equals(Payload.class) ||
                                            data.getAnnotation().annotationType().equals(PayloadParam.class))
                                    {
                                        post = true;
                                        break;
                                    }
                                }
                                //By default Mule will post if no method is set
                                if (!post && binding.getEndpoint().getProperties().get(HttpConnector.HTTP_METHOD_PROPERTY) == null)
                                {
                                    binding.getEndpoint().getProperties().put(HttpConnector.HTTP_METHOD_PROPERTY, "GET");
                                }
                            }

                            binding.setInterface(getInterface());
                            binding.setMethod(metaData.getMember().toString());
                            handler.addRouter(binding);

                        }
                    }
                }
                else if (metaData.getAnnotation().annotationType().equals(Template.class))
                {
                    evals.put(metaData.getMember().toString(), ((Template) metaData.getAnnotation()).value());
                }
            }
//            if (handler == null)
//            {
//                if (evals.size() > 0)
//                {
//                    handler = new TemplateAnnotationHandler(muleContext, getInterface());
//                    ((TemplateAnnotationHandler) handler).setEvals(evals);
//                }
//                else
//                {
//                    throw new IllegalArgumentException("The Service proxy interface did not have any @Call or @Template annotations configured on it.");
//                }
//            }
            if (evals.size() > 0)
            {
                handler.getTemplateHandler().setEvals(evals);
            }

            Object proxy = Proxy.newProxyInstance(getInterface().getClassLoader(), new Class[]{getInterface()}, handler);
            if (logger.isDebugEnabled())
            {
                logger.debug("Have proxy?: " + (null != proxy));
            }
            return proxy;

        }
        catch (Exception e)
        {
            throw new MuleRuntimeException(CoreMessages.failedToCreateProxyFor(target), e);
        }
    }

    public void setEndpoint(ImmutableEndpoint e)
    {
        outboundRouter = new OutboundPassThroughRouter();
        outboundRouter.addEndpoint((OutboundEndpoint) e);
        outboundRouter.setTransactionConfig(e.getTransactionConfig());
    }

    public String toString()
    {
        final StringBuffer sb = new StringBuffer();
        sb.append("AnnotatedInterfaceBinding");
        sb.append(", interface=").append(interfaceClass);
        sb.append('}');
        return sb.toString();
    }

    public ImmutableEndpoint getEndpoint()
    {
        if (outboundRouter != null)
        {
            return (OutboundEndpoint) outboundRouter.getEndpoints().get(0);
        }
        else
        {
            return null;
        }
    }
}
