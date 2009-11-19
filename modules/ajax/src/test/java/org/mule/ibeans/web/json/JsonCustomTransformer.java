/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.web.json;

import org.mule.ibeans.api.application.Transformer;
import org.mule.ibeans.api.application.params.MessagePayload;
import org.mule.ibeans.api.application.params.ReceivedHeaders;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;

/**
 * TODO
 */
public class JsonCustomTransformer
{
    //This is used to test other source types and injecting an ObjectMapper instance
    @Transformer(sourceTypes = String.class)
    public Car toCar(byte[] doc, ObjectMapper context) throws IOException
    {
        return context.readValue(doc, 0, doc.length, Car.class);
    }


    //NOTE the @MessagePayload annotation is ignorred for transformer but we're just testing that that it doesn't break things
    @Transformer
    public Feature toFeature(@MessagePayload InputStream in, @ReceivedHeaders("*") Map headers, ObjectMapper context) throws IOException
    {
        return context.readValue(in, Feature.class);
    }


}