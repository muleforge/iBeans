/*
 * $Id$
 * -------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.ibeans.impl.support;

import java.util.Map;

import org.ibeans.api.Request;
import org.ibeans.api.Response;
import org.ibeans.impl.support.util.TemplateParser;
import org.ibeans.spi.ExpressionParser;

/**
 * TODO
 */
public abstract class AbstractExpressionParser<R extends Request, S extends Response> implements ExpressionParser<R, S>
{
    private TemplateParser placeholderParser = TemplateParser.createAntStyleParser();
    private TemplateParser uriTokenParser = TemplateParser.createCurlyBracesStyleParser();

    public String parsePropertyPlaceholders(Map properties, String value)
    {
        return placeholderParser.parse(properties, value);
    }

    public String parseUriTokens(Map properties, String uri)
    {
        return uriTokenParser.parse(properties, uri);
    }

    public boolean hasPropertyPlaceholders(String value)
    {
        return placeholderParser.isContainsTemplate(value);
    }

    public boolean hasUriTokens(String value)
    {
        return uriTokenParser.isContainsTemplate(value);
    }
}
