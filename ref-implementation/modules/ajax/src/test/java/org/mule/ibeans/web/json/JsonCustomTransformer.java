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

import org.mule.api.annotations.Transformer;
import org.mule.api.annotations.param.InboundHeaders;
import org.mule.api.annotations.param.Payload;
import org.mule.ibeans.web.json.model.EmailAddress;
import org.mule.ibeans.web.json.model.Item;
import org.mule.ibeans.web.json.model.Person;

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
    public Person toCar(byte[] doc, ObjectMapper context) throws IOException
    {
        return context.readValue(doc, 0, doc.length, Person.class);
    }


    //NOTE the @Body annotation is ignored for transformer but we're just testing that that it doesn't break things
    @Transformer
    public EmailAddress toEmail(@Payload InputStream in, @InboundHeaders("*") Map headers, ObjectMapper mapper) throws IOException
    {
        if(!headers.get("foo").equals("fooValue"))
        {
            throw new IllegalArgumentException("Header foo not set to 'fooValue'");
        }
        return mapper.readValue(in, EmailAddress.class);
    }


    @Transformer(sourceTypes = {InputStream.class})
    public List<Item> toItemList(@Payload String in, ObjectMapper mapper) throws IOException
    {
        List<Item> items = new ArrayList<Item>();
        ArrayNode nodes = (ArrayNode) mapper.readTree(in);
        for (Iterator<JsonNode> iterator = nodes.getElements(); iterator.hasNext();)
        {
            //TODO, we're reparsing content here
            items.add(mapper.readValue(iterator.next().toString(), Item.class));
        }

        return items;
    }

    @Transformer(sourceTypes = {InputStream.class})
    public List<Person> toPeople(@Payload String in, ObjectMapper mapper) throws IOException
    {
        List<Person> people = new ArrayList<Person>();
        ArrayNode nodes = (ArrayNode) mapper.readTree(in);
        for (Iterator<JsonNode> iterator = nodes.getElements(); iterator.hasNext();)
        {
            //TODO, we're reparsing content here
             people.add(mapper.readValue(iterator.next().toString(), Person.class));
        }

        return people;
    }
}