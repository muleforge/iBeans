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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;

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
    public Feature toFeature(@MessagePayload InputStream in, @ReceivedHeaders("*") Map headers, ObjectMapper mapper) throws IOException
    {
        return mapper.readValue(in, Feature.class);
    }


    @Transformer(sourceTypes = {InputStream.class})
    public List<Feature> toFeatures(@MessagePayload String in, ObjectMapper mapper) throws IOException
    {
        List<Feature> features = new ArrayList<Feature>();
        ArrayNode nodes = (ArrayNode) mapper.readTree(in);
        for (Iterator<JsonNode> iterator = nodes.getElements(); iterator.hasNext();)
        {
            //TODO, we're reparsing content here
            features.add(mapper.readValue(iterator.next().toString(), Feature.class));
        }

        return features;
    }

    @Transformer(sourceTypes = {InputStream.class})
    public List<Car> toCars(@MessagePayload String in, ObjectMapper mapper) throws IOException
    {
        List<Car> cars = new ArrayList<Car>();
        ArrayNode nodes = (ArrayNode) mapper.readTree(in);
        for (Iterator<JsonNode> iterator = nodes.getElements(); iterator.hasNext();)
        {
            //TODO, we're reparsing content here
             cars.add(mapper.readValue(iterator.next().toString(), Car.class));
        }

        return cars;
    }
}