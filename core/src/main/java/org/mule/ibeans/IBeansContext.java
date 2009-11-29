/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans;

import org.mule.DefaultMuleEvent;
import org.mule.DefaultMuleMessage;
import org.mule.DefaultMuleSession;
import org.mule.api.DefaultMuleException;
import org.mule.api.MuleContext;
import org.mule.api.MuleEvent;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.api.config.MuleConfiguration;
import org.mule.api.context.notification.ServerNotificationListener;
import org.mule.api.endpoint.ImmutableEndpoint;
import org.mule.api.endpoint.InboundEndpoint;
import org.mule.api.endpoint.OutboundEndpoint;
import org.mule.api.transformer.DataType;
import org.mule.api.transformer.Transformer;
import org.mule.api.transformer.TransformerException;
import org.mule.api.transport.DispatchException;
import org.mule.api.transport.ReceiveException;
import org.mule.config.ExceptionHelper;
import org.mule.config.i18n.CoreMessages;
import org.mule.context.notification.CustomNotification;
import org.mule.context.notification.NotificationException;
import org.mule.ibeans.config.ChannelConfigBuilder;
import org.mule.ibeans.i18n.IBeansMessages;
import org.mule.ibeans.internal.client.AnnotatedInterfaceBinding;
import org.mule.transformer.types.DataTypeFactory;
import org.mule.transport.AbstractConnector;
import org.mule.transport.NullPayload;

import java.util.Collections;
import java.util.Map;

import edu.emory.mathcs.backport.java.util.concurrent.ConcurrentHashMap;
import edu.emory.mathcs.backport.java.util.concurrent.ConcurrentMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Defines the client API that can be used from Java, JSPs or TestCases (see {@link org.mule.ibeans.test.AbstractIBeansTestCase}).
 * For application development the Mule iBeans annotations are usually sufficient. This API does offer some additional
 * functionality such as on demand transforms using the {@link #transform(Object, Class)} method and the ability to publish
 * and subscribe notifications using {@link #publishNotification(org.mule.api.context.notification.ServerNotification)} and
 * {@link #registerNotificationListener(org.mule.api.context.notification.ServerNotificationListener)} respectively.  Notifications
 * can be used to register interest in Mule iBeans container events such as when messages are sent or received, and container
 * lifecycle events.
 */
public final class IBeansContext
{
    public static final String CONTEXT_PROPERTY = "mule.ibeans.context";
    /**
     * logger used by this class
     */
    protected static final Log logger = LogFactory.getLog(IBeansContext.class);

    /**
     * The local MuleContext instance.
     */
    private MuleContext muleContext;

    private ConfigManager configManager;

    /**
     * Endpoints are cached so that the same endpoint URI can be used multiple times without creating
     * a new endpoint each time, which can be expensive
     */
    private ConcurrentMap inboundEndpointCache = new ConcurrentHashMap();

    private ConcurrentMap outboundEndpointCache = new ConcurrentHashMap();

    /**
     * Creates a Mule iBeans client. This constuctor can only be accessed by Mule itself an IntegrationBeans instance
     * will be made availble to the developer through dependency injection and the ServletContext.
     *
     * @throws org.mule.api.MuleException
     */
    IBeansContext(MuleContext context) throws MuleException
    {
        init(context);
    }

    /**
     * Initialises a default {@link MuleContext} for use by the client.
     *
     * @param startManager start the Mule context if it has not yet been initialised
     * @throws MuleException
     */
    private void init(MuleContext context) throws MuleException
    {
        // if we are creating a server for this client then set client mode
        // this will disable Admin connections by default;
        // If there is no local muleContext present create a default muleContext
        muleContext = context;

        if (muleContext == null)
        {
            throw new DefaultMuleException(CoreMessages.objectIsNull("muleContext"));
        }

        configManager = new ConfigManager(muleContext);
    }

    /**
     * Throws the root exception of the current message if a message occured
     *
     * @param message
     * @throws MuleException
     */
    protected void exceptionCheck(MuleMessage message) throws MuleException
    {
        if (message != null && message.getExceptionPayload() != null)
        {
            throw ExceptionHelper.getRootMuleException(message.getExceptionPayload().getException());
        }
    }

    /**
     * publishes a message asynchronously over a channel via the local Mule iBeans instance. The URI
     * determines where to dispatch the message.
     *
     * @param uriOrRef the URI used to determine the channel of the
     *                 message or a reference to an endpoint name.
     * @param payload  the object that is the payload of the message
     * @throws org.mule.api.MuleException if the message cannot be sent
     */
    public void send(String uriOrRef, Object payload) throws MuleException
    {
        send(uriOrRef, new DefaultMuleMessage(payload, Collections.EMPTY_MAP, muleContext));
    }

    /**
     * Sends a message asynchronously over a channel via the local Mule iBeans instance. The URI
     * determines where to dispatch the message.
     *
     * @param uriOrRef   the URI used to determine the channel for the
     *                   message or a reference to an endpoint name
     * @param payload    the object that is the payload of the event
     * @param properties any properties to be associated with the message. In
     *                   the case of JMS you could set headers such as JMSReplyTo property in these
     *                   properties.
     * @throws org.mule.api.MuleException if the message cannot be sent
     */
    public void send(String uriOrRef, Object payload, Map properties) throws MuleException
    {
        send(uriOrRef, new DefaultMuleMessage(payload, properties, muleContext));
    }

    /**
     * Sends a message asynchronously over a channel via the local Mule iBeans instance. The URI
     * determines where to dispatch the message.
     *
     * @param uriOrRef the URI used to determine the channel for the
     *                 message or a reference to an endpoint name
     * @param message  the message to send
     * @throws org.mule.api.MuleException
     */
    private void send(String uriOrRef, MuleMessage message) throws MuleException
    {
        MuleEvent event = getEvent(message, uriOrRef, false);
        try
        {
            event.getSession().dispatchEvent(event);
        }
        catch (MuleException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            throw new DispatchException(IBeansMessages.failedToSendMessageUsingUri(uriOrRef), event.getMessage(),
                    event.getEndpoint(), e);
        }
    }

    /**
     * Makes a request/response call to a remote service.  Typically this will be to a REST or Web Service can can be to
     * any service that returns a response. The call is synchronous which means it will block while waiting for a response.
     *
     * @param uriOrRef   The URI to make the request to or a reference to an endpoint name. This can any valid URI for modules deployed in the Mule iBeans container.
     *                   See the iBeans documentation or on a running Mule iBeans instance go to http://<host>:8080/ibeans.
     * @param payload    the request payload to send
     * @param properties any properties to associate with the request.  These are usually headers associated with the underlying
     *                   transport sucvh as HTTP headers or JMS properties.
     * @return the response message. Note there s no type conversion here, use {@link #request(String, Class, Object, java.util.Map)}
     *         to specify a return type and have Mule iBeans automatically transform the response.
     * @throws MuleException If Mule iBeans cannot connect to the service or a error is returned from the service.
     */
    public Object request(String uriOrRef, Object payload, Map properties) throws MuleException
    {
        return request(uriOrRef, Object.class, payload, properties);
    }

    /**
     * Makes a request/response call to a remote service.  Typically this will be to a REST or Web Service can can be to
     * any service that returns a response. The call is synchronous which means it will block while waiting for a response.
     *
     * @param uriOrRef The URI to make the request to or a reference to an endpoint name. This can any valid URI for modules deployed in the Mule iBeans container.
     *                 See the iBeans documentation or on a running Mule iBeans instance go to http://<host>:8080/ibeans.
     * @param payload  the request payload to send
     * @return the response message. Note there s no type conversion here, use {@link #request(String, Class, Object)}
     *         to specify a return type and have Mule iBeans automatically transform the response.
     * @throws MuleException If Mule iBeans cannot connect to the service or a error is returned from the service.
     */
    public Object request(String uriOrRef, Object payload) throws MuleException
    {
        return request(uriOrRef, Object.class, payload);
    }

    /**
     * Makes a request/response call to a remote service.  Typically this will be to a REST or Web Service can can be to
     * any service that returns a response. The call is synchronous which means it will block while waiting for a response.
     *
     * @param uriOrRef   The URI to make the request to or a reference to an endpoint name. This can any valid URI for modules deployed in the Mule iBeans container.
     *                   See the iBeans documentation or on a running Mule iBeans instance go to http://<host>:8080/ibeans.
     * @param returnType The returntype to transform the response message. Mule iBeans provides an auto-transformation feature
     *                   that will automatically perform basic transformations. For example, if the result is an XML string you
     *                   can specify the returnType to be a Document object.  To see what transformations are available inside
     *                   the current iBeans container go to - http://<host>:8080/ibeans/transformers. You can add your own
     *                   transformers to the container. See the {@link org.mule.ibeans.api.application.Transformer} annotation
     *                   documentation for more information.
     * @param payload    the request payload to send
     * @return the response message converted to the type defined by the 'returnType' parameter.
     * @throws MuleException        If Mule iBeans cannot connect to the service or a error is returned from the service
     * @throws TransformerException if the response cannot be transformed to the specified return type.
     */
    public <T extends Object> T request(String uriOrRef, Class<T> returnType, Object payload) throws MuleException
    {
        MuleMessage message = request(uriOrRef, new DefaultMuleMessage(payload, Collections.EMPTY_MAP, muleContext));
        exceptionCheck(message);
        //A request can return null
        if (message == null)
        {
            return null;
        }
        if (returnType == null)
        {
            return (T) message.getPayload();
        }
        else if (MuleMessage.class.isAssignableFrom(returnType))
        {
            return (T) message;
        }
        else
        {
            return message.getPayload(returnType);
        }
    }

    /**
     * Makes a request/response call to a remote service.  Typically this will be to a REST or Web Service can can be to
     * any service that returns a response. The call is synchronous which means it will block while waiting for a response.
     *
     * @param uriOrRef   The URI to make the request to or a reference to an endpoint name. This can any valid URI for modules deployed in the Mule iBeans container.
     *                   See the iBeans documentation or on a running Mule iBeans instance go to http://<host>:8080/ibeans.
     * @param returnType The returntype to transform the response message. Mule iBeans provides an auto-transformation feature
     *                   that will automatically perform basic transformations. For example, if the result is an XML string you
     *                   can specify the returnType to be a Document object.  To see what transformations are available inside
     *                   the current iBeans container go to - http://<host>:8080/ibeans/transformers. You can add your own
     *                   transformers to the container. See the {@link org.mule.ibeans.api.application.Transformer} annotation
     *                   documentation for more information.
     * @param payload    the request payload to send
     * @param properties any properties to associate with the request.  These are usually headers associated with the underlying
     *                   transport sucvh as HTTP headers or JMS properties.
     * @return the response message converted to the type defined by the 'returnType' parameter.
     * @throws MuleException        If Mule iBeans cannot connect to the service or a error is returned from the service
     * @throws TransformerException if the response cannot be transformed to the specified return type.
     */

    public <T extends Object> T request(String uriOrRef, Class<T> returnType, Object payload, Map properties) throws MuleException
    {
        MuleMessage message = request(uriOrRef, new DefaultMuleMessage(payload, properties, muleContext));
        exceptionCheck(message);
        if (returnType == null)
        {
            return (T) message.getPayload();
        }
        else if (MuleMessage.class.isAssignableFrom(returnType))
        {
            return (T) message;
        }
        else
        {
            return message.getPayload(returnType);
        }
    }


    /**
     * Sends an event synchronously to a endpointUri via a Mule server and a
     * resulting message is returned.
     *
     * @param uriOrRef the URL used to determine the destination and transport of the
     *                 message or a reference to an endpoint name.
     * @param message  the Message for the event
     * @return A return message, this could be <code>null</code> if the the components invoked
     *         explicitly sets a return as <code>null</code>.
     * @throws org.mule.api.MuleException
     */
    private MuleMessage request(String uriOrRef, MuleMessage message) throws MuleException
    {
        MuleEvent event = getEvent(message, uriOrRef, true);
        event.setTimeout(getConfiguration().getDefaultResponseTimeout());

        try
        {
            MuleMessage msg = event.getSession().sendEvent(event);
            if (msg == null || message.getPayload() instanceof NullPayload)
            {
                msg = null;
            }
            return msg;
        }
        catch (MuleException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            throw new DispatchException(IBeansMessages.failedToSendMessageUsingUri(uriOrRef), event.getMessage(),
                    event.getEndpoint(), e);
        }
    }


    /**
     * Makes a single polling call to a remote resource.  Typically, this will be to a resource or store such as File, FTP, JMS queue, etc
     *
     * @param uriOrRef   The URI to make the request to or a reference to an endpoint name. This can any valid URI for modules deployed in the Mule iBeans container.
     *                   See the iBeans documentation or on a running Mule iBeans instance go to http://<host>:8080/ibeans.
     * @param returnType The returntype to transform the response message. Mule iBeans provides an auto-transformation feature
     *                   that will automatically perform basic transformations. For example, if the result is an XML string you
     *                   can specify the returnType to be a Document object.  To see what transformations are available inside
     *                   the current iBeans container go to - http://<host>:8080/ibeans/transformers. You can add your own
     *                   transformers to the container. See the {@link org.mule.ibeans.api.application.Transformer} annotation
     *                   documentation for more information.
     * @return the response message converted to the type defined by the 'returnType' parameter.
     * @throws MuleException        If Mule iBeans cannot connect to the service/resource or a error is returned from from the remote service/resource
     * @throws TransformerException if the response cannot be transformed to the specified return type.
     */
    public <T extends Object> T receive(String uriOrRef, Class<T> returnType) throws MuleException
    {
        return receive(uriOrRef, returnType, getConfiguration().getDefaultResponseTimeout());
    }

    /**
     * Makes a single request call to a remote resource.  Typically, this will be to a resource or store such as File, FTP, JMS queue, etc
     *
     * @param uriOrRef   The URI to make the request to or a reference to an endpoint name. This can any valid URI for modules deployed in the Mule iBeans container.
     *                   See the iBeans documentation or on a running Mule iBeans instance go to http://<host>:8080/ibeans.
     * @param returnType The returntype to transform the response message. Mule iBeans provides an auto-transformation feature
     *                   that will automatically perform basic transformations. For example, if the result is an XML string you
     *                   can specify the returnType to be a Document object.  To see what transformations are available inside
     *                   the current iBeans container go to - http://<host>:8080/ibeans/transformers. You can add your own
     *                   transformers to the container. See the {@link org.mule.ibeans.api.application.Transformer} annotation
     *                   documentation for more information.
     * @param timeout    how long to block waiting to receive a response to the request, if set to 0 the
     *                   receive will not wait at all and if set to -1 the receive will wait
     *                   forever
     * @return the response message converted to the type defined by the 'returnType' parameter.
     * @throws MuleException        If Mule iBeans cannot connect to the service/resource or a error is returned from from the remote service/resource
     * @throws TransformerException if the response cannot be transformed to the specified return type.
     */
    public <T extends Object> T receive(String uriOrRef, Class<T> returnType, int timeout) throws MuleException
    {
        InboundEndpoint endpoint = getInboundEndpoint(uriOrRef);
        try
        {
            MuleMessage message = endpoint.request(timeout);
            exceptionCheck(message);
            if (message == null || message.getPayload() instanceof NullPayload)
            {
                return null;
            }

            if (returnType == null)
            {
                return (T) message.getPayload();
            }
            else if (MuleMessage.class.isAssignableFrom(returnType))
            {
                return (T) message;
            }
            else
            {
                return message.getPayload(returnType);
            }
        }
        catch (MuleException e)
        {
            throw e;
        }
        catch (Exception ex)
        {
            throw new ReceiveException(endpoint, timeout, ex);
        }
    }


    /**
     * Packages a mule event for the current request
     *
     * @param message     the event payload
     * @param uri         the destination endpointUri
     * @param synchronous whether the event will be synchronously processed
     * @return A new MuleEvent instance
     * @throws MuleException if an error occurs while creating the event
     */
    protected MuleEvent getEvent(MuleMessage message, String uri, boolean synchronous)
            throws MuleException
    {
        ImmutableEndpoint endpoint = getOutboundEndpoint(uri);
        if (!endpoint.getConnector().isStarted() && muleContext.isStarted())
        {
            endpoint.getConnector().start();
        }
        try
        {
            DefaultMuleSession session = new DefaultMuleSession(message,
                    ((AbstractConnector) endpoint.getConnector()).getSessionHandler(), muleContext);

//            if (user != null)
//            {
//                message.setProperty(MuleProperties.MULE_USER_PROPERTY, MuleCredentials.createHeader(
//                    user.getUsername(), user.getPassword()));
//            }
            DefaultMuleEvent event = new DefaultMuleEvent(message, endpoint, session, synchronous);
            return event;
        }
        catch (Exception e)
        {
            throw new DispatchException(CoreMessages.failedToCreate("Client event"), message, endpoint, e);
        }
    }

    /**
     * Creates an inbound endpoint for the URI string.  Note that these endpoints are cached so the same URI will always
     * return the same endpoint instance.
     *
     * @param uri the inbound (subscription) URI to create an inbound endpoint for.
     * @return a new {@link org.mule.api.endpoint.InboundEndpoint} instance or a cached one if available.
     * @throws MuleException if Mule cannot create the endpoint. USually this will be becase a URI scheme is used that requires
     *                       a module that is not deployed to the container.
     */
    protected InboundEndpoint getInboundEndpoint(String uri) throws MuleException
    {
        // There was a potential leak here between get() and putIfAbsent(). This
        // would cause the endpoint that was created to be used rather an endpoint
        // with the same key that has been created and put in the cache by another
        // thread. To avoid this we test for the result of putIfAbsent result and if
        // it is non-null then an endpoint was created and added concurrently and we
        // return this instance instead.
        InboundEndpoint endpoint = (InboundEndpoint) inboundEndpointCache.get(uri);
        if (endpoint == null)
        {
            Object temp = muleContext.getRegistry().lookupObject(uri + ".builder");
            if (temp != null && temp instanceof ChannelConfigBuilder)
            {
                endpoint = ((ChannelConfigBuilder) temp).buildReceiveAndReplyChannel();
            }
            else
            {
                endpoint = muleContext.getRegistry().lookupEndpointFactory().getInboundEndpoint(uri);
            }
            InboundEndpoint concurrentlyAddedEndpoint = (InboundEndpoint) inboundEndpointCache.putIfAbsent(uri, endpoint);
            if (concurrentlyAddedEndpoint != null)
            {
                return concurrentlyAddedEndpoint;
            }
        }
        return endpoint;
    }

    /**
     * Creates an outbound endpoint for the URI string.  Note that these endpoints are cached so the same URI will always
     * return the same endpoint instance.
     *
     * @param uri the inbound (subscription) URI to create an inbound endpoint for.
     * @return a new {@link org.mule.api.endpoint.OutboundEndpoint} instance or a cached one if available.
     * @throws MuleException if Mule cannot create the endpoint. USually this will be becase a URI scheme is used that requires
     *                       a module that is not deployed to the container.
     */
    protected OutboundEndpoint getOutboundEndpoint(String uri) throws MuleException
    {
        // There was a potential leak here between get() and putIfAbsent(). This
        // would cause the endpoint that was created to be used rather an endpoint
        // with the same key that has been created and put in the cache by another
        // thread. To avoid this we test for the result of putIfAbsent result and if
        // it is non-null then an endpoint was created and added concurrently and we
        // return this instance instead.
        OutboundEndpoint endpoint = (OutboundEndpoint) outboundEndpointCache.get(uri);
        if (endpoint == null)
        {
            Object temp = muleContext.getRegistry().lookupObject(uri + ".builder");
            if (temp != null && temp instanceof ChannelConfigBuilder)
            {
                endpoint = ((ChannelConfigBuilder) temp).buildSendChannel();
            }
            else
            {
                endpoint = muleContext.getRegistry().lookupEndpointFactory().getOutboundEndpoint(uri);
            }

            OutboundEndpoint concurrentlyAddedEndpoint = (OutboundEndpoint) outboundEndpointCache.putIfAbsent(uri, endpoint);
            if (concurrentlyAddedEndpoint != null)
            {
                return concurrentlyAddedEndpoint;
            }
        }
        return endpoint;
    }

    public <T> T createIBean(Class<T> clazz)
    {
        AnnotatedInterfaceBinding router = new AnnotatedInterfaceBinding();
        router.setMuleContext(muleContext);
        router.setInterface(clazz);
        return (T) router.createProxy(new Object());
    }

    /**
     * The overriding method may want to return a custom {@link MuleContext} here
     *
     * @return the MuleContext to use
     */
    MuleContext getMuleContext()
    {
        return muleContext;
    }

    /**
     * Registers an integration bean with the Mule iBeans container. An integration bean is just a JavaBean with any of
     * the Mule iBeans annotations used for publishing, subscribing, polling or requesting messages.
     *
     * @param name The identifying name for the bean. This can be used to later unregister it.
     * @param bean any java object with one or more Mule iBeans annotations
     * @see org.mule.ibeans.api.application.Receive
     * @see org.mule.ibeans.api.application.Send
     * @see org.mule.ibeans.api.application.ReceiveAndReply
     * @see org.mule.ibeans.api.application.Schedule
     */
    public void registerApplicationIBean(String name, Object bean)
            throws MuleException
    {
        getMuleContext().getRegistry().registerObject(name, bean);
    }

    /**
     * Unregisters a previously registered iBean.
     *
     * @param name the name of the bean to unregister
     * @throws MuleException if unregistering the bean fails, i.e. The
     *                       underlying transport fails to unregister a listener. If the
     *                       components does not exist, this method should not throw an
     *                       exception.
     */
    public void unregisterApplicationIBean(String name) throws MuleException
    {
        getMuleContext().getRegistry().unregisterObject(name);
        //remove the service object too
        getMuleContext().getRegistry().unregisterObject(name + ".service");
    }

    /**
     * Retuens the MuleContext configuration object
     *
     * @return the MuleContext configuration object
     */
    protected MuleConfiguration getConfiguration()
    {
        return muleContext.getConfiguration();
    }

    /**
     * Mule iBeans has an internal notification mechanism that allows application to receive notifications from the
     * container about events like the container starting or a message being received. Developers can also publish their
     * own custom notifications to signify events in their application. These notifications can be used by monitoring tools
     * or other applications.
     *
     * @param n the notification object to send.  Note that custom notifications must extend {@link org.mule.context.notification.CustomNotification}
     *          Any attempt to fire other predefined notification types will result in a runtime error
     */
    public void fireNotification(CustomNotification n)
    {
        getMuleContext().fireNotification(n);
    }

    /**
     * Mule iBeans has an internal notification mechanism that allows application to receive notifications from the
     * container about events like the container starting or a message being received.
     *
     * @param l the notification listener that will be used to receive notifications.
     */
    public void registerNotificationListener(ServerNotificationListener l) throws NotificationException
    {
        getMuleContext().registerListener(l);
    }

    /**
     * Mule iBeans has an internal notification mechanism that allows application to receive notifications from the
     * container about events like the container starting or a message being received.
     *
     * @param l               the notification listener that will be used to receive notifications.
     * @param subscriptionKey A key used to define interest in specific notifications, for example if you want to receive
     *                        notifications about messages received by a specific iBean you can specify the subsriptionKey as the name of the iBean.
     *                        Note that wildcard subscriptions can be used, so to recieve notifications for all iBeans with 'public' in the name,
     *                        use a subscription key '*public*'.
     */
    public void registerNotificationListener(ServerNotificationListener l, String subscriptionKey) throws NotificationException
    {
        getMuleContext().registerListener(l, subscriptionKey);
    }

    /**
     * Performs an auto-transformation on the 'source' object. Mule iBeans provides an auto-transformation feature
     * that will automatically perform basic transformations. For example, if the result is an XML string you
     * can specify the returnType to be a Document object.  To see what transformations are available inside
     * the current iBeans container go to - http://<host>:8080/ibeans/transformers. You can add your own
     * transformers to the container. See the {@link org.mule.ibeans.api.application.Transformer} annotation
     * documentation for more information.
     *
     * @param source     the object to transform
     * @param resultType the return type for the transformed object
     * @return the transformed object
     * @throws TransformerException if there is no transformer found to convert from the source to the returnType, or if
     *                              the transform fails.
     */
    public <T> T transform(Object source, Class<T> resultType) throws TransformerException
    {
//        Class srcType = source.getClass();
//        if (source instanceof MuleMessage)
//        {
//            if (resultType.equals(MuleMessage.class))
//            {
//                return (T) source;
//            }
//            srcType = ((MuleMessage) source).getPayload().getClass();
//        }
//
//        if (resultType.isAssignableFrom(srcType))
//        {
//            if (srcType.equals(source.getClass()))
//            {
//                return (T) source;
//            }
//            else
//            {
//                return (T) ((MuleMessage) source).getPayload();
//            }
//        }
        return (T)transform(source, new DataTypeFactory().create(resultType));
    }
    /**
     * Performs an auto-transformation on the 'source' object. Mule iBeans provides an auto-transformation feature
     * that will automatically perform basic transformations. For example, if the result is an XML string you
     * can specify the returnType to be a Document object.  To see what transformations are available inside
     * the current iBeans container go to - http://<host>:8080/ibeans/transformers. You can add your own
     * transformers to the container. See the {@link org.mule.ibeans.api.application.Transformer} annotation
     * documentation for more information.
     *
     * @param source     the object to transform
     * @param result the return type for the transformed object
     * @return the transformed object
     * @throws TransformerException if there is no transformer found to convert from the source to the returnType, or if
     *                              the transform fails.
     */
    public <T>T transform(Object source, DataType<T> result) throws TransformerException
    {
        DataType sourceType = new DataTypeFactory().createFromObject(source);

        Class sourceClass = source.getClass();
        if (source instanceof MuleMessage)
        {
            if (result.getType().equals(MuleMessage.class))
            {
                return (T) source;
            }
            sourceClass = ((MuleMessage) source).getPayload().getClass();
        }

        if (result.isCompatibleWith(sourceType))
        {
            if (sourceClass.equals(source.getClass()))
            {
                return (T) source;
            }
            else
            {
                return (T) ((MuleMessage) source).getPayload();
            }
        }

        Transformer transformer = getMuleContext().getRegistry().lookupTransformer(sourceType, result);
        return (T)transformer.transform(source);
    }

    /**
     * Provides access to the {@link org.mule.ibeans.ConfigManager} where users can access objects in the repository, the
     * scope of the repository will be differnet depending on where IBeans is running.  for example, if running in Tomcat
     * all Tomcat JNDI resources are available via the ConfigManager.
     *
     * @return a reference to the config Manager.
     */
    public ConfigManager getConfig()
    {
        return configManager;
    }

    public <T> T eval(String expression, Object data, Class<T> returnType) throws TransformerException
    {
        Object o = muleContext.getExpressionManager().parse(expression, new DefaultMuleMessage(data, muleContext));
        return transform(o, returnType);
    }

    public Object eval(String expression, Object data) throws TransformerException
    {
        return muleContext.getExpressionManager().parse(expression, new DefaultMuleMessage(data, muleContext));
    }

}
