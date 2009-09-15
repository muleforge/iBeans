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

import org.mule.ibeans.api.application.Schedule;
import org.mule.ibeans.api.application.Send;
import org.mule.ibeans.api.application.params.SendHeaders;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * TODO
 */
public class DataGenerator
{
    private Random generator = new Random();

    private int batchSize = 10;

    @Schedule(interval = 2000)
    @Send(uri = "vm://channels/gatekeeper")
    public List<String> createDummyEmails(@SendHeaders Map sendHeaders) throws Exception
    {
        sendHeaders.put("from.email.address", "testdatagenerator@geomail.com");

        //Create 3 for each run since many addresses will not be valid
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
