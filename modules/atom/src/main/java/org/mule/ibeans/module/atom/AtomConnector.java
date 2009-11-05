/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.module.atom;

import org.mule.api.DefaultMuleException;
import org.mule.api.MuleException;
import org.mule.api.endpoint.ImmutableEndpoint;
import org.mule.api.routing.filter.Filter;
import org.mule.api.service.Service;
import org.mule.ibeans.channels.ATOM;
import org.mule.ibeans.internal.ext.ServiceCallback;
import org.mule.transport.abdera.InboundFeedSplitter;
import org.mule.transport.abdera.filters.EntryLastUpdatedFilter;
import org.mule.transport.http.HttpConnector;
import org.mule.util.MapUtils;
import org.mule.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * TODO
 */
public class AtomConnector extends HttpConnector implements ServiceCallback
{
    public static final String PROTOCOL = "atom";

    public static final String DATE_FORMAT = "yyyy-MM-dd hh:mm:ss";
    public static final String SHORT_DATE_FORMAT = "yyyy-MM-dd";

    private boolean splitFeed = true;

    private Date lastUpdate = null;

    private List<String> acceptedContentTypes;

    private final SimpleDateFormat dateFormatter = new SimpleDateFormat(DATE_FORMAT);
    private final SimpleDateFormat shortDateFormatter = new SimpleDateFormat(SHORT_DATE_FORMAT);

    public AtomConnector()
    {
        registerSupportedProtocol("http");
        acceptedContentTypes = new ArrayList<String>();
        acceptedContentTypes.add("application/atom+xml");
        acceptedContentTypes.add("text/xml");
    }

    public boolean isSplitFeed()
    {
        return splitFeed;
    }

    public void setSplitFeed(boolean splitFeed)
    {
        this.splitFeed = splitFeed;
    }

    public Date getLastUpdate()
    {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate)
    {
        this.lastUpdate = lastUpdate;
    }

    @Override
    public String getProtocol()
    {
        return PROTOCOL;
    }

    public List<String> getAcceptedContentTypes()
    {
        return acceptedContentTypes;
    }

    public void setAcceptedContentTypes(List<String> acceptedContentTypes)
    {
        this.acceptedContentTypes = acceptedContentTypes;
    }

    public void process(Service service, ImmutableEndpoint endpoint) throws MuleException
    {
        String lastUpdate = (String) endpoint.getProperty(ATOM.LAST_UPDATE_DATE);

        Date lastUpdateDate;
        if (StringUtils.isNotBlank(lastUpdate))
        {
            try
            {
                if (lastUpdate.length() == 10)
                {
                    lastUpdateDate = shortDateFormatter.parse(lastUpdate);
                }
                else
                {
                    lastUpdateDate = dateFormatter.parse(lastUpdate);
                }
            }
            catch (ParseException e)
            {
                throw new DefaultMuleException(e);
            }
        }
        else
        {
            lastUpdateDate = getLastUpdate();
        }

        boolean splitFeed = MapUtils.getBooleanValue(endpoint.getProperties(), "splitFeed", isSplitFeed());
        if (splitFeed)
        {
            Filter filter = new EntryLastUpdatedFilter(lastUpdateDate);
            InboundFeedSplitter splitter = new InboundFeedSplitter();
            splitter.setMuleContext(getMuleContext());
            splitter.setEntryFilter(filter);
            splitter.setAcceptedContentTypes(acceptedContentTypes);
            splitter.initialise();
            service.getInboundRouter().addRouter(splitter);
        }
    }
}
