/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.internal.parsers;

import org.mule.api.expression.ExpressionParser;
import org.mule.config.annotations.expressions.Evaluator;
import org.mule.expression.ExpressionConfig;
import org.mule.expression.MessageHeaderExpressionEvaluator;
import org.mule.expression.MessageHeadersExpressionEvaluator;
import org.mule.expression.MessageHeadersListExpressionEvaluator;
import org.mule.expression.transformers.ExpressionArgument;
import org.mule.ibeans.api.application.params.ReceivedHeaders;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;

/**
 * Responsible for parsing the {@link org.mule.ibeans.api.application.params.ReceivedHeaders} annotation.  This is an iBeans
 * framework class and cannot be used in any other context.
 */
public class ReceivedHeadersAnnotationParser implements ExpressionParser
{
    public ExpressionArgument parse(Annotation annotation, Class parameterType)
    {
        Evaluator evaluator = annotation.annotationType().getAnnotation(Evaluator.class);
        if (evaluator != null)
        {
            String eval = MessageHeaderExpressionEvaluator.NAME;
            if (parameterType.isAssignableFrom(Map.class))
            {
                eval = MessageHeadersExpressionEvaluator.NAME;
            }
            else if (parameterType.isAssignableFrom(List.class))
            {
                eval = MessageHeadersListExpressionEvaluator.NAME;
            }
            return new ExpressionArgument(null, new ExpressionConfig(((ReceivedHeaders) annotation).value(), eval, null), true, parameterType);
        }
        else
        {
            throw new IllegalArgumentException("The @Evaluator annotation must be set on an Expression Annotation");
        }

    }

    public boolean supports(Annotation annotation)
    {
        return annotation instanceof ReceivedHeaders;
    }
}