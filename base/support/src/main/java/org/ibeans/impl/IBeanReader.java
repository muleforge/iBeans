/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.ibeans.impl;

import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.activation.DataSource;

import org.ibeans.annotation.Invoke;
import org.ibeans.annotation.Namespace;
import org.ibeans.annotation.State;
import org.ibeans.annotation.param.Attachment;
import org.ibeans.annotation.param.Body;
import org.ibeans.annotation.param.BodyParam;
import org.ibeans.annotation.param.HeaderParam;
import org.ibeans.annotation.param.Optional;
import org.ibeans.annotation.param.Order;
import org.ibeans.annotation.param.PropertyParam;
import org.ibeans.annotation.param.ReturnType;
import org.ibeans.annotation.param.UriParam;
import org.ibeans.api.DataType;
import org.ibeans.api.IBeanStateData;
import org.ibeans.api.IBeansException;
import org.ibeans.api.InvocationContext;
import org.ibeans.api.ParamFactory;
import org.ibeans.api.ParamFactoryHolder;
import org.ibeans.api.channel.CHANNEL;
import org.ibeans.api.channel.HTTP;
import org.ibeans.impl.i18n.IBeansMessages;
import org.ibeans.impl.support.NamespaceMap;
import org.ibeans.impl.support.annotation.AnnotationMetaData;
import org.ibeans.impl.support.annotation.AnnotationUtils;
import org.ibeans.impl.support.datatype.DataTypeFactory;
import org.ibeans.impl.support.ds.DataSourceFactory;
import org.ibeans.impl.support.util.Utils;
import org.ibeans.spi.ErrorFilter;
import org.ibeans.spi.ErrorFilterFactory;
import org.ibeans.spi.ExpressionParser;
import org.ibeans.spi.IBeansPlugin;

/**
 * This class wraps the logic for parsing iBean annotations and stores any state associated with the annotations, including
 * all default and state properties and return type.
 * <p/>
 * The code has been put here so that it could be shared between different annotation handlers.  Currently there are only
 * two method level annotations that have handlers, the {@link org.mule.ibeans.api.client.Call} annotation and the {@link org.mule.ibeans.api.client.Template} annotation.
 */
public class IBeanReader
{
    protected Map registryMap;
    protected ExpressionParser parser;
    protected IBeansPlugin plugin;

    public IBeanReader(IBeansPlugin plugin) throws IBeansException
    {
        this.plugin = plugin;
        registryMap = plugin.getProperties();
        parser = plugin.getExpressionParser();
    }

    private Object parsePropertyPlaceholderValues(Object value)
    {
        if (value != null && value instanceof String)
        {
            return parser.parsePropertyPlaceholders(registryMap, (String)value);
        }
        else
        {
            return value;
        }
    }

    public IBeanStateData readStateData(Class ibean) throws IBeansException
    {
        DefaultIBeanStateData stateDataDefault = new DefaultIBeanStateData();
        addErrorFilters(ibean, stateDataDefault.getErrorFilters());
        addMethodLevelErrorFilters(ibean, stateDataDefault.getMethodLevelErrorFilters());

        try
        {

            Set<AnnotationMetaData> annos = AnnotationUtils.getFieldAnnotationsForHeirarchy(ibean);
            for (AnnotationMetaData metaData : annos)
            {
                if (metaData.getAnnotation() instanceof Order)
                {
                    continue;
                }
                else if (metaData.getAnnotation() instanceof Namespace)
                {

                    String prefix = ((Namespace) metaData.getAnnotation()).value();
                    stateDataDefault.getNamespaces().put(prefix, (String)((Field) metaData.getMember()).get(ibean));
                    continue;

                }
                Field field = (Field) metaData.getMember();
                UriParam uriParam = field.getAnnotation(UriParam.class);
                if (uriParam != null)
                {
                    String key = (uriParam.value().length() > 0 ? uriParam.value() : field.getName());
                    if (ParamFactory.class.isAssignableFrom(field.getType()))
                    {
                        ParamFactory pf = (ParamFactory) field.get(ibean);
                        Order order = field.getAnnotation(Order.class);
                        ParamFactoryHolder holder = (order == null ? new ParamFactoryHolder(pf, key) : new ParamFactoryHolder(pf, key, order.value()));
                        stateDataDefault.getUriFactoryParams().add(holder);
                    }
                    else
                    {
                        stateDataDefault.getUriParams().put(key, encode(field.get(ibean)));
                    }

                }
                
                BodyParam bodyParam = field.getAnnotation(BodyParam.class);
                if (bodyParam != null)
                {
                	String key = (bodyParam.value().length() > 0 ? bodyParam.value() : field.getName());
                	if (ParamFactory.class.isAssignableFrom(field.getType())) 
                	{
                		ParamFactory pf = (ParamFactory) field.get(ibean);
                		Order order = field.getAnnotation(Order.class);
                		ParamFactoryHolder holder = (order == null ? new ParamFactoryHolder(pf, key) : new ParamFactoryHolder(pf, key, order.value()));
                        stateDataDefault.getPayloadFactoryParams().add(holder);
                	}
                	else 
                	{
                		stateDataDefault.getPayloadParams().put(key, encode(field.get(ibean)));
                	}
                }

                HeaderParam headerParam = field.getAnnotation(HeaderParam.class);
                if (headerParam != null)
                {
                    String key = (headerParam.value().length() > 0 ? headerParam.value() : field.getName());
                    if (ParamFactory.class.isAssignableFrom(field.getType()))
                    {
                        ParamFactory pf = (ParamFactory) field.get(ibean);
                        Order order = field.getAnnotation(Order.class);
                        ParamFactoryHolder holder = (order == null ? new ParamFactoryHolder(pf, key) : new ParamFactoryHolder(pf, key, order.value()));
                        stateDataDefault.getHeaderFactoryParams().add(holder);
                    }
                    else
                    {
                        stateDataDefault.getHeaderParams().put(key, encode(field.get(ibean)));
                    }
                }

                Attachment attachment = field.getAnnotation(Attachment.class);
                if (attachment != null)
                {
                    String key = (attachment.value().length() > 0 ? attachment.value() : field.getName());
                    if (ParamFactory.class.isAssignableFrom(field.getType()))
                    {
                        ParamFactory pf = (ParamFactory) field.get(ibean);
                        Order order = field.getAnnotation(Order.class);
                        ParamFactoryHolder holder = (order == null ? new ParamFactoryHolder(pf, key) : new ParamFactoryHolder(pf, key, order.value()));
                        stateDataDefault.getAttachmentFactoryParams().add(holder);
                    }
                    else
                    {
                        stateDataDefault.getAttachmentParams().put(key, encode(field.get(ibean)));
                    }
                }

                PropertyParam propertyParam = field.getAnnotation(PropertyParam.class);
                if (propertyParam != null)
                {
                    String key = (propertyParam.value().length() > 0 ? propertyParam.value() : field.getName());
                    if (ParamFactory.class.isAssignableFrom(field.getType()))
                    {
                        ParamFactory pf = (ParamFactory) field.get(ibean);
                        Order order = field.getAnnotation(Order.class);
                        ParamFactoryHolder holder = (order == null ? new ParamFactoryHolder(pf, key) : new ParamFactoryHolder(pf, key, order.value()));
                        stateDataDefault.getPropertyFactoryParams().add(holder);
                    }
                    else if (Map.class.isAssignableFrom(field.getType()))
                    {
                        stateDataDefault.getPropertyParams().putAll((Map) field.get(ibean));
                    }
                    else
                    {
                        stateDataDefault.getPropertyParams().put(key, field.get(ibean));
                    }
                }

                ReturnType returnTypeAnno = field.getAnnotation(ReturnType.class);
                if (returnTypeAnno != null)
                {
                    if (field.getType().equals(Class.class))
                    {
                        stateDataDefault.setReturnType(DataTypeFactory.create((Class) field.get(ibean)));
                    }
                    else
                    {
                        throw new IllegalArgumentException("The @ReturnType annotation can only be set on Fields and parameters of type java.lang.Class");
                    }
                }
            }
        }
        catch (Exception e)
        {
            throw new IBeansException(e);
        }

        return stateDataDefault;
    }

    protected void addErrorFilters(Class ibean, Map<String, ErrorFilter> filters) throws IBeansException
    {
        List<ErrorFilterFactory> factories = plugin.getErrorFilterFactories();

        List<AnnotationMetaData> annos = AnnotationUtils.getClassAnnotationInHeirarchy(ibean);
        for (AnnotationMetaData metaData : annos)
        {
            Annotation anno = metaData.getAnnotation();
            if (!anno.annotationType().isAnnotationPresent(org.ibeans.annotation.filter.ErrorFilter.class))
            {
                continue;
            }

            for (ErrorFilterFactory factory : factories)
            {
                if (factory.isSupported(anno))
                {
                    ErrorFilterFactory.ErrorFilterHolder holder = factory.parse(anno);
                    filters.put(holder.getMimeType().toString(), holder.getFilter());
                    break;
                }
            }
        }
    }

    protected void addMethodLevelErrorFilters(Class ibean, Map<Method, ErrorFilter> filters) throws IBeansException
    {
        List<AnnotationMetaData> methodLevelFilterAnnotations = AnnotationUtils.getMethodMetaAnnotations(ibean, org.ibeans.annotation.filter.ErrorFilter.class);
        List<ErrorFilterFactory> factories = plugin.getErrorFilterFactories();

        for (AnnotationMetaData metaData : methodLevelFilterAnnotations)
        {
            for (ErrorFilterFactory factory : factories)
            {
                if (factory.isSupported(metaData.getAnnotation()))
                {
                    ErrorFilterFactory.ErrorFilterHolder holder = factory.parse(metaData.getAnnotation());
                    filters.put((Method) metaData.getMember(), holder.getFilter());
                    break;
                }
            }
        }

    }

    public void populate(InvocationContext context) throws Exception
    {
        context.getIBeanConfig().getHeaderParams().putAll(context.getIBeanDefaultConfig().getHeaderParams());
        context.getIBeanConfig().getPayloadParams().putAll(context.getIBeanDefaultConfig().getPayloadParams());
        context.getIBeanConfig().getUriParams().putAll(context.getIBeanDefaultConfig().getUriParams());
        context.getIBeanConfig().getPropertyParams().putAll(context.getIBeanDefaultConfig().getPropertyParams());
        context.getIBeanConfig().getPayloadParams().putAll(context.getIBeanDefaultConfig().getPayloadParams());

        Method method = context.getMethod();
        Object[] args = context.getArgs();
        DataType returnType = DataTypeFactory.createFromReturnType(context.getMethod());

        checkReturnClass(returnType, method);

        boolean stateCall = false;
        //Methods with a name that starts with 'ibean' can be ignored, these are reserved method names that
        //can be used by ibeans internally
        if(method.getName().startsWith("ibean"))
        {
            return;
        }
        if (method.isAnnotationPresent(State.class))
        {
            stateCall = true;
            //Record any parameter as stateful
            context.getIBeanConfig().setHeaderParams(context.getIBeanDefaultConfig().getHeaderParams());
            context.getIBeanConfig().setPayloadParams(context.getIBeanDefaultConfig().getPayloadParams());
            context.getIBeanConfig().setUriParams(context.getIBeanDefaultConfig().getUriParams());
        }

        List<Object> payloads = context.getIBeanConfig().getPayloads();

        //We can have method calls with no parameters
        if (method.getParameterAnnotations().length > 0)
        {
            //But we cannot have calls with parameters that are not annotated
            if (method.getParameterAnnotations()[0].length > 0)
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
                        if (args[i] instanceof ParamFactory)
                        {
                            addComplexParam(annotation, args[i], optional, context);
                        }
                        else if (args[i] instanceof Map)
                        {
                            addUriParamMap((UriParam) annotation, (Map) args[i], context.getIBeanConfig().getUriParams());
                        }
                        else
                        {
                            addUriParam((UriParam) annotation, args[i], method, context.getIBeanConfig().getUriParams(), optional);
                        }
                    }
                    else if (annotation.annotationType().equals(HeaderParam.class))
                    {
                        if (args[i] instanceof ParamFactory)
                        {
                            addComplexParam(annotation, args[i], optional, context);
                        }
                        else
                        {
                            addHeaderParam((HeaderParam) annotation, args[i], method, context.getIBeanConfig().getHeaderParams(), optional);
                        }
                    }
                    else if (annotation.annotationType().equals(PropertyParam.class))
                    {
                        if (args[i] instanceof ParamFactory)
                        {
                            addComplexParam(annotation, args[i], optional, context);
                        }
                        //allow @PropertyParams on Call methods as a way to pass data to a Factory
                        //but don't allow ParamFactories to be used for @PropertyParams on @Call method since
                        //their only purpose is to pass in data to a factory
                        else if (stateCall)
                        {
                            addPropertyParam((PropertyParam) annotation, args[i], method, context.getIBeanDefaultConfig().getPropertyParams(), optional);
                        }
                        else if (args[i] instanceof ParamFactory)
                        {
                            throw new IllegalArgumentException("The @PropertyParam can only be used on call methods without a ParamFactory return type");
                        }
                        else
                        {
                            addPropertyParam((PropertyParam) annotation, args[i], method, context.getIBeanConfig().getPropertyParams(), optional);
                        }
                    }
                    else if (annotation.annotationType().equals(BodyParam.class))
                    {
                        addPayloadParam((BodyParam) annotation, args[i], method, context.getIBeanConfig().getPayloadParams(), optional);
                    }
                    else if (annotation.annotationType().equals(Body.class))
                    {
                        payloads.add(args[i]);
                    }
                    else if (annotation.annotationType().equals(ReturnType.class))
                    {
                        if (args[i].getClass().equals(Class.class))
                        {
                            if (stateCall)
                            {
                                ((DefaultIBeanStateData)context.getIBeanDefaultConfig()).setReturnType(
                                    DataTypeFactory.create((Class) args[i]));
                            }
                            else
                            {
                                context.setInvocationReturnType(DataTypeFactory.create((Class) args[i]));
                            }
                        }
                    }
                    // Add mime type support on the annotation itself
                    else if (annotation.annotationType().equals(Attachment.class))
                    {
                        addAttachments((Attachment) annotation, args[i], method, context.getIBeanConfig().getAttachments(), optional);
                    }
                    else
                    {
                        throw new IllegalArgumentException("unknown annotation: " + annotation);
                    }
                }
            }
            else
            {
                throw new IllegalArgumentException("Parameter for method: " + method.getName() + " has parameters that are not annotated");
            }
        }

        if(!stateCall)
        {
            if(context.getIBeanConfig().getPropertyParams().get(HTTP.METHOD_KEY) == null)
            {
                 //By default all calls are HTTP POST
                 context.getIBeanConfig().getPropertyParams().put(HTTP.METHOD_KEY, "POST");
            }
            ((InternalInvocationContext)context).createMessage();
        }
    }

    private void checkReturnClass(DataType dataType, Method m)
    {
        Class c = dataType.getType();
        if (c.isPrimitive() && !void.class.equals(c) && !m.getName().equals("equals") && !m.getName().equals("hashcode"))
        {
            throw new IllegalArgumentException("iBean methods can only return objects, not primitives." + ("Method is: " + m) + ". Class is: " + c);
        }
    }

//    private DataType getReturnClass(Method method, DataType defaultReturnType)
//    {
//        DataTypeFactory factory = new DataTypeFactory();
//        DataType ret;
//        if (getInvocationReturnType() != null)
//        {
//            ret = getInvocationReturnType();
//            setInvocationReturnType(null);
//        }
//        //Only use the @ReturnType value if the method return type is a Generics type variable
//        else if (method.getGenericReturnType() instanceof TypeVariable)
//        {
//            ret = (defaultReturnType == null ? factory.createFromReturnType(method) : defaultReturnType);
//        }
////        else if(getReturnType()!=null)
////        {
////            ret= getReturnType();
////        }
//        else
//        {
//            ret = factory.createFromReturnType(method);
//        }
//        return ret;
//    }

    protected void addUriParamMap(UriParam annotation, Map<String, Object> arg, Map<String, Object> uriParams) throws UnsupportedEncodingException
    {
        String[] p = Utils.splitAndTrim(annotation.value(), ",");

        for (int j = 0; j < p.length; j++)
        {
            String s = p[j];
            if (arg.containsKey(s))
            {
                uriParams.put(s, encode(parsePropertyPlaceholderValues(arg.get(s))));
            }
            else
            {
                uriParams.put(s, CHANNEL.NULL_URI_PARAM);
            }
        }
        //Validate that we don't have any properties specified that aren't valid for this param map
        for (String key : arg.keySet())
        {
            if (!uriParams.containsKey(key))
            {
                throw new IllegalArgumentException("A UriParam named '" + key + "' was included in a @UriParam as a Map but was not specified on the @UriParam annotation: " + annotation);
            }
        }
    }

    protected void addAttachments(Attachment annotation, Object arg, Method method, Set<DataSource> attachments, boolean optional)
    {
        if (arg == null)
        {
            if (!optional)
            {
                throw new IllegalArgumentException(IBeansMessages.parameterNotOptional(annotation, method));
            }
        }
        else if (arg.getClass().isArray())
        {
            for (int i = 0; i < ((Object[]) arg).length; i++)
            {
                attachments.add(DataSourceFactory.create(annotation.value() + (i + 1), ((Object[]) arg)[i]));
            }
        }
        else
        {
            attachments.add(DataSourceFactory.create(annotation.value(), arg));
        }
    }

    protected void addPropertyParam(PropertyParam annotation, Object arg, Method method, Map<String, Object> params, boolean optional)
    {
        //Allow Property Param values to use property placeholders. These will get resolved in the registry
        addParams(annotation, parsePropertyPlaceholderValues(arg), annotation.value(), method, params, optional);
    }

    protected void addUriParam(UriParam annotation, Object arg, Method method, Map<String, Object> params, boolean optional) throws UnsupportedEncodingException
    {
        if (arg == null && optional)
        {
            arg = CHANNEL.NULL_URI_PARAM;
        }
        else if (arg == null)
        {
            throw new IllegalArgumentException(IBeansMessages.parameterNotOptional(annotation, method));
        }

        //Allow Property Param values to use property placeholders. These will get resolved in the registry
        params.put(annotation.value(), encode(parsePropertyPlaceholderValues(arg)));
    }

    protected void addComplexParam(Annotation annotation, Object arg, boolean optional, InvocationContext ctx) throws Exception
    {
        if (arg == null && optional)
        {
            arg = CHANNEL.NULL_URI_PARAM;
        }
        else if (arg == null)
        {
            throw new IllegalArgumentException(IBeansMessages.parameterNotOptional(annotation, ctx.getMethod()));
        }
        if (!(arg instanceof ParamFactory))
        {
            throw new IllegalArgumentException("@ComplexHeaderParam must annotate an instance of ParamFactory");
        }
        String paramName;
        if (annotation instanceof UriParam)
        {
            paramName = ((UriParam) annotation).value();
        }
        else if (annotation instanceof HeaderParam)
        {
            paramName = ((HeaderParam) annotation).value();
        }
        else if (annotation instanceof BodyParam)
        {
            paramName = ((BodyParam) annotation).value();
        }
        else if (annotation instanceof PropertyParam)
        {
            paramName = ((PropertyParam) annotation).value();
        }
        else
        {
            throw new IllegalArgumentException("ParamFactory not supported in conjunction with: " + annotation);
        }

        ParamFactory factory = (ParamFactory) arg;
        final Object value = factory.create(paramName, optional, ctx);

        if (annotation instanceof UriParam)
        {
            ctx.getIBeanConfig().addUriParam(paramName, encode(value));
        }
        else if (annotation instanceof HeaderParam)
        {
            ctx.getIBeanConfig().addHeaderParam(paramName, encode(value));
        }
        else if (annotation instanceof PropertyParam)
        {
            ctx.getIBeanConfig().addPropertyParam(paramName, encode(value));
        }
        else
        {
            ctx.getIBeanConfig().addPayloadParam(paramName, encode(value));
        }

    }

    protected void addPayloadParam(BodyParam annotation, Object arg, Method method, Map<String, Object> params, boolean optional)
    {
        addParams(annotation, parsePropertyPlaceholderValues(arg), annotation.value(), method, params, optional);
    }

    protected void addHeaderParam(HeaderParam annotation, Object arg, Method method, Map<String, Object> params, boolean optional)
    {
        //Allow Property Param values to use property placeholders. These will get resolved in the registry
        addParams(annotation, parsePropertyPlaceholderValues(arg), annotation.value(), method, params, optional);
    }

    protected void addParams(Annotation annotation, Object arg, String key, Method method, Map<String, Object> params, boolean optional)
    {
        if (arg == null)
        {
            if (!optional)
            {
                throw new IllegalArgumentException(IBeansMessages.parameterNotOptional(annotation, method).toString());
            }
            else
            {
                //If the param has explicitly been set to null, remove it
                params.remove(key);
            }
        }
        else if (arg instanceof Map)
        {
            // Only do a putAll for a Call or Template method as it makes sense to expand the map for parameters
            // passed using http but not for Invoke when calling out to a method that might take a map as a parameter.
            if (method.getAnnotation(Invoke.class) != null)
            {
                params.put(key, arg);
            }
            else
            {
                params.putAll((Map) arg);
            }
        }
        else
        {
            params.put(key, arg);
        }
    }


    /**
     * Currently this method only handles spaces, any other illegal characters will throw an exception, it is up to the
     * developer to pass in encoded values right now.
     *
     * @param obj The value to encode
     * @return the encoded value
     * @throws java.io.UnsupportedEncodingException if an illegal coding is set as the container default
     */
    protected Object encode(Object obj) throws UnsupportedEncodingException
    {
        if (obj == null)
        {
            return obj;
        }

        if (obj instanceof String)
        {
            String result = obj.toString().replaceAll(" ", "+");
            result = result.replaceAll("@", "%40");
            result = result.replaceAll("#", "%23");
            return result;
//            int i = obj.toString().indexOf("?");
//            if (i > -1)
//            {
//                String query = obj.toString().substring(i + 1);
//                query = URLEncoder.encode(query, muleContext.getConfiguration().getDefaultEncoding());
//                return obj.toString().substring(0, i) + query;
//            }
        }
        return obj;
    }

    class DefaultIBeanStateData implements IBeanStateData
    {
        protected Map<String, Object> defaultUriParams = new HashMap<String, Object>();
        protected Map<String, Object> defaultHeaderParams = new HashMap<String, Object>();
        protected Map<String, Object> defaultPayloadParams = new HashMap<String, Object>();
        protected Map<String, Object> defaultPropertyParams = new HashMap<String, Object>();
        protected Map<String, Object> defaultAttachmentParams = new HashMap<String, Object>();

        protected Set<ParamFactoryHolder> defaultUriFactoryParams = new TreeSet<ParamFactoryHolder>();
        protected Set<ParamFactoryHolder> defaultHeaderFactoryParams = new TreeSet<ParamFactoryHolder>();
        protected Set<ParamFactoryHolder> defaultPayloadFactoryParams = new TreeSet<ParamFactoryHolder>();
        protected Set<ParamFactoryHolder> defaultPropertyFactoryParams = new TreeSet<ParamFactoryHolder>();
        protected Set<ParamFactoryHolder> defaultAttachmentFactoryParams = new TreeSet<ParamFactoryHolder>();

        protected Map<String, ErrorFilter> errorFilters = new HashMap<String, ErrorFilter>();
        protected Map<Method, ErrorFilter> methodLevelErrorFilters = new HashMap<Method, ErrorFilter>();

        protected DataType returnType = null;

        protected NamespaceMap namespaces = new NamespaceMap();

        public Map<String, Object> getHeaderParams()
        {
            return defaultHeaderParams;
        }

        public Map<String, Object> getPayloadParams()
        {
            return defaultPayloadParams;
        }

        public Map<String, Object> getUriParams()
        {
            return defaultUriParams;
        }

        public Map<String, Object> getPropertyParams()
        {
            return defaultPropertyParams;
        }

        public Map<String, Object> getAttachmentParams()
        {
            return defaultAttachmentParams;
        }

        public DataType getReturnType()
        {
            return returnType;
        }
        public Map<String, ErrorFilter> getErrorFilters()
        {
            return errorFilters;
        }

        public Map<Method, ErrorFilter> getMethodLevelErrorFilters()
        {
            return methodLevelErrorFilters;
        }

        public Set<ParamFactoryHolder> getUriFactoryParams()
        {
            return defaultUriFactoryParams;
        }

        public Set<ParamFactoryHolder> getHeaderFactoryParams()
        {
            return defaultHeaderFactoryParams;
        }
        
        public Set<ParamFactoryHolder> getPayloadFactoryParams()
        {
        	return defaultPayloadFactoryParams;
        }

        public Set<ParamFactoryHolder> getPropertyFactoryParams()
        {
            return defaultPropertyFactoryParams;
        }

        public Set<ParamFactoryHolder> getAttachmentFactoryParams()
        {
            return defaultAttachmentFactoryParams;
        }

        public NamespaceMap getNamespaces()
        {
            return namespaces;
        }

        public  void setReturnType(DataType returnType)
        {
            checkReturnClass(returnType, null);
            this.returnType = returnType;
        }

    }
}
