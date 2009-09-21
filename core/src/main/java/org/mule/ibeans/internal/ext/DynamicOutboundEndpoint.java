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

import org.mule.api.MuleEvent;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.api.endpoint.EndpointURI;
import org.mule.api.endpoint.OutboundEndpoint;
import org.mule.api.transport.Connector;
import org.mule.api.transport.DispatchException;
import org.mule.config.i18n.CoreMessages;
import org.mule.endpoint.DynamicURIOutboundEndpoint;
import org.mule.endpoint.MuleEndpointURI;
import org.mule.ibeans.internal.util.UriParamFilter;
import org.mule.transport.AbstractConnector;
import org.mule.transport.service.TransportFactory;
import org.mule.util.BeanUtils;
import org.mule.util.TemplateParser;
import org.mule.util.ClassUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * An Outbound endpoint who's URI will be constructed based on the current message. This allows for the destination of a messgae to change
 * based on the contents of the message.  Note that this endpoint ONLY substitues the URI, but other config elements such as
 * the connector (and scheme), transformers, filters, etc do not change.  You cannot change an endpoint scheme dynamically so you
 * can't switch between HTTP and JMS using the same dynamic endpoint.
 */
public class DynamicOutboundEndpoint extends DynamicURIOutboundEndpoint
{
    public static final String NULL_PARAM = "null.param";
    /**
     * logger used by this class
     */
    protected transient final Log logger = LogFactory.getLog(DynamicOutboundEndpoint.class);

    private static final long serialVersionUID = 8861985949279708638L;

    /**
     * THe URI template used to construct the actual URI to send the message to.
     */
    protected String uriTemplate;

    //Need a local read-write intance
    protected AbstractConnector localConnector;

    //The parser used to parse the uriTemplate
    protected TemplateParser parser = TemplateParser.createCurlyBracesStyleParser();

    private UriParamFilter filter = new UriParamFilter();


    public DynamicOutboundEndpoint(OutboundEndpoint endpoint, String uriTemplate)
    {
        super(endpoint);
        this.uriTemplate = uriTemplate;
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
            logger.debug("Uri before parsing is: " + uriTemplate);
        }

        Map<String, Object> props = getPropertiesForTemplate(message);

        String newUriString = parser.parse(props, uriTemplate);
        //Remove optional params completely if null
        newUriString = filter.filterParamsByValue(newUriString, NULL_PARAM);
        newUriString = this.getMuleContext().getExpressionManager().parse(newUriString, message, true);

        if (logger.isDebugEnabled())
        {
            logger.debug("Uri after parsing is: " + newUriString);
        }


        try
        {
            MuleEndpointURI uri = new MuleEndpointURI(newUriString, getMuleContext());

            setEndpointURI(uri);

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
                    CoreMessages.templateCausedMalformedEndpoint(uriTemplate, newUriString),
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
            e.printStackTrace();
            return null;
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

    public MuleMessage send(MuleEvent event) throws DispatchException
    {

        EndpointURI uri = getEndpointURIForMessage(event.getMessage());
        OutboundEndpoint outboundEndpoint = new DynamicURIOutboundEndpoint(this, uri);
        try
        {
            return getLocalConnector().send(outboundEndpoint, event);
        }
        catch (Exception e)
        {
            throw new DispatchException(event.getMessage(), event.getEndpoint(), e);
        }

    }

    public void dispatch(MuleEvent event) throws DispatchException
    {
        EndpointURI uri = getEndpointURIForMessage(event.getMessage());
        OutboundEndpoint outboundEndpoint = new DynamicURIOutboundEndpoint(this, uri);
        try
        {
            getLocalConnector().dispatch(outboundEndpoint, event);
        }
        catch (MuleException e)
        {
            throw new DispatchException(event.getMessage(), event.getEndpoint(), e);
        }
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (!(o instanceof DynamicOutboundEndpoint))
        {
            return false;
        }


        DynamicOutboundEndpoint that = (DynamicOutboundEndpoint) o;

        if (localConnector != null ? !localConnector.equals(that.localConnector) : that.localConnector != null)
        {
            return false;
        }
        if (uriTemplate != null ? !uriTemplate.equals(that.uriTemplate) : that.uriTemplate != null)
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = 0;
        result = 31 * result + (uriTemplate != null ? uriTemplate.hashCode() : 0);
        result = 31 * result + (localConnector != null ? localConnector.hashCode() : 0);
        return result;
    }
}
