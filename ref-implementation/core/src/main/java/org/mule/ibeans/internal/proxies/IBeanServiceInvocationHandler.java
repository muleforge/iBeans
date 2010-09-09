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

import org.mule.DefaultMuleEvent;
import org.mule.DefaultMuleMessage;
import org.mule.RequestContext;
import org.mule.api.MuleEvent;
import org.mule.api.MuleMessage;
import org.mule.api.config.MuleProperties;
import org.mule.api.service.Service;
import org.mule.api.transport.PropertyScope;
import org.mule.transport.NullPayload;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Java proxy support.
 *
 * @deprecated This class is not used.  CGLib is preferred, this may get deleted before 1.0
 */
public class IBeanServiceInvocationHandler implements InvocationHandler
{
    private Service service;
    private Object theBean;

    public IBeanServiceInvocationHandler(Service service, Object bean)
    {
        this.service = service;
        theBean = bean;
    }

    public Object invoke(Object o, Method method, Object[] args) throws Throwable
    {
        if (method.getAnnotations().length == 0)
        {
            return method.invoke(theBean, args);
        }

        MuleMessage message = createMuleMessage(args);

        // Some transports such as Axis, RMI and EJB can use the method information
        message.setProperty(MuleProperties.MULE_METHOD_PROPERTY, method.getName(), PropertyScope.INVOCATION);

        MuleEvent currentEvent = RequestContext.getEvent();
        MuleEvent reply = service.getOutboundMessageProcessor().process(new DefaultMuleEvent(message, currentEvent));

            if (reply.getMessage().getExceptionPayload() != null)
            {
                throw findDeclaredMethodException(method, reply.getMessage().getExceptionPayload().getException());
            }
            else
            {
                return determineReply(reply.getMessage(), method);
            }

    }

    private MuleMessage createMuleMessage(Object[] args)
    {
        if (args == null)
        {
            return new DefaultMuleMessage(NullPayload.getInstance(), service.getMuleContext());
        }
        else if (args.length == 1)
        {
            return new DefaultMuleMessage(args[0], service.getMuleContext());
        }
        else
        {
            return new DefaultMuleMessage(args, service.getMuleContext());
        }
    }

    /**
     * Return the causing exception instead of the general "container" exception (typically
     * UndeclaredThrowableException) if the cause is known and the type matches one of the
     * exceptions declared in the given method's "throws" clause.
     */
    private Throwable findDeclaredMethodException(Method method, Throwable throwable) throws Throwable
    {
        Throwable cause = throwable.getCause();
        if (cause != null)
        {
            // Try to find a matching exception type from the method's "throws" clause, and if so
            // return that exception.
            Class[] exceptions = method.getExceptionTypes();
            for (int i = 0; i < exceptions.length; i++)
            {
                if (cause.getClass().equals(exceptions[i]))
                {
                    return cause;
                }
            }
        }

        return throwable;
    }

    private Object determineReply(MuleMessage reply, Method bindingMethod)
    {
        if (MuleMessage.class.isAssignableFrom(bindingMethod.getReturnType()))
        {
            return reply;
        }
        else
        {
            return reply.getPayload();
        }
    }
}
