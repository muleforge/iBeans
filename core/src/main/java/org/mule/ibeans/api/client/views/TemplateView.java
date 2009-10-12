/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
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
import java.lang.annotation.ElementType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * TODO
 */
public abstract class TemplateView implements IBeanView
{
    private Class ibeanClass;
    private String usage;
    private String shortId;
    private boolean authentication;

    List<AnnotationMetaData> calls = new ArrayList<AnnotationMetaData>();
    List<AnnotationMetaData> templates = new ArrayList<AnnotationMetaData>();
    List<AnnotationMetaData> stateCalls = new ArrayList<AnnotationMetaData>();
    List<AnnotationMetaData> authCalls = new ArrayList<AnnotationMetaData>();
    List<FieldAnnotation> fields = new ArrayList<FieldAnnotation>();

    public String createView(Class ibean)
    {
        this.ibeanClass = ibean;

        AnnotationMetaData anno = AnnotationUtils.getClassAnnotationInHeirarchy(Usage.class, ibean);
        if (anno != null)
        {
            usage = ((Usage) anno.getAnnotation()).value();
        }
        shortId = IBeansNotationHelper.getIBeanShortID(ibean);
        authentication = ClientAuthentication.class.isAssignableFrom(ibean);


        for (int i = 0; i < ibean.getMethods().length; i++)
        {
            Method method = ibean.getMethods()[i];
            if (method.isAnnotationPresent(Call.class))
            {
                calls.add(new AnnotationMetaData(ibeanClass, method, ElementType.METHOD, method.getAnnotation(Call.class)));
            }
            else if (method.isAnnotationPresent(Template.class))
            {
                templates.add(new AnnotationMetaData(ibeanClass, method, ElementType.METHOD, method.getAnnotation(Template.class)));
                ;
            }
            else if (method.isAnnotationPresent(State.class))
            {
                stateCalls.add(new AnnotationMetaData(ibeanClass, method, ElementType.METHOD, method.getAnnotation(State.class)));
            }
            //TODO Authentication
        }

        Set<AnnotationMetaData> flds = AnnotationUtils.getFieldAnnotationsForHeirarchy(ibean);
        for (AnnotationMetaData field : flds)
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
                fields.add(new FieldAnnotation("@UriParam", f.getType(), f.getName(), value, uriParam.value(), uriParam));
            }
            if (field.getAnnotation() instanceof HeaderParam)
            {
                HeaderParam headerParam = (HeaderParam) field.getAnnotation();
                fields.add(new FieldAnnotation("@HeaderParam", f.getType(), f.getName(), value, headerParam.value(), headerParam));
            }
            if (field.getAnnotation() instanceof PropertyParam)
            {
                PropertyParam propertyParam = (PropertyParam) field.getAnnotation();
                fields.add(new FieldAnnotation("@PropertyParam", f.getType(), f.getName(), value, propertyParam.value(), propertyParam));
            }
            if (field.getAnnotation() instanceof ReturnType)
            {
                fields.add(new FieldAnnotation("@ReturnType", f.getType(), f.getName(), null, null, field.getAnnotation()));
            }
        }
        return null;
    }

    public List<AnnotationParam> listParams(Method method)
    {
        List<AnnotationParam> params = new ArrayList<AnnotationParam>();
        if (method.getParameterAnnotations().length > 0 && method.getParameterAnnotations()[0].length > 0)
        {
            for (int i = 0; i < method.getParameterAnnotations().length; i++)
            {
                Annotation annotation = method.getParameterAnnotations()[i][0];
                boolean optional = false;
                if (annotation.annotationType().equals(Optional.class))
                {
                    optional = true;
                    annotation = method.getParameterAnnotations()[i][1];
                }
                if (annotation.annotationType().equals(UriParam.class))
                {
                    UriParam param = (UriParam) annotation;
                    params.add(new AnnotationParam(param.value(), method.getParameterTypes()[i], param, optional));
                }
                else if (annotation.annotationType().equals(HeaderParam.class))
                {
                    HeaderParam param = (HeaderParam) annotation;
                    params.add(new AnnotationParam(param.value(), method.getParameterTypes()[i], param, optional));
                }
                else if (annotation.annotationType().equals(PropertyParam.class))
                {
                    PropertyParam param = (PropertyParam) annotation;
                    params.add(new AnnotationParam(param.value(), method.getParameterTypes()[i], param, optional));
                }
                else if (annotation.annotationType().equals(PayloadParam.class))
                {
                    PayloadParam param = (PayloadParam) annotation;
                    params.add(new AnnotationParam(param.value(), method.getParameterTypes()[i], param, optional));
                }
                else if (annotation.annotationType().equals(Payload.class))
                {
                    params.add(new AnnotationParam("[Payload]", method.getParameterTypes()[i], annotation, optional));
                }
                else if (annotation.annotationType().equals(ReturnType.class))
                {
                    params.add(new AnnotationParam("[ReturnType]", method.getParameterTypes()[i], annotation, optional));
                }
                // Add mime type support on the annotation itself
                else if (annotation.annotationType().equals(Attachment.class))
                {
                    params.add(new AnnotationParam("[attachment]", method.getParameterTypes()[i], annotation, optional));
                }
            }
        }
        return params;
    }

    public Class getIbeanClass()
    {
        return ibeanClass;
    }

    public String getUsage()
    {
        return usage;
    }

    public String getShortId()
    {
        return shortId;
    }

    public boolean isAuthentication()
    {
        return authentication;
    }

    public List<AnnotationMetaData> getCalls()
    {
        return calls;
    }

    public List<AnnotationMetaData> getTemplates()
    {
        return templates;
    }

    public List<AnnotationMetaData> getStateCalls()
    {
        return stateCalls;
    }

    public List<AnnotationMetaData> getAuthCalls()
    {
        return authCalls;
    }

    public List<FieldAnnotation> getFields()
    {
        return fields;
    }

    public class AnnotationParam
    {
        private String name;
        private Annotation annotation;
        private boolean optional = false;
        private Class<?> paramType;

        public AnnotationParam(String name, Class<?> paramType, Annotation annotation, boolean optional)
        {
            this.name = name;
            this.annotation = annotation;
            this.optional = optional;
            this.paramType = paramType;
        }

        public String getName()
        {
            return name;
        }

        public Annotation getAnnotation()
        {
            return annotation;
        }

        public boolean isOptional()
        {
            return optional;
        }

        public Class getParamType()
        {
            return paramType;
        }
    }

    public class FieldAnnotation
    {
        private String prefix;
        private Annotation annotation;
        private String fieldName;
        private Object fieldValue;
        private Class fieldType;
        private String paramName;

        public FieldAnnotation(String prefix, Class fieldType, String fieldName, Object fieldValue, String paramName, Annotation annotation)
        {
            this.prefix = prefix;
            this.annotation = annotation;
            this.fieldName = fieldName;
            this.fieldValue = fieldValue;
            this.paramName = paramName;
            this.fieldType = fieldType;
        }

        public String getPrefix()
        {
            return prefix;
        }

        public Annotation getAnnotation()
        {
            return annotation;
        }

        public String getFieldName()
        {
            return fieldName;
        }

        public Object getFieldValue()
        {
            return fieldValue;
        }

        public String getParamName()
        {
            return paramName;
        }

        public Class getFieldType()
        {
            return fieldType;
        }

    }
}
