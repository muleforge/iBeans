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
import org.mule.example.geomail.components.ibeans.HostIpClient;
import org.mule.example.geomail.dao.Sender;
import org.mule.example.geomail.dao.SenderDao;
import org.mule.ibeans.api.application.Receive;
import org.mule.ibeans.api.application.Send;
import org.mule.ibeans.api.client.IntegrationBean;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import java.util.ArrayList;
import java.util.List;


/**
 * TODO
 */
public class LookupService
{
    @Inject
    private SenderDao senderDao;

    @Inject
    private
    @Named("HostIpToSender")
    Transformer hostIpToSender;

    @IntegrationBean
    private HostIpClient hostIpClient;

    @Receive(uri = "vm://channels/gatekeeper")
    @Send(uri = "vm://channels/positions/storage", split = "default")
    public List<Sender> fetchSenderInfo(List<String> ipAddresses) throws Exception
    {
        List<Sender> senders = new ArrayList<Sender>();
        for (String address : ipAddresses)
        {
            //Check the database to see if we have resolved the IP address already
            Sender sender = senderDao.getSender(address);
            if (sender == null)
            {
                String hostInfo = hostIpClient.getHostInfo(address);
                sender = (Sender) hostIpToSender.transform(hostInfo);

                //Lets store this for next time
                senderDao.addSender(sender);

            }
            senders.add(sender);

        }
        return senders;

    }
}
