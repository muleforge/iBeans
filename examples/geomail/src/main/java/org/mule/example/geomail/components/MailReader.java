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

import org.mule.api.DefaultMuleException;
import org.mule.api.annotations.param.OutboundHeaders;
import org.mule.module.annotationx.api.Send;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Singleton;
import javax.mail.Message;

import org.apache.log4j.Logger;

/**
 *  This component receives email and sends out the trail of IP-addresses as individual messages.
 * In order to use this service, the user must supply mailbox credentials to connect to.  The mail box cannot be a
 * public hosted mail box. Additionally, this component should be configured in the {@link org.mule.example.geomail.config.GeoMailModule}
 * instead of the {@link org.mule.example.geomail.components.DataGenerator} component.
 */
@Singleton
public class MailReader
{

    private static final Logger log = Logger.getLogger(MailReader.class.getName());

    // TODO add your email server here, note that Gmail, Yahoo, etc cannot be used since they do not set the sender ip addrsss -->
    //@Receive(uri="imaps://user:pass@imap.mycompany.com")
    @Send(uri = "vm://lookup")
    public List<String> receiveEmail(Message mail, @OutboundHeaders Map sendHeaders) throws Exception
    {
        String from = mail.getFrom()[0].toString();
        String[] received = mail.getHeader("Received");

        List<String> ipAddresses = new ArrayList<String>();

        for (int i = received.length - 1; i >= 0; i--)
        {

            ReceivedHeader receivedHeader = ReceivedHeader.getInstance(received[i]);
            if (receivedHeader != null && receivedHeader.getFrom() != null)
            {
                if (!receivedHeader.getFrom().startsWith("localhost") && !receivedHeader.getFrom().startsWith("127.0.0.1"))
                { // Test
                    String ip = getFromIP(receivedHeader);

                    if (ip != null)
                    {
                        ipAddresses.add(ip);
                    }
                }
            }

        }

        if (ipAddresses.isEmpty())
        {
            throw new DefaultMuleException("Received e-mail does not provide sender IP information.");
        }

        sendHeaders.put("from.email.address", from);

        return ipAddresses;
    }

    private String getFromIP(ReceivedHeader receivedHeader)
    {

        String result = null;

        Matcher matcher = Pattern.compile(".*\\(.*\\[(.*?)\\]\\)", Pattern.DOTALL).matcher(receivedHeader.getFrom());
        if (matcher.matches())
        {
            result = matcher.group(1);
        }

        return result;
    }


}
