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

import org.mule.api.MuleContext;
import org.mule.api.MuleMessage;
import org.mule.ibeans.api.client.params.InvocationContext;
import org.mule.ibeans.internal.ext.DynamicOutboundEndpoint;
import org.mule.ibeans.internal.util.UriParamFilter;
import org.mule.util.TemplateParser;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Used to Handle {@link org.mule.ibeans.api.client.Template} annotated method calls.
 */
public class TemplateAnnotationHandler implements ClientAnnotationHandler
{
    private Map<String, String> evals = new HashMap<String, String>();

    private MuleContext context;

    //The parser used to parse the uriTemplate
    protected TemplateParser parser = TemplateParser.createCurlyBracesStyleParser();

    private UriParamFilter filter = new UriParamFilter();

    public TemplateAnnotationHandler(MuleContext context)
    {
        this.context = context;
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

    public MuleMessage invoke(InvocationContext ctx, MuleMessage message) throws Exception
    {

        String eval = evals.get(ctx.getMethod().toString());
        if (eval == null)
        {
            return null;
        }

        //If there is no template, just return the current message.  This assumes the bean just wants to perform a transform
        if (eval.length() == 0)
        {
            return message;
        }
        Map<String, Object> props = getPropertiesForTemplate(message);

        eval = parser.parse(props, eval);
        //Remove optional params completely if null
        eval = filter.filterParamsByValue(eval, DynamicOutboundEndpoint.NULL_PARAM);

        Object result = context.getExpressionManager().parse(eval, message);

        message.setPayload(result);
        return message;

    }

    protected Map<String, Object> getPropertiesForTemplate(MuleMessage message)
    {
        Map<String, Object> props = (Map) message.removeProperty(CallOutboundEndpoint.URI_PARAM_PROPERTIES);
        if (props == null)
        {
            throw new IllegalStateException(CallOutboundEndpoint.URI_PARAM_PROPERTIES + " not set on message");
        }
        return props;
    }

    public String getScheme(Method method)
    {
        return "template";
    }
}
