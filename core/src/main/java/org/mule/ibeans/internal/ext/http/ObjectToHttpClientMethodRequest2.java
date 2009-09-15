/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.internal.ext.http;

import org.mule.RequestContext;
import org.mule.api.MuleContext;
import org.mule.api.MuleEvent;
import org.mule.api.MuleMessage;
import org.mule.api.config.MuleProperties;
import org.mule.api.context.MuleContextAware;
import org.mule.api.transformer.TransformerException;
import org.mule.api.transport.OutputHandler;
import org.mule.api.transport.PropertyScope;
import org.mule.transformer.AbstractMessageAwareTransformer;
import org.mule.transport.NullPayload;
import org.mule.transport.http.HttpConnector;
import org.mule.transport.http.HttpConstants;
import org.mule.transport.http.StreamPayloadRequestEntity;
import org.mule.transport.http.i18n.HttpMessages;
import org.mule.util.StringUtils;

import java.io.InputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpVersion;
import org.apache.commons.httpclient.methods.ByteArrayRequestEntity;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.EntityEnclosingMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.HeadMethod;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.OptionsMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.methods.TraceMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.lang.SerializationUtils;

/**
 * <code>ObjectToHttpClientMethodRequest</code> transforms a MuleMessage into a
 * HttpClient HttpMethod that represents an HttpRequest.
 */

public class ObjectToHttpClientMethodRequest2 extends AbstractMessageAwareTransformer implements MuleContextAware
{

    private MuleContext muleContext;

    public ObjectToHttpClientMethodRequest2()
    {
        setReturnClass(HttpMethod.class);
        registerSourceType(MuleMessage.class);
        registerSourceType(byte[].class);
        registerSourceType(String.class);
        registerSourceType(InputStream.class);
        registerSourceType(OutputHandler.class);
        registerSourceType(NullPayload.class);
    }

    public void setMuleContext(MuleContext context)
    {
        this.muleContext = context;
    }


    public Object transform(MuleMessage msg, String outputEncoding) throws TransformerException
    {
        Object src = msg.getPayload();

        String endpoint = msg.getStringProperty(MuleProperties.MULE_ENDPOINT_PROPERTY, null);
        if (endpoint == null)
        {
            throw new TransformerException(
                    HttpMessages.eventPropertyNotSetCannotProcessRequest(
                            MuleProperties.MULE_ENDPOINT_PROPERTY), this);
        }

        String method = (String) msg.getProperty(HttpConnector.HTTP_METHOD_PROPERTY, PropertyScope.OUTBOUND);
        if (method == null)
        {
            method = msg.getStringProperty(HttpConnector.HTTP_METHOD_PROPERTY, "POST");
        }

        try
        {
            //Allow Expressions to be embedded
            endpoint = endpoint.replaceAll("%23", "#");
            endpoint = muleContext.getExpressionManager().parse(endpoint, msg, true);
            URI uri = new URI(endpoint);
            HttpMethod httpMethod;

            if (HttpConstants.METHOD_GET.equals(method))
            {
                httpMethod = new GetMethod(uri.toString());
                String paramName = URLEncoder.encode(msg.getStringProperty(HttpConnector.HTTP_GET_BODY_PARAM_PROPERTY,
                        HttpConnector.DEFAULT_HTTP_GET_BODY_PARAM_PROPERTY), outputEncoding);
                String paramValue = URLEncoder.encode(src.toString(), outputEncoding);

                String query = uri.getRawQuery();
                if (!(src instanceof NullPayload) && !StringUtils.EMPTY.equals(src))
                {
                    if (query == null)
                    {
                        query = paramName + "=" + paramValue;
                    }
                    else
                    {
                        query += "&" + paramName + "=" + paramValue;
                    }
                }
                httpMethod.setQueryString(query);

            }
            else if (HttpConstants.METHOD_POST.equalsIgnoreCase(method))
            {
                PostMethod postMethod = new PostMethod(uri.toString());
                String paramName = msg.getStringProperty(HttpConnector.HTTP_POST_BODY_PARAM_PROPERTY, null);

                if (src instanceof Map)
                {
                    for (Iterator iterator = ((Map) src).entrySet().iterator(); iterator.hasNext();)
                    {
                        Map.Entry entry = (Map.Entry) iterator.next();
                        postMethod.addParameter(entry.getKey().toString(), entry.getValue().toString());
                    }
                }
                else if (paramName != null)
                {
                    postMethod.addParameter(paramName, src.toString());

                }
                else
                {
                    setupEntityMethod(src, outputEncoding, msg, uri, postMethod);
                }

                httpMethod = postMethod;
            }
            else if (HttpConstants.METHOD_PUT.equalsIgnoreCase(method))
            {
                PutMethod putMethod = new PutMethod(uri.toString());

                setupEntityMethod(src, outputEncoding, msg, uri, putMethod);

                httpMethod = putMethod;
            }
            else if (HttpConstants.METHOD_DELETE.equalsIgnoreCase(method))
            {
                httpMethod = new DeleteMethod(uri.toString());
            }
            else if (HttpConstants.METHOD_HEAD.equalsIgnoreCase(method))
            {
                httpMethod = new HeadMethod(uri.toString());
            }
            else if (HttpConstants.METHOD_OPTIONS.equalsIgnoreCase(method))
            {
                httpMethod = new OptionsMethod(uri.toString());
            }
            else if (HttpConstants.METHOD_TRACE.equalsIgnoreCase(method))
            {
                httpMethod = new TraceMethod(uri.toString());
            }
            else
            {
                throw new TransformerException(HttpMessages.unsupportedMethod(method));
            }

            // Allow the user to set HttpMethodParams as an object on the message
            HttpMethodParams params = (HttpMethodParams) msg.removeProperty(HttpConnector.HTTP_PARAMS_PROPERTY);
            if (params != null)
            {
                httpMethod.setParams(params);
            }
            else
            {
                // TODO we should probably set other properties here
                String httpVersion = msg.getStringProperty(HttpConnector.HTTP_VERSION_PROPERTY,
                        HttpConstants.HTTP11);
                if (HttpConstants.HTTP10.equals(httpVersion))
                {
                    httpMethod.getParams().setVersion(HttpVersion.HTTP_1_0);
                }
                else
                {
                    httpMethod.getParams().setVersion(HttpVersion.HTTP_1_1);
                }
            }

            setHeaders(httpMethod, msg);

            return httpMethod;
        }
        catch (Exception e)
        {
            throw new TransformerException(this, e);
        }
    }

    protected void setupEntityMethod(Object src,
                                     String encoding,
                                     MuleMessage msg,
                                     URI uri,
                                     EntityEnclosingMethod postMethod)
            throws UnsupportedEncodingException, TransformerException
    {
        // Dont set a POST payload if the body is a Null Payload.
        // This way client calls
        // can control if a POST body is posted explicitly
        if (!(msg.getPayload() instanceof NullPayload))
        {
            String mimeType = (String) msg.getProperty(HttpConstants.HEADER_CONTENT_TYPE, PropertyScope.OUTBOUND);
            if (mimeType == null)
            {
                mimeType = HttpConstants.DEFAULT_CONTENT_TYPE;
                logger.info("Content-Type not set on outgoing request, defaulting to: " + mimeType);
            }

            if (encoding != null
                    && !"UTF-8".equals(encoding.toUpperCase())
                    && mimeType.indexOf("charset") == -1)
            {
                mimeType += "; charset=" + encoding;
            }

            // Ensure that we have a cached representation of the message if we're using HTTP 1.0
            String httpVersion = msg.getStringProperty(HttpConnector.HTTP_VERSION_PROPERTY, HttpConstants.HTTP11);
            if (HttpConstants.HTTP10.equals(httpVersion))
            {
                try
                {
                    src = msg.getPayloadAsBytes();
                }
                catch (Exception e)
                {
                    throw new TransformerException(this, e);
                }
            }

            if (src instanceof String)
            {

                postMethod.setRequestEntity(new StringRequestEntity(src.toString(), mimeType, encoding));
                return;
            }

            if (src instanceof InputStream)
            {
                postMethod.setRequestEntity(new InputStreamRequestEntity((InputStream) src, mimeType));
            }
            else if (src instanceof byte[])
            {
                postMethod.setRequestEntity(new ByteArrayRequestEntity((byte[]) src, mimeType));
            }
            else if (src instanceof OutputHandler)
            {
                MuleEvent event = RequestContext.getEvent();
                postMethod.setRequestEntity(new StreamPayloadRequestEntity((OutputHandler) src, event));
            }
            else
            {
                byte[] buffer = SerializationUtils.serialize((Serializable) src);
                postMethod.setRequestEntity(new ByteArrayRequestEntity(buffer, mimeType));
            }
        }

    }

    protected void setHeaders(HttpMethod httpMethod, MuleMessage msg)
    {
        String headerName;

        for (Iterator iterator = msg.getPropertyNames(PropertyScope.OUTBOUND).iterator(); iterator.hasNext();)
        {
            headerName = (String) iterator.next();
            //Filter out any Mule-Specific headers
            if (!headerName.startsWith(MuleProperties.PROPERTY_PREFIX))
            {
                httpMethod.addRequestHeader(headerName, msg.getProperty(headerName, PropertyScope.OUTBOUND).toString());
            }
        }

        Set attNams = msg.getAttachmentNames();
        if (msg.getPayload() instanceof InputStream
                && attNams != null && attNams.size() > 0)
        {
            // must set this for receiver to properly parse attachments
            httpMethod.addRequestHeader(HttpConstants.HEADER_CONTENT_TYPE, "multipart/related");
        }

    }
}
