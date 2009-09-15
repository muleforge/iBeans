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

import org.mule.example.geomail.dao.Sender;
import org.mule.example.geomail.dao.SenderDao;
import org.mule.ibeans.api.application.Receive;
import org.mule.ibeans.api.application.Send;
import org.mule.ibeans.api.application.params.ReceivedHeaders;

import com.google.inject.Inject;

import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * TODO
 */
public class Storage
{

    private static final Logger log = Logger.getLogger(Storage.class.getName());

    @Inject
    private SenderDao senderDao;

    @Receive(uri = "vm://channels/positions/storage")
    @Send(uri = "vm://channels/positions/validate")
    public Sender store(Sender sender, @ReceivedHeaders("*") Map headers) throws Exception
    {

//        System.err.println("GroupSize: " + message.getCorrelationGroupSize());
//        System.err.println("Correlation ID: " + message.getCorrelationId());
//        System.err.println("Sequence Number: " + message.getCorrelationSequence());

        for (Iterator it = headers.keySet().iterator(); it.hasNext();)
        {
            String key = (String) it.next();
            System.err.println(key + " = " + headers.get(key));
        }

        String ip = (String) headers.get("ip");
        if (ip == null)
        {
            throw new IllegalStateException("'ip' property should have been set on UMOMessage.");
        }

        String from = (String) headers.get("from.email.address");
        /*
        if (from == null) {
            throw new IllegalStateException("'from.email.address' property should have been set on UMOMessage.");
        }*/

        sender.setIp(ip);
        sender.setEmail(from);

        if (getSenderDao().getSender(sender.getIp()) != null)
        {
            log.warn("Sender '" + sender + "' should not be in the Database.");
        }
        else
        {
            getSenderDao().addSender(sender);
            log.warn("Sender '" + sender + "' successfully added to the Database.");
        }
//
//        MuleMessage resultMessage = new DefaultMuleMessage(sender, context.getMuleContext());
//        resultMessage.setCorrelationGroupSize(message.getCorrelationGroupSize());
//        resultMessage.setCorrelationId(message.getCorrelationId());
//        resultMessage.setCorrelationSequence(message.getCorrelationSequence());
//
//        return resultMessage;
        return sender;
    }

    public SenderDao getSenderDao()
    {
        return senderDao;
    }

    public void setSenderDao(SenderDao senderDao)
    {
        this.senderDao = senderDao;
    }
}
