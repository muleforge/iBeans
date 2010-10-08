/*
 * $Id$
 * -------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.ibeans.impl;

import java.beans.ExceptionListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ibeans.annotation.Invoke;
import org.ibeans.api.ClientAnnotationHandler;
import org.ibeans.api.InvocationContext;
import org.ibeans.api.Response;

/**
 * TODO
 */
public class DefaultIBeanInvoker<C extends ClientAnnotationHandler, T extends TemplateAnnotationHandler, I extends InvokeAnnotationHandler> implements org.ibeans.api.IBeanInvoker<C,T,I>
{
    /**
     * logger used by this class
     */
    protected transient final Log logger = LogFactory.getLog(DefaultIBeanInvoker.class);

    private C callHandler;
    private T templateHandler;
    private I invokeHandler;

    public DefaultIBeanInvoker(C callHandler, T templateHandler, I invokeHandler)
    {
        this.callHandler = callHandler;
        this.templateHandler = templateHandler;
        this.invokeHandler = invokeHandler;
    }

    public C getCallHandler()
    {
        return callHandler;
    }

    public T getTemplateHandler()
    {
        return templateHandler;
    }

    public I getInvokeHandler()
    {
        return invokeHandler;
    }

    public void intercept(InvocationContext invocationContext) throws Throwable
    {
        ExceptionListener exceptionListener = invocationContext.getExceptionListener();

        Response result;

        if(invocationContext.getMethod().isAnnotationPresent(Invoke.class))
        {
            result = invokeHandler.invoke(invocationContext);
        }
        //Can Template handler be simplified here? Is there a need to register evals on the handler?
        else if (templateHandler != null && templateHandler.isMatch(invocationContext.getMethod()))
        {
            result = templateHandler.invoke(invocationContext);
        }
        else
        {
            result = callHandler.invoke(invocationContext);
        }

        ((InternalInvocationContext)invocationContext).setResponse(result);

        if (result != null)
        {
            invocationContext.setResult(result.getPayload());
        }

        if (result != null)
        {
            if (result.getException() != null)
            {
                Throwable t = result.getException();
                if (exceptionListener != null)
                {
                    if (Exception.class.isAssignableFrom(t.getClass()))
                    {
                        exceptionListener.exceptionThrown((Exception) t);
                    }
                    else
                    {
                        exceptionListener.exceptionThrown(new Exception(t));
                    }
                }
                else
                {
                    t = ProcessErrorsInterceptor.createCallException(invocationContext, t);
                    throw t;
                }
            }
        }
    }

}