/*
 * $Id$
 * -------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.ibeans.spi;


import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.activation.DataHandler;

import org.ibeans.api.CallInterceptor;
import org.ibeans.api.IBeanInvocationData;
import org.ibeans.api.IBeanInvoker;
import org.ibeans.api.IBeansException;
import org.ibeans.api.Request;
import org.ibeans.api.Response;

/**
 * The entry point for frameworks that want to support iBeans in their code
 *
 * This interface allows frameworks to customise how the requests are made, the request and response message from iBeans
 * expression handling and error handling
 */
public interface IBeansPlugin<R extends Request, S extends Response>
{
    CallInterceptor getResponseTransformInterceptor() throws IBeansException;

    IBeanInvoker getIBeanInvoker() throws IBeansException;

    IBeanInvoker getMockIBeanInvoker(Object mock) throws IBeansException;

    void addInterceptors(LinkedList<CallInterceptor> interceptors);

    List<ErrorFilterFactory> getErrorFilterFactories();

    Map getProperties();

    ExpressionParser getExpressionParser();

    R createRequest(IBeanInvocationData data)throws IBeansException;

    S createResponse(Object payload, Map<String, Object> headers, Map<String, DataHandler> attachments)throws IBeansException;
}
