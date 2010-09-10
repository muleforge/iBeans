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

import org.mule.api.annotations.Schedule;
import org.mule.api.annotations.param.OutboundHeaders;
import org.mule.module.annotationx.api.Send;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.inject.Singleton;

/**
 * generates random IP addresses to simulate the {@link org.mule.example.geomail.components.MailReader} component,which
 * listens on a mail box for incoming mail and retrieves IP addresses from the emails themselves.
 *
 * Since we are generating random addresses we create 10 at a time since most will not be valid/know ip addresses
 */
@Singleton
public class DataGenerator
{
    private Random generator = new Random();

    private int batchSize = 10;

    @Schedule(interval = 4000)
    @Send(uri = "vm://lookup")
    public List<String> createDummyEmails(@OutboundHeaders Map sendHeaders) throws Exception
    {
        sendHeaders.put("from.email.address", "testdatagenerator@geomail.com");

        List<String> ipAddresses = new ArrayList<String>(batchSize);
        for (int i = 0; i < batchSize; i++)
        {
            String address = new StringBuffer().append(generator.nextInt(255)).append(".").append(generator.nextInt(255))
                    .append(".").append(generator.nextInt(255)).append(".").append(generator.nextInt(255)).toString();
            ipAddresses.add(address);
        }
        return ipAddresses;
    }

    public int getBatchSize()
    {
        return batchSize;
    }

    public void setBatchSize(int batchSize)
    {
        this.batchSize = batchSize;
    }
}
