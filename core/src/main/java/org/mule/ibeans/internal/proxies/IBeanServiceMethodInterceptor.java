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
import org.mule.module.annotationx.api.Send;
import org.mule.transport.NullPayload;

import java.lang.reflect.Method;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

/**
 * A method interceptor for a CGLib proxy that will process annotations on that method.
 * Currently only {@link org.mule.ibeans.api.application.Send} annotation is supported.  If the method has
 * a send annotation the result of the method call will be dispatched over the send channel URI
 */
public class IBeanServiceMethodInterceptor implements MethodInterceptor
{
    private Service service;

    public IBeanServiceMethodInterceptor(Service service)
    {
        this.service = service;
    }

    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable
    {
        Method realMethod = obj.getClass().getSuperclass().getMethod(method.getName(), method.getParameterTypes());

        Object result = proxy.invokeSuper(obj, args);

        //Check for @Send annotation only i.e. an outbound endpoint
        if (!realMethod.isAnnotationPresent(Send.class))
        {
            return result;
        }

        MuleMessage message = createMuleMessage(result);

        // This is used to select the outbound endpoint against the current method
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

    private MuleMessage createMuleMessage(Object arg)
    {
        if (arg == null)
        {
            return new DefaultMuleMessage(NullPayload.getInstance(), service.getMuleContext());
        }
        else
        {
            return new DefaultMuleMessage(arg, service.getMuleContext());
        }
    }

    /**
     * Return the causing exception instead of the general "container" exception (typically
     * UndeclaredThrowableException) if the cause is known and the type matches one of the
     * exceptions declared in the given method's "throws" clause.
     *
     * @param method the method that was invoked that caused the exception
     * @param throwable the actual exception thrown, this may be a wrapped exception
     * @return THe correct, unwrapped exception that should be thrown
     *
     */
    private Throwable findDeclaredMethodException(Method method, Throwable throwable)
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
