/*
 * $Id: TemplateAnnotationHandler.java 290 2010-02-15 09:54:41Z ross $
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.ibeans.impl;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.activation.DataHandler;

import org.ibeans.api.ClientAnnotationHandler;
import org.ibeans.api.IBeansException;
import org.ibeans.api.InvocationContext;
import org.ibeans.api.Request;
import org.ibeans.api.Response;
import org.ibeans.api.channel.CHANNEL;
import org.ibeans.impl.support.util.TemplateParser;
import org.ibeans.impl.support.util.UriParamFilter;
import org.ibeans.spi.IBeansPlugin;

/**
 * Used to Handle {@link org.ibeans.annotation.Template} annotated method calls.
 */
public class TemplateAnnotationHandler implements ClientAnnotationHandler
{
    private Map<String, String> evals = new HashMap<String, String>();

    //The parser used to parse the uriTemplate
    protected TemplateParser parser = TemplateParser.createCurlyBracesStyleParser();

    private UriParamFilter filter = new UriParamFilter();

    private IBeansPlugin plugin;

    public TemplateAnnotationHandler(IBeansPlugin plugin)
    {
        this.plugin = plugin;
    }

    public boolean isMatch(Method method)
    {
        return evals.get(method.toString()) != null;
    }

    public Map<String, String> getEvals()
    {
        return evals;
    }

    public void setEvals(Map<String, String> evals)
    {
        this.evals = evals;
    }

    public Response invoke(InvocationContext ctx) throws Exception
    {
        String eval = evals.get(ctx.getMethod().toString());
        if (eval == null)
        {
            return null;
        }

        //If there is no template, just return the current message.  This assumes the bean just wants to
        //perform a transform
        if (eval.length() == 0)
        {
            return createResponse(ctx.getRequest().getPayload(), ctx.getRequest());
        }
        
        Map<String, Object> props = getPropertiesForTemplate(ctx.getRequest());

        eval = parser.parse(props, eval);
        //Remove optional params completely if null
        eval = filter.filterParamsByValue(eval, CHANNEL.NULL_URI_PARAM);

        Object result = ctx.getExpressionParser().evaluate(eval, ctx.getRequest());
        return createResponse(result, ctx.getRequest());
    }

    protected Response createResponse(Object payload, Request request) throws IBeansException
    {
        //Since this is a @Template request, we can keep the headers. These are useful for debugging
        Map<String, Object> headers = new HashMap<String, Object>();
        for (String s : request.getHeaderNames())
        {
            headers.put(s, request.getHeader(s));
        }
        Map<String, DataHandler> attachments = new HashMap<String, DataHandler>();
        for (String s : request.getAttachmentNames())
        {
            attachments.put(s, request.getAttachment(s));
        }
        return plugin.createResponse(payload, headers, attachments);
    }

    protected Map<String, Object> getPropertiesForTemplate(Request message)
    {
        Map<String, Object> props = (Map) message.removeHeader(CHANNEL.URI_PARAM_PROPERTIES);
        if (props == null)
        {
            throw new IllegalStateException(CHANNEL.URI_PARAM_PROPERTIES + " not set on message");
        }
        return props;
    }

    public String getScheme(Method method)
    {
        return "template";
    }
}
