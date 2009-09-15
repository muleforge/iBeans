/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.api.client.views;

import org.mule.ibeans.api.IBeansNotationHelper;
import org.mule.ibeans.api.client.Call;
import org.mule.ibeans.api.client.State;
import org.mule.ibeans.api.client.Template;
import org.mule.ibeans.api.client.Usage;
import org.mule.ibeans.api.client.authentication.AuthenticationMethod;
import org.mule.ibeans.api.client.authentication.ClientAuthentication;
import org.mule.ibeans.api.client.params.Attachment;
import org.mule.ibeans.api.client.params.HeaderParam;
import org.mule.ibeans.api.client.params.Optional;
import org.mule.ibeans.api.client.params.Payload;
import org.mule.ibeans.api.client.params.PayloadParam;
import org.mule.ibeans.api.client.params.PropertyParam;
import org.mule.ibeans.api.client.params.ReturnType;
import org.mule.ibeans.api.client.params.UriParam;
import org.mule.utils.AnnotationMetaData;
import org.mule.utils.AnnotationUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Creates a text representation of the iBean, which is useful for describing how to use the iBean
 */
public class TextUsageView implements IBeanView
{
    public String createView(Class ibean)
    {
        StringBuffer buf = new StringBuffer();
        AnnotationMetaData usage = AnnotationUtils.getClassAnnotationInHeirarchy(Usage.class, ibean);
        if (usage != null)
        {
            buf.append(((Usage) usage.getAnnotation()).value());
            buf.append("\n");
        }
        else
        {
            buf.append("No usage text available for iBean").append("\n");
        }
        buf.append("Short ID: ").append(IBeansNotationHelper.getIBeanShortID(ibean)).append("\n");
        buf.append("Class: ").append(ibean).append("\n");
        buf.append("Authentication: ").append((ClientAuthentication.class.isAssignableFrom(ibean) ? "Yes" : "No")).append("\n");

        List<Method> call = new ArrayList<Method>();
        List<Method> template = new ArrayList<Method>();
        List<Method> state = new ArrayList<Method>();
        List<Method> auth = new ArrayList<Method>();

        for (int i = 0; i < ibean.getMethods().length; i++)
        {
            Method method = ibean.getMethods()[i];
            if (method.isAnnotationPresent(Call.class))
            {
                call.add(method);
            }
            else if (method.isAnnotationPresent(Template.class))
            {
                template.add(method);
            }
            else if (method.isAnnotationPresent(State.class))
            {
                state.add(method);
            }
            //TODO
//            else if (method.isAnnotationPresent(AuthenticationMethod.class))
//            {
//                auth.add(method);
//            }
        }
        String spacer = "  ";
        if (auth.size() > 0)
        {
            buf.append("Authentication Method (Required: Should be called first):\n");
            for (Method method : auth)
            {
                buf.append(spacer).append(method.getName()).append(listParams(method));
                buf.append(" [").append((method.getAnnotation(AuthenticationMethod.class)).value()).append(" Authentication]\n");
            }
        }
        if (state.size() > 0)
        {
            buf.append("State Methods (Should be called first, may be optional):\n");
            for (Method method : state)
            {
                buf.append(spacer).append(method.getName()).append(listParams(method)).append("\n");
            }
        }
        buf.append("iBean Methods: \n");
        for (Method method : call)
        {
            buf.append(spacer).append("@Call: ").append(method.getReturnType().getSimpleName()).append(" ").append(method.getName()).append(listParams(method)).append("\n");
        }

        for (Method method : template)
        {
            buf.append(spacer).append("@Template: ").append(method.getReturnType().getSimpleName()).append(" ").append(method.getName()).append(listParams(method)).append("\n");
        }

        buf.append(listDefaultValues(ibean));

        buf.append("\nMethod parameters marked with a '*' are optional, null can be used.\n");
        return buf.toString();
    }

    protected String listDefaultValues(Class ibean)
    {
        Set<AnnotationMetaData> fields = AnnotationUtils.getFieldAnnotationsForHeirarchy(ibean);
        StringBuffer buf = new StringBuffer();
        if (fields.size() > 0)
        {
            buf.append("Default values:\n");
        }
        for (AnnotationMetaData field : fields)
        {
            Field f = (Field) field.getMember();
            f.setAccessible(true);
            Object value = null;
            try
            {
                value = f.get(ibean);
            }
            catch (IllegalAccessException e)
            {
                //Ignore
            }
            if (field.getAnnotation() instanceof UriParam)
            {
                UriParam uriParam = (UriParam) field.getAnnotation();
                buf.append("  @UriParam: ").append(f.getType().getSimpleName()).append(" ").append(uriParam.value())
                        .append("=").append(value).append(" [").append(f.getName()).append("]\n");
            }
            if (field.getAnnotation() instanceof HeaderParam)
            {
                HeaderParam headerParam = (HeaderParam) field.getAnnotation();
                buf.append("  @HeaderParam: ").append(f.getType().getSimpleName()).append(" ").append(headerParam.value())
                        .append("=").append(value).append(" [").append(f.getName()).append("]\n");
            }

            if (field.getAnnotation() instanceof PropertyParam)
            {
                PropertyParam propertyParam = (PropertyParam) field.getAnnotation();
                buf.append("  @PropertyParam: ").append(f.getType().getSimpleName()).append(" ").append(propertyParam.value())
                        .append("=").append(value).append(" [").append(f.getName()).append("]\n");
            }

            if (field.getAnnotation() instanceof ReturnType)
            {
                buf.append("  @ReturnType: ").append(((Class) value).getName()).append(" [").append(f.getName()).append("]\n");
            }
        }
        return buf.toString();
    }

    protected String listParams(Method method)
    {
        StringBuffer buf = new StringBuffer();
        buf.append("(");

        if (method.getParameterAnnotations().length > 0 && method.getParameterAnnotations()[0].length > 0)
        {
            for (int i = 0; i < method.getParameterAnnotations().length; i++)
            {
                if (i > 0)
                {
                    buf.append(", ");
                }
                buf.append(method.getParameterTypes()[i].getSimpleName()).append(" ");
                Annotation annotation = method.getParameterAnnotations()[i][0];
                boolean optional = false;
                if (annotation.annotationType().equals(Optional.class))
                {
                    optional = true;
                    annotation = method.getParameterAnnotations()[i][1];
                }
                if (annotation.annotationType().equals(UriParam.class))
                {
                    buf.append((((UriParam) annotation).value()));
                }
                else if (annotation.annotationType().equals(HeaderParam.class))
                {
                    buf.append((((HeaderParam) annotation).value()));
                }
                else if (annotation.annotationType().equals(PropertyParam.class))
                {
                    buf.append((((PropertyParam) annotation).value()));
                }
                else if (annotation.annotationType().equals(PayloadParam.class))
                {
                    buf.append((((PayloadParam) annotation).value()));
                }
                else if (annotation.annotationType().equals(Payload.class))
                {
                    buf.append("[payload]");
                }
                else if (annotation.annotationType().equals(ReturnType.class))
                {
                    buf.append("[returnType]");
                }
                // Add mime type support on the annotation itself
                else if (annotation.annotationType().equals(Attachment.class))
                {
                    buf.append("[attachment]");
                }
                else
                {
                    buf.append("[unknown]");
                }
                if (optional)
                {
                    buf.append("*");
                }
            }
        }

        buf.append(")");
        return buf.toString();
    }
}
