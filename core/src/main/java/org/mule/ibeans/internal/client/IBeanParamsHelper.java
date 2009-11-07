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

import org.mule.DefaultMuleMessage;
import org.mule.api.MuleContext;
import org.mule.api.MuleMessage;
import org.mule.api.config.MuleProperties;
import org.mule.api.transport.PropertyScope;
import org.mule.ibeans.api.client.State;
import org.mule.ibeans.api.client.filters.AtomErrorFilter;
import org.mule.ibeans.api.client.filters.ErrorFilter;
import org.mule.ibeans.api.client.filters.ExpressionErrorFilter;
import org.mule.ibeans.api.client.filters.JsonErrorFilter;
import org.mule.ibeans.api.client.filters.RssErrorFilter;
import org.mule.ibeans.api.client.filters.XmlErrorFilter;
import org.mule.ibeans.api.client.params.Attachment;
import org.mule.ibeans.api.client.params.HeaderParam;
import org.mule.ibeans.api.client.params.InvocationContext;
import org.mule.ibeans.api.client.params.Optional;
import org.mule.ibeans.api.client.params.Order;
import org.mule.ibeans.api.client.params.ParamFactory;
import org.mule.ibeans.api.client.params.Payload;
import org.mule.ibeans.api.client.params.PayloadParam;
import org.mule.ibeans.api.client.params.PropertyParam;
import org.mule.ibeans.api.client.params.ReturnType;
import org.mule.ibeans.api.client.params.UriParam;
import org.mule.ibeans.channels.CHANNEL;
import org.mule.ibeans.i18n.IBeansMessages;
import org.mule.ibeans.internal.ext.DynamicOutboundEndpoint;
import org.mule.ibeans.internal.util.InputStreamDataSource;
import org.mule.ibeans.internal.util.NamedFileDataSource;
import org.mule.ibeans.internal.util.NamedURLDataSource;
import org.mule.ibeans.internal.util.StringDataSource;
import org.mule.routing.filters.ExpressionFilter;
import org.mule.transport.NullPayload;
import org.mule.utils.AnnotationMetaData;
import org.mule.utils.AnnotationUtils;

import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.TypeVariable;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.activation.FileTypeMap;
import javax.activation.MimetypesFileTypeMap;
import javax.activation.URLDataSource;

/**
 * This class wraps the logic for parsing iBean annotations and stores any state associated with the annotations, including
 * all default and state properties and return type.
 * <p/>
 * The code has been put here so that it could be shared between different annotation handlers.  Currently there are only
 * two method level annotations that have handlers, the {@link org.mule.ibeans.api.client.Call} annotation and the {@link org.mule.ibeans.api.client.Template} annotation.
 */
public class IBeanParamsHelper
{
    private MuleContext muleContext;
    protected Map<String, Object> defaultUriParams = new HashMap<String, Object>();
    protected Map<String, Object> defaultHeaderParams = new HashMap<String, Object>();
    protected Map<String, Object> defaultPayloadParams = new HashMap<String, Object>();
    protected Map<String, Object> defaultPropertyParams = new HashMap<String, Object>();

    protected Set<ParamFactoryHolder> defaultUriFactoryParams = new TreeSet<ParamFactoryHolder>();
    protected Set<ParamFactoryHolder> defaultHeaderFactoryParams = new TreeSet<ParamFactoryHolder>();

    protected Class returnType = null;
    protected Class invocationReturnType = null;
    protected Class ibeanInterface = null;

    /**
     * A lot of web servers do not use the http return code, instead they retuen an error message as the result of the call
     * This filter is used to determine whether an error was returned from the service
     */
    protected Map<String, ErrorExpressionFilter> errorFilters;
    protected Map<Method, ErrorExpressionFilter> methodErrorFilters;

    private static final FileTypeMap mimeType = new MimetypesFileTypeMap();

    public IBeanParamsHelper(MuleContext muleContext, Class iface)
    {
        this.muleContext = muleContext;
        this.ibeanInterface = iface;
        readErrorFilters(iface);
        readDefaultParams(iface);
    }

    protected void readErrorFilters(Class clazz)
    {
        errorFilters = new HashMap<String, ErrorExpressionFilter>();
        Annotation[] annos = clazz.getAnnotations();
        for (int i = 0; i < annos.length; i++)
        {
            Annotation anno = annos[i];
            if (!anno.annotationType().isAnnotationPresent(ErrorFilter.class))
            {
                continue;
            }
            ErrorExpressionFilter errorFilter;
            if (anno.annotationType().equals(JsonErrorFilter.class))
            {
                JsonErrorFilter filter = (JsonErrorFilter) anno;
                errorFilter = new ErrorExpressionFilter(JsonErrorFilter.evaluator, filter.expr());
                errorFilter.setErrorCodeExpr(filter.errorCode());
                errorFilters.put(JsonErrorFilter.mimeType, errorFilter);
            }
            else if (anno.annotationType().equals(XmlErrorFilter.class))
            {
                XmlErrorFilter filter = (XmlErrorFilter) anno;
                //Explicitly add the return type here
                errorFilter = new ErrorExpressionFilter(XmlErrorFilter.evaluator, "[boolean]" + filter.expr());
                errorFilter.setErrorCodeExpr(filter.errorCode());
                errorFilters.put(XmlErrorFilter.mimeType, errorFilter);
            }
            else if (anno.annotationType().equals(AtomErrorFilter.class))
            {
                AtomErrorFilter filter = (AtomErrorFilter) anno;
                //Explicitly add the return type here
                errorFilter = new ErrorExpressionFilter(AtomErrorFilter.evaluator, "[boolean]" + filter.expr());
                errorFilter.setErrorCodeExpr(filter.errorCode());

                errorFilters.put(AtomErrorFilter.mimeType, errorFilter);
            }
            else if (anno.annotationType().equals(RssErrorFilter.class))
            {
                RssErrorFilter filter = (RssErrorFilter) anno;
                //Explicitly add the return type here
                errorFilter = new ErrorExpressionFilter(RssErrorFilter.evaluator, "[boolean]" + filter.expr());
                errorFilter.setErrorCodeExpr(filter.errorCode());

                errorFilters.put(RssErrorFilter.mimeType, errorFilter);
            }
            else if (anno.annotationType().equals(ExpressionErrorFilter.class))
            {
                ExpressionErrorFilter filter = (ExpressionErrorFilter) anno;
                errorFilter = new ErrorExpressionFilter(filter.expr());
                errorFilters.put(filter.mimeType(), errorFilter);
            }
            else
            {
                throw new IllegalArgumentException("Unrecognised Error Filter: " + anno);
            }
        }
        List<AnnotationMetaData> results = AnnotationUtils.getMethodMetaAnnotations(clazz, ErrorFilter.class);
        methodErrorFilters = new HashMap<Method, ErrorExpressionFilter>();
        for (AnnotationMetaData result : results)
        {
            methodErrorFilters.put((Method) result.getMember(), new ErrorExpressionFilter(((ExpressionErrorFilter) result.getAnnotation()).expr()));
        }

        for (ExpressionFilter filter : errorFilters.values())
        {
            filter.setMuleContext(muleContext);
        }

        for (ExpressionFilter filter : methodErrorFilters.values())
        {
            filter.setMuleContext(muleContext);
        }

    }


    public void readDefaultParams(Class iface)
    {
        Set<AnnotationMetaData> annos = AnnotationUtils.getFieldAnnotationsForHeirarchy(iface);
        for (AnnotationMetaData metaData : annos)
        {
            if (metaData.getAnnotation() instanceof Order)
            {
                continue;
            }
            Field field = (Field) metaData.getMember();
            UriParam uriParam = field.getAnnotation(UriParam.class);
            if (uriParam != null)
            {
                try
                {
                    String key = (uriParam.value().length() > 0 ? uriParam.value() : field.getName());
                    if (ParamFactory.class.isAssignableFrom(field.getType()))
                    {
                        ParamFactory pf = (ParamFactory) field.get(iface);
                        Order order = field.getAnnotation(Order.class);
                        ParamFactoryHolder holder = (order == null ? new ParamFactoryHolder(pf, key) : new ParamFactoryHolder(pf, key, order.value()));
                        defaultUriFactoryParams.add(holder);
                    }
                    else
                    {
                        getDefaultUriParams().put(key, encode(field.get(iface)));
                    }
                }
                catch (Exception e)
                {
                    throw new RuntimeException(e);
                }
            }

            HeaderParam headerParam = field.getAnnotation(HeaderParam.class);
            if (headerParam != null)
            {
                try
                {
                    String key = (headerParam.value().length() > 0 ? headerParam.value() : field.getName());
                    if (ParamFactory.class.isAssignableFrom(field.getType()))
                    {
                        ParamFactory pf = (ParamFactory) field.get(iface);
                        Order order = field.getAnnotation(Order.class);
                        ParamFactoryHolder holder = (order == null ? new ParamFactoryHolder(pf, key) : new ParamFactoryHolder(pf, key, order.value()));
                        defaultHeaderFactoryParams.add(holder);
                    }
                    else
                    {
                        getDefaultHeaderParams().put(key, encode(field.get(iface)));
                    }
                }
                catch (Exception e)
                {
                    throw new RuntimeException(e);
                }
            }

            PropertyParam propertyParam = field.getAnnotation(PropertyParam.class);
            if (propertyParam != null)
            {
                try
                {
                    if (Map.class.isAssignableFrom(field.getType()))
                    {
                        getDefaultPropertyParams().putAll((Map) field.get(iface));
                    }
                    else
                    {
                        String key = (propertyParam.value().length() > 0 ? propertyParam.value() : field.getName());
                        getDefaultPropertyParams().put(key, field.get(iface));
                    }
                }
                catch (Exception e)
                {
                    throw new RuntimeException(e);
                }
            }

            ReturnType returnTypeAnno = field.getAnnotation(ReturnType.class);
            if (returnTypeAnno != null)
            {
                try
                {
                    if (field.getType().equals(Class.class))
                    {
                        setReturnType((Class) field.get(iface));
                    }
                    else
                    {
                        throw new IllegalArgumentException("The @ReturnType annotation can only be set on Fields and parameters of type java.lang.Class");
                    }
                }
                catch (Exception e)
                {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void populateInvocationContext(InvocationContext internalInvocationContext) throws Exception
    {
        InternalInvocationContext invocationContext = (InternalInvocationContext) internalInvocationContext;

        invocationContext.getHeaderParams().putAll(defaultHeaderParams);
        invocationContext.getPayloadParams().putAll(defaultPayloadParams);
        invocationContext.getUriParams().putAll(defaultUriParams);
        invocationContext.getPropertyParams().putAll(defaultPropertyParams);

        Method method = invocationContext.getMethod();
        Object[] args = invocationContext.getArgs();
        Class returnType = invocationContext.getMethod().getReturnType();

        checkReturnClass(returnType, invocationContext.getMethod());

        boolean stateCall = false;
        if (method.isAnnotationPresent(State.class))
        {
            stateCall = true;
            //Record any parameter as stateful
            invocationContext.headerParams = defaultHeaderParams;
            invocationContext.payloadParams = defaultPayloadParams;
            invocationContext.uriParams = defaultUriParams;
        }

        List payloads = invocationContext.getPayloads();

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
                            addComplexParam(annotation, args[i], optional, invocationContext);
                        }
                        else
                        {
                            addUriParam((UriParam) annotation, args[i], method, invocationContext.getUriParams(), optional);
                        }
                    }
                    else if (annotation.annotationType().equals(HeaderParam.class))
                    {
                        if (args[i] instanceof ParamFactory)
                        {
                            addComplexParam(annotation, args[i], optional, invocationContext);
                        }
                        else
                        {
                            addHeaderParam((HeaderParam) annotation, args[i], method, invocationContext.getHeaderParams(), optional);
                        }
                    }
                    else if (annotation.annotationType().equals(PropertyParam.class))
                    {
                        //IBEANS-95 allow @PropertyParams on Call methods as a way to pass data to a Factory
                        //but don't allow ParamFactories to be used for @PropertyParams on @Call method since
                        //thier only purpose is to pass in data to a factory
                        if (stateCall)
                        {
                            addPropertyParam((PropertyParam) annotation, args[i], method, defaultPropertyParams, optional);
                        }
                        else if (args[i] instanceof ParamFactory)
                        {
                            throw new IllegalArgumentException("The @PropertyParam can only be used on call methods without a ParamFactory return type");
                        }
                        else
                        {
                            addPropertyParam((PropertyParam) annotation, args[i], method, invocationContext.getPropertyParams(), optional);
                        }
                    }
                    else if (annotation.annotationType().equals(PayloadParam.class))
                    {
                        addPayloadParam((PayloadParam) annotation, args[i], method, invocationContext.getPayloadParams(), optional);
                    }
                    else if (annotation.annotationType().equals(Payload.class))
                    {
                        payloads.add(args[i]);
                    }
                    else if (annotation.annotationType().equals(ReturnType.class))
                    {
                        if (args[i].getClass().equals(Class.class))
                        {
                            if (stateCall)
                            {
                                this.returnType = (Class) args[i];
                            }
                            else
                            {
                                invocationReturnType = (Class) args[i];
                            }
                        }
                    }
                    // Add mime type support on the annotation itself
                    else if (annotation.annotationType().equals(Attachment.class))
                    {
                        addAttachments((Attachment) annotation, args[i], method, invocationContext.getAttachments(), optional);
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

        invocationContext.returnType = getReturnClass(method);
    }

    private void checkReturnClass(Class c, Method m)
    {
        if (c.isPrimitive() && !void.class.equals(c) && !m.getName().equals("equals") && !m.getName().equals("hashcode"))
        {

            throw new IllegalArgumentException("iBean methods can only return objects, not primitives." + (m != null ? "Method is: " + m : "") + ". Class is: " + c);
        }
    }

    private Class getReturnClass(Method method)
    {
        Class ret;
        if (getInvocationReturnType() != null)
        {
            ret = getInvocationReturnType();
            setInvocationReturnType(null);
        }
        //Only use the @ReturnType value if the method return type is a Generics type variable
        else if (method.getGenericReturnType() instanceof TypeVariable)
        {
            ret = (getReturnType() == null ? method.getReturnType() : getReturnType());
        }
//        else if(getReturnType()!=null)
//        {
//            ret= getReturnType();
//        }
        else
        {
            ret = method.getReturnType();
        }
        return ret;
    }

    public MuleMessage createMessage(InvocationContext ctx) throws Exception
    {
        MuleMessage message;
        if (ctx.getRequestPayloads().size() == 0)
        {
            if (ctx.getRequestPayloadParams().size() == 0)
            {
                ctx.getRequestPayloads().add(NullPayload.getInstance());
            }
            else
            {
                ctx.getRequestPayloads().add(ctx.getRequestPayloadParams());
            }
        }

        for (ParamFactoryHolder holder : defaultUriFactoryParams)
        {
            ctx.getUriParams().put(holder.getParamName(), holder.getParamFactory().create(holder.getParamName(), false, ctx));
        }

        for (ParamFactoryHolder holder : defaultHeaderFactoryParams)
        {
            ctx.getRequestHeaderParams().put(holder.getParamName(), holder.getParamFactory().create(holder.getParamName(), false, ctx));
        }

        //We need to scrub any null header values since Mule does not allow Null headers
        Map<String, Object> headers = new TreeMap<String, Object>();
        for (Iterator<Map.Entry<String, Object>> iterator = ctx.getRequestHeaderParams().entrySet().iterator(); iterator.hasNext();)
        {
            Map.Entry<String, Object> entry = iterator.next();
            if (entry.getValue() != null)
            {
                headers.put(entry.getKey(), entry.getValue());
            }
        }
        if (ctx.getRequestPayloads().size() == 1)
        {
            message = new DefaultMuleMessage(ctx.getRequestPayloads().get(0), headers, muleContext);
        }
        else
        {
            message = new DefaultMuleMessage(ctx.getRequestPayloads(), headers, muleContext);
        }

        //Some transports such as Axis, RMI and EJB can use the method information
        message.setProperty(MuleProperties.MULE_METHOD_PROPERTY, ctx.getMethod().getName(), PropertyScope.INVOCATION);

        //Set the URI params so the correct URI can be constructed for this invocation
        message.setProperty(CallOutboundEndpoint.URI_PARAM_PROPERTIES, ctx.getUriParams(), PropertyScope.INVOCATION);

        //Add any attachments
        for (DataSource dataSource : ctx.getAttachments())
        {
            message.addAttachment(dataSource.getName(), new DataHandler(dataSource));
        }
        //Special channel properties
        if (ctx.getPropertyParams().containsKey(CHANNEL.TIMEOUT))
        {
            message.setProperty(CHANNEL.TIMEOUT, ctx.getPropertyParams().get(CHANNEL.TIMEOUT), PropertyScope.INVOCATION);
        }
        return message;
    }

    protected void addAttachments(Attachment annotation, Object arg, Method method, List<DataSource> attachments, boolean optional)
    {
        if (arg == null)
        {
            if (!optional)
            {
                throw new IllegalArgumentException(IBeansMessages.parameterNotOptional(annotation, method).toString());
            }
        }
        else
        {
            int counter = 1;
            if (arg instanceof DataSource)
            {
                attachments.add((DataSource) arg);
            }
            else if (arg instanceof DataSource[])
            {
                DataSource[] ds = (DataSource[]) arg;
                for (int j = 0; j < ds.length; j++)
                {
                    attachments.add(ds[j]);

                }
            }
            else if (arg instanceof File)
            {
                attachments.add(new NamedFileDataSource((File) arg, annotation.value()));
            }
            else if (arg instanceof File[])
            {
                File[] files = (File[]) arg;
                for (int i = 0; i < files.length; i++)
                {
                    attachments.add(new FileDataSource(files[i]));
                }
            }
            else if (arg instanceof URL)
            {
                attachments.add(new NamedURLDataSource((URL) arg, annotation.value()));
            }
            else if (arg instanceof URL[])
            {
                URL[] urls = (URL[]) arg;
                for (int i = 0; i < urls.length; i++)
                {
                    attachments.add(new URLDataSource(urls[i]));

                }
            }
            else if (arg instanceof InputStream)
            {
                attachments.add(new InputStreamDataSource((InputStream) arg, (annotation.value().equals("") ? "isAttatchemnt" + counter++ : annotation.value())));
            }
            else if (arg instanceof InputStream[])
            {
                InputStream[] streams = (InputStream[]) arg;
                for (int i = 0; i < streams.length; i++)
                {
                    attachments.add(new InputStreamDataSource(streams[i], (annotation.value().equals("") ? "isAttatchemnt" + counter++ : annotation.value())));
                }
            }
            else
            {
                attachments.add(new StringDataSource(annotation.value(), arg.toString()));
            }
        }
    }

    protected void addPropertyParam(PropertyParam annotation, Object arg, Method method, Map<String, Object> params, boolean optional)
    {
        addParams(annotation, arg, annotation.value(), method, params, optional);
    }

    protected void addUriParam(UriParam annotation, Object arg, Method method, Map<String, Object> params, boolean optional) throws UnsupportedEncodingException
    {
        if (arg == null && optional)
        {
            arg = DynamicOutboundEndpoint.NULL_PARAM;
        }
        else if (arg == null)
        {
            throw new IllegalArgumentException(IBeansMessages.parameterNotOptional(annotation, method).toString());
        }

        params.put(annotation.value(), encode(arg));
    }

    protected void addComplexParam(Annotation annotation, Object arg, boolean optional, InternalInvocationContext ctx) throws UnsupportedEncodingException
    {
        if (arg == null && optional)
        {
            arg = DynamicOutboundEndpoint.NULL_PARAM;
        }
        else if (arg == null)
        {
            throw new IllegalArgumentException(IBeansMessages.parameterNotOptional(annotation, ctx.getMethod()).toString());
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
        else if (annotation instanceof PayloadParam)
        {
            paramName = ((PayloadParam) annotation).value();
        }
        else
        {
            throw new IllegalArgumentException("ParamFactory not supported in conjunction with: " + annotation);
        }

        ParamFactory factory = (ParamFactory) arg;
        final String value = factory.create(paramName, optional, ctx);

        if (annotation instanceof UriParam)
        {
            ctx.getUriParams().put(paramName, encode(value));
        }
        else if (annotation instanceof HeaderParam)
        {
            ctx.getHeaderParams().put(paramName, encode(value));
        }
        else if (annotation instanceof PayloadParam)
        {
            ctx.getPayloadParams().put(paramName, encode(value));
        }

    }

    protected void addPayloadParam(PayloadParam annotation, Object arg, Method method, Map<String, Object> params, boolean optional)
    {
        addParams(annotation, arg, annotation.value(), method, params, optional);
    }

    protected void addHeaderParam(HeaderParam annotation, Object arg, Method method, Map<String, Object> params, boolean optional)
    {
        addParams(annotation, arg, annotation.value(), method, params, optional);
    }

    protected void addParams(Annotation annotation, Object arg, String key, Method method, Map<String, Object> params, boolean optional)
    {
        if (arg == null)
        {
            if (!optional)
            {
                throw new IllegalArgumentException(IBeansMessages.parameterNotOptional(annotation, method).toString());
            }
        }
        else if (arg instanceof Map)
        {
            params.putAll((Map) arg);
        }
        else
        {
            params.put(key, arg);
        }
    }

    public Map<String, Object> getDefaultUriParams()
    {
        return defaultUriParams;
    }

    public void setDefaultUriParams(Map<String, Object> defaultUriParams)
    {
        this.defaultUriParams = defaultUriParams;
    }

    public Map<String, Object> getDefaultHeaderParams()
    {
        return defaultHeaderParams;
    }

    public void setDefaultHeaderParams(Map<String, Object> defaultHeaderParams)
    {
        this.defaultHeaderParams = defaultHeaderParams;
    }

    public Map<String, Object> getDefaultPayloadParams()
    {
        return defaultPayloadParams;
    }

    public void setDefaultPayloadParams(Map<String, Object> defaultPayloadParams)
    {
        this.defaultPayloadParams = defaultPayloadParams;
    }

    public Map<String, Object> getDefaultPropertyParams()
    {
        return defaultPropertyParams;
    }

    public void setDefaultPropertyParams(Map<String, Object> defaultPropertyParams)
    {
        this.defaultPropertyParams = defaultPropertyParams;
    }

    public Class getReturnType()
    {
        return returnType;
    }

    public void setReturnType(Class returnType)
    {
        checkReturnClass(returnType, null);
        this.returnType = returnType;
    }

    public Class getInvocationReturnType()
    {
        return invocationReturnType;
    }

    public void setInvocationReturnType(Class invocationReturnType)
    {
        this.invocationReturnType = invocationReturnType;
    }

    /**
     * Currently this method only handles spaces, any other illegal charactures will throw an exception, it is up to the
     * developer to pass in encoded values right now.
     *
     * @param obj The value to encode
     * @return the encoded value
     * @throws UnsupportedEncodingException if an illegal coding is set as the container default
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

    public Set<ParamFactoryHolder> getDefaultUriFactoryParams()
    {
        return defaultUriFactoryParams;
    }

    public Set<ParamFactoryHolder> getDefaultHeaderFactoryParams()
    {
        return defaultHeaderFactoryParams;
    }

    class ParamFactoryHolder implements Comparable
    {
        private ParamFactory paramFactory;
        private String paramName;
        private int order;

        ParamFactoryHolder(ParamFactory paramFactory, String paramName)
        {
            this(paramFactory, paramName, -1);
        }

        ParamFactoryHolder(ParamFactory paramFactory, String paramName, int order)
        {
            this.paramFactory = paramFactory;
            this.paramName = paramName;
            this.order = order;
        }

        public ParamFactory getParamFactory()
        {
            return paramFactory;
        }

        public String getParamName()
        {
            return paramName;
        }

        public int getOrder()
        {
            return order;
        }

        public int compareTo(Object o)
        {
            ParamFactoryHolder holder = (ParamFactoryHolder) o;

            if ((getOrder() == holder.getOrder()))
            {
                return 0;
            }
            else if (getOrder() > holder.getOrder())
            {
                return 1;
            }
            else
            {
                return -1;
            }
        }
    }

    public Class getIbeanInterface()
    {
        return ibeanInterface;
    }

    public Map<String, ErrorExpressionFilter> getErrorFilters()
    {
        return errorFilters;
    }

    public Map<Method, ErrorExpressionFilter> getMethodErrorFilters()
    {
        return methodErrorFilters;
    }
}
