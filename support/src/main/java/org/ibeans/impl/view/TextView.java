/*
 * $Id:  $
 * -------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.ibeans.impl.view;

import java.lang.reflect.Method;
import java.util.List;

import org.ibeans.annotation.AuthenticationMethod;
import org.ibeans.impl.support.annotation.AnnotationMetaData;

/**
 * Generates a text usage view of an IBean
 */
public class TextView extends TemplateView
{
    public static final String SPACER = "  ";

    @Override
    public String createView(Class ibean)
    {
        super.createView(ibean);

        StringBuffer buf = new StringBuffer();
        if (getUsage() != null)
        {
            buf.append(getUsage());
            buf.append("\n");
        }
        else
        {
            buf.append("No usage text available for iBean").append("\n");
        }
        buf.append("Short ID: ").append(getShortId()).append("\n");
        buf.append("Class: ").append(ibean).append("\n");
        buf.append("Authentication: ").append((isAuthentication() ? "Yes" : "No")).append("\n");

        String spacer = "  ";
        if (getAuthCalls().size() > 0)
        {
            buf.append("Authentication Method (Required: Should be called first):\n");

            for (AnnotationMetaData metaData : getAuthCalls())
            {
                buf.append(printMethod((Method) metaData.getMember()));
                buf.append(" [").append(((Method) metaData.getMember()).getAnnotation(AuthenticationMethod.class).value()).append(" Authentication]\n");
            }
        }
        if (stateCalls.size() > 0)
        {
            buf.append("State Methods (Should be called first, may be optional):\n");
            for (AnnotationMetaData metaData : getStateCalls())
            {
                buf.append(printMethod((Method) metaData.getMember())).append("\n");
            }
        }
        buf.append("iBean Methods: \n");
        for (AnnotationMetaData metaData : getCalls())
        {
            Method m = (Method) metaData.getMember();
            buf.append(spacer).append("@Call: ").append(m.getReturnType().getSimpleName()).append(" ");
            buf.append(printMethod(m)).append("\n");
        }

        for (AnnotationMetaData metaData : getTemplates())
        {
            Method m = (Method) metaData.getMember();
            buf.append(spacer).append("@Template: ").append(m.getReturnType().getSimpleName()).append(" ");
            buf.append(printMethod(m)).append("\n");
        }

        if (getFields().size() > 0)
        {
            buf.append("Default values:\n");
            for (FieldAnnotation field : fields)
            {
                buf.append(SPACER).append(field.getPrefix()).append(": ");
                buf.append(field.getFieldType().getSimpleName()).append(" ").append(field.getParamName())
                        .append("=").append(field.getFieldValue()).append(" [").append(field.getFieldName()).append("]\n");
            }
        }
        buf.append("\nMethod parameters marked with a '*' are optional, null can be used.\n");
        return buf.toString();
    }

    protected String printMethod(Method method)
    {
        StringBuffer buf = new StringBuffer();
        buf.append(method.getName());
        List<AnnotationParam> params = listParams(method);
        boolean first = true;
        buf.append("(");
        for (AnnotationParam param : params)
        {
            if (!first)
            {
                buf.append(", ");
            }
            first = false;
            buf.append(param.getParamType().getSimpleName()).append(" ").append(param.getName());
            if (param.isOptional())
            {
                buf.append("*");
            }
        }
        buf.append(")");
        return buf.toString();
    }
}
