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

import java.util.Map;

import org.ibeans.api.Request;
import org.ibeans.api.Response;

/**
 * TODO
 */

public interface ExpressionParser<R extends Request, S extends Response>
{
    String parsePropertyPlaceholders(Map properties, String value);

    boolean hasPropertyPlaceholders(String value);

    String parseUriTokens(Map properties, String uri);

    boolean hasUriTokens(String value);
    
    Object evaluate(String expression, R request);

    Object evaluate(String expression, S response);

    Object evaluate(String evaluator, String expression, S response);

}
