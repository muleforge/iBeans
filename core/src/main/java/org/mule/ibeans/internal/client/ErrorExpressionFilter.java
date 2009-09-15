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

import org.mule.routing.filters.ExpressionFilter;

/**
 * TODO
 */
public class ErrorExpressionFilter extends ExpressionFilter
{
    public ErrorExpressionFilter(String evaluator, String customEvaluator, String expression)
    {
        super(evaluator, customEvaluator, expression);
    }

    public ErrorExpressionFilter(String evaluator, String expression)
    {
        super(evaluator, expression);
    }

    public ErrorExpressionFilter(String expression)
    {
        super(expression);
    }

    public ErrorExpressionFilter()
    {
    }

    private String errorCodeExpr;

    public String getErrorCodeExpr()
    {
        return errorCodeExpr;
    }

    public void setErrorCodeExpr(String errorCodeExpr)
    {
        if (errorCodeExpr != null && errorCodeExpr.length() > 0)
        {
            this.errorCodeExpr = errorCodeExpr;
        }
    }
}
