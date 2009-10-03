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

import org.mule.DefaultMuleMessage;
import org.mule.DefaultMuleSession;
import org.mule.NullSessionHandler;
import org.mule.api.MuleContext;
import org.mule.api.MuleMessage;
import org.mule.api.MuleSession;
import org.mule.api.endpoint.ImmutableEndpoint;
import org.mule.api.context.MuleContextAware;
import org.mule.api.routing.InterfaceBinding;
import org.mule.api.service.Service;
import org.mule.api.transport.PropertyScope;
import org.mule.config.i18n.CoreMessages;
import org.mule.message.DefaultExceptionPayload;
import org.mule.transport.NullPayload;
import org.mule.util.TemplateParser;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Used to Handle {@link org.mule.ibeans.api.client.Template} annotated method calls.
 */
public class CallAnnotationHandler implements ClientAnnotationHandler
{
    public static final String DEFAULT_METHOD_NAME_TOKEN = "default";

    protected static Log logger = LogFactory.getLog(CallAnnotationHandler.class);

    protected Service service;

    private MuleContext muleContext;

    protected IBeanParamsHelper helper;

    protected Map<String, InterfaceBinding> routers = new HashMap<String, InterfaceBinding>();

    //The parser used to parse the uriTemplate
    protected TemplateParser parser = TemplateParser.createCurlyBracesStyleParser();

    public CallAnnotationHandler(MuleContext muleContext, Service service, IBeanParamsHelper helper)
    {
        this.muleContext = muleContext;
        this.service = service;
        this.helper = helper;
    }

    public void addRouterForInterface(InterfaceBinding router)
    {
        if (router instanceof MuleContextAware)
        {
            ((MuleContextAware) router).setMuleContext(muleContext);
        }
        if (router.getMethod() == null)
        {
            if (routers.size() == 0)
            {
                routers.put(DEFAULT_METHOD_NAME_TOKEN, router);
            }
            else
            {
                throw new IllegalArgumentException(CoreMessages.mustSetMethodNamesOnBinding().getMessage());
            }
        }
        else
        {
            routers.put(router.getMethod(), router);
        }


    }

    public MuleMessage invoke(Object proxy, Method method, Object[] args, MuleMessage message) throws Exception
    {

        InterfaceBinding router = routers.get(method.toString());
        if (router == null)
        {
            throw new IllegalArgumentException(CoreMessages.cannotFindBindingForMethod(method.getName()).toString());
        }
        router.getEndpoint().getProperties().putAll(helper.getDefaultPropertyParams());

        message.addProperties(router.getEndpoint().getProperties(), PropertyScope.INVOCATION);


        MuleMessage reply;
        MuleSession session = new DefaultMuleSession(message, new NullSessionHandler(), service, muleContext);

        try
        {
            reply = router.route(message, session);
        }
        catch (Throwable e)
        {
            //Make all exceptions go through the CallException handler
            reply = new DefaultMuleMessage(NullPayload.getInstance(), muleContext);
            reply.setExceptionPayload(new DefaultExceptionPayload(e));
        }
        return reply;
    }

    public String getScheme(Method method)
    {
        InterfaceBinding router = routers.get(method.toString());
        if (router == null)
        {
            throw new IllegalArgumentException(CoreMessages.cannotFindBindingForMethod(method.getName()).toString());
        }
        return router.getEndpoint().getEndpointURI().getScheme();
    }

    ImmutableEndpoint getEndpointForMethod(Method method)
    {
        InterfaceBinding router = routers.get(method.toString());
        if (router != null)
        {
            return router.getEndpoint();
        }
        return null;
    }
}