/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.example.geomail.components;

import org.mule.api.annotations.param.InboundHeaders;
import org.mule.api.annotations.param.Payload;
import org.mule.api.transformer.Transformer;
import org.mule.api.transformer.TransformerException;
import org.mule.example.geomail.components.ibeans.FraudLabsIBean;
import org.mule.example.geomail.dao.Sender;
import org.mule.example.geomail.dao.SenderDao;
import org.mule.ibeans.hostip.HostipIBean;
import org.mule.module.annotationx.api.Receive;
import org.mule.module.annotationx.api.Send;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ibeans.annotation.IntegrationBean;
import org.ibeans.api.CallException;


/**
 * Receives an IP address and processes it. The service will check the database to see if the IP address has already been processed
 * if not it will use the {@link org.mule.ibeans.hostip.HostipIBean} to look up information including Geo coordinates for the IP
 * address and save the result to the database.
 */
public class LookupService
{
    /**
     * logger used by this class
     */
    protected transient final Log logger = LogFactory.getLog(LookupService.class);

    @Inject
    private SenderDao senderDao;

    @Inject
    @Named("HostIpToSender")
    private Transformer hostIpToSender;

    @Inject
    @Named("IpLocationToSender")
    private Transformer ipLocationToSender;

    @Inject(optional = true)
    @Named("fraudlabs.api.key")
    private String fraudLabsKey;

    @IntegrationBean
    private HostipIBean hostIp;

    @IntegrationBean
    private FraudLabsIBean fraudLabs;


    @Receive(uri = "vm://lookup")
    @Send(uri = "vm://summary", split = "default")
    public List<Sender> fetchSenderInfo(@Payload List<String> ipAddresses, @InboundHeaders("from.email.address") String address) throws Exception
    {
        List<Sender> senders = new ArrayList<Sender>();
        for (String ip : ipAddresses)
        {
            //Check the database to see if we have resolved the IP address already
            Sender sender = senderDao.getSender(ip);
            if (sender == null)
            {
                //Lets try the free lookup service first
                sender = geoLookupFromHostIp(ip);
                if(sender==null)
                {
                    sender = geoLookupFromFraudLabs(ip);
                }
                if(sender==null)
                {
                    //Create an empty object that we will store with this IP as not to look it up again
                    sender = new Sender();
                }

                sender.setIp(ip);
                sender.setEmail(address);
            }
            senders.add(sender);
        }
        return senders;

    }

    protected Sender geoLookupFromHostIp(String ip) throws TransformerException
    {
        try
        {
            String hostInfo = hostIp.getHostInfo(ip);
            return (Sender) hostIpToSender.transform(hostInfo);
        }
        catch (CallException e)
        {
            logger.warn("Geo Location information not found for ip: " + ip + " using hostip.info");
            return null;
        }
    }

    protected Sender geoLookupFromFraudLabs(String ip) throws TransformerException
    {
        if(fraudLabsKey==null)
        {
            logger.info("No API key for FraudLabs set");
            return null;
        }
        try
        {
            String hostInfo = fraudLabs.ipToLocation(fraudLabsKey, ip);
            return (Sender) ipLocationToSender.transform(hostInfo);
        }
        catch (CallException e)
        {
            logger.warn("Geo Location information not found for ip: " + ip + " using FraudLabs");
            return null;
        }
    }
}
