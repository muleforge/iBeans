/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.internal.ext;

import org.mule.DefaultMuleMessage;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.api.MuleRuntimeException;
import org.mule.api.config.MuleProperties;
import org.mule.api.endpoint.EndpointURI;
import org.mule.api.endpoint.InboundEndpoint;
import org.mule.api.transport.Connector;
import org.mule.api.transport.DispatchException;
import org.mule.config.i18n.CoreMessages;
import org.mule.endpoint.DynamicURIInboundEndpoint;
import org.mule.endpoint.MuleEndpointURI;
import org.mule.transport.AbstractConnector;
import org.mule.transport.service.TransportFactory;
import org.mule.util.BeanUtils;
import org.mule.util.TemplateParser;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A dynamic request endpoint is used in conjection with the {@link org.mule.ibeans.api.client.Call} annotation when there are no {@link org.mule.ibeans.api.client.params.Payload},
 * {@link org.mule.ibeans.api.client.params.PayloadParam} or {@link org.mule.ibeans.api.client.params.HeaderParam} annotations
 * on a method and allows a dynamic {@link org.mule.api.endpoint.InboundEndpoint} to be created.  This endpoint is then used via the Mule {@link org.mule.api.transport.MessageRequester}
 * interface to make a specific request to a transport for a message.
 */
public class DynamicRequestEndpoint extends DynamicURIInboundEndpoint
{
    public static final String EVAL_PARAM_PROPERTY = "eval.param";
    /**
     * logger used by this class
     */
    protected transient final Log logger = LogFactory.getLog(DynamicRequestEndpoint.class);
    private static final long serialVersionUID = 8861985949279708638L;

    protected String uri;

    //Need a local read-write intance
    protected AbstractConnector localConnector;

    protected TemplateParser parser = TemplateParser.createCurlyBracesStyleParser();

    public DynamicRequestEndpoint(InboundEndpoint endpoint, String uri)
    {
        super(endpoint);
        this.uri = uri;
        this.localConnector = (AbstractConnector) endpoint.getConnector();
    }

    protected Map<String, Object> getPropertiesForTemplate(MuleMessage message)
    {
        Map<String, Object> props = new HashMap<String, Object>();
        // Also add the endpoint propertie so that users can set fallback values
        // when the property is not set on the event
        props.putAll(this.getProperties());
        for (Iterator iterator = message.getPropertyNames().iterator(); iterator.hasNext();)
        {
            String propertyKey = (String) iterator.next();
            props.put(propertyKey, message.getProperty(propertyKey));
        }
        return props;
    }

    protected EndpointURI getEndpointURIForMessage(MuleMessage message) throws DispatchException
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Uri before parsing is: " + uri);
        }

        Map<String, Object> props = getPropertiesForTemplate(message);

        String newUriString = parser.parse(props, uri);
        Object evalParam = props.get(EVAL_PARAM_PROPERTY);
        if (evalParam != null)
        {
            newUriString = this.getMuleContext().getExpressionManager().parse(newUriString, new DefaultMuleMessage(evalParam, getMuleContext()), true);
        }
        else
        {
            newUriString = this.getMuleContext().getExpressionManager().parse(newUriString, message, true);
        }
        if (logger.isDebugEnabled())
        {
            logger.debug("Uri after parsing is: " + newUriString);
        }

        try
        {
            setEndpointURI(new MuleEndpointURI(newUriString, getMuleContext()));

            if (!getLocalConnector().supportsProtocol(getEndpointURI().getScheme()))
            {
                throw new DispatchException(CoreMessages.schemeCannotChangeForRouter(
                        this.getEndpointURI().getScheme(), getEndpointURI().getScheme()), message, this);
            }
            getEndpointURI().initialise();
            return getEndpointURI();
        }
        catch (Exception e)
        {
            throw new DispatchException(
                    CoreMessages.templateCausedMalformedEndpoint(uri, newUriString),
                    message, this, e);
        }

    }

    @Override
    public Connector getConnector()
    {
        try
        {
            return getLocalConnector();
        }
        catch (MuleException e)
        {
            throw new MuleRuntimeException(e.getI18nMessage(), e);
        }
    }


    protected AbstractConnector getLocalConnector() throws MuleException
    {
        if (localConnector == null)
        {
            localConnector = (AbstractConnector) new TransportFactory(getMuleContext()).createConnector(getEndpointURI());
            getMuleContext().getRegistry().registerConnector(localConnector);
            //This allows connector properties to be set as propertieds on the endpoint
            BeanUtils.populateWithoutFail(localConnector, this.getProperties(), false);
        }
        return localConnector;
    }

    public MuleMessage request(long timeout, MuleMessage message) throws Exception
    {
        EndpointURI uri = getEndpointURIForMessage(message);
        DynamicURIInboundEndpoint inboundEndpoint = new DynamicURIInboundEndpoint(this, uri);
        if (message.getProperty(MuleProperties.MULE_CREDENTIALS_PROPERTY) != null)
        {
            inboundEndpoint.getProperties().put(MuleProperties.MULE_CREDENTIALS_PROPERTY, message.getProperty(MuleProperties.MULE_CREDENTIALS_PROPERTY));
        }
        return getLocalConnector().request(inboundEndpoint, timeout);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }
        if (!super.equals(o))
        {
            return false;
        }

        DynamicRequestEndpoint that = (DynamicRequestEndpoint) o;

        if (uri != null ? !uri.equals(that.uri) : that.uri != null)
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = 0;
        result = 31 * result + (uri != null ? uri.hashCode() : 0);
        return result;
    }
}