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

import org.mule.api.transformer.Transformer;
import org.mule.example.geomail.components.ibeans.FraudLabsClient;
import org.mule.example.geomail.dao.Sender;
import org.mule.example.geomail.dao.SenderDao;
import org.mule.ibeans.api.application.Receive;
import org.mule.ibeans.api.application.Send;
import org.mule.ibeans.api.client.IntegrationBean;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * TODO
 */
public class BackupLookupService
{
    @Inject
    private Logger logger;

    @Inject
    private SenderDao senderDao;

    @Inject
    private
    @Named("IPLocationToSender")
    Transformer ipLocationToSender;

    //This is our back IP to Location service
    @IntegrationBean
    private FraudLabsClient fraudlabsClient;

    private String FraudLabsKey;

    @Receive(uri = "vm://channels/validator")
    @Send(uri = "vm://channels/positions/validator", split = "default")
    public List<Sender> fetchSenderInfo(List<Sender> senders) throws Exception
    {
        List<Sender> validSenders = new ArrayList<Sender>();
        for (Sender sender : senders)
        {
            if (sender.getLongitude() == null)
            {
                String info = fraudlabsClient.ipToLocation(getFraudLabsKey(), sender.getIp());
                validSenders.add((Sender) ipLocationToSender.transform(info));

                if (sender.getLongitude() == null)
                {
                    logger.log(Level.WARNING, "Could not look up location details for IP address: " + sender.getIp());
                }
                else
                {
                    senderDao.addSender(sender);
                    validSenders.add(sender);
                }
            }
            else
            {
                validSenders.add(sender);
            }
        }
        return validSenders;
    }

    public String getFraudLabsKey()
    {
        return FraudLabsKey;
    }

    public void setFraudLabsKey(@Named("fraudlabs-api-key") String fraudLabsKey)
    {
        FraudLabsKey = fraudLabsKey;
    }
}
