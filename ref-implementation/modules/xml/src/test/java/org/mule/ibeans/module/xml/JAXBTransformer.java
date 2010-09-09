/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.module.xml;

import org.mule.ibeans.api.application.Transformer;
import org.mule.ibeans.api.application.params.MessagePayload;
import org.mule.ibeans.api.application.params.ReceivedHeaders;
import org.mule.ibeans.module.xml.model.EmailAddress;
import org.mule.ibeans.module.xml.model.Person;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import static org.mule.ibeans.IBeansSupport.select;

/**
 * Explict JAXB transformers used to test that JAXB transforms can be intercepted
 */
public class JAXBTransformer
{

    @Transformer(sourceTypes = {String.class})
    public Person toPerson(Document doc, JAXBContext context) throws JAXBException
    {
        return (Person) context.createUnmarshaller().unmarshal(doc);
    }


    //NOTE the @MessagePayload annotation is ignorred for transformer but we're just testing that that it doesn't break things
    @Transformer(sourceTypes = {String.class, InputStream.class})
    public List<EmailAddress> toEmailAddresses(@MessagePayload Document doc, @ReceivedHeaders("*") Map headers, JAXBContext context) throws JAXBException
    {
        //Test that we receive headers
        if(!headers.get("foo").equals("fooValue"))
        {
            throw new IllegalArgumentException("Header foo was not set to the correct value 'fooValue'");
        }
        
        List<Node> nodes = select("/person/emailAddresses/emailAddress", doc);
        List<EmailAddress> addrs = new ArrayList<EmailAddress>(nodes.size());
        for (Node node : nodes)
        {
            addrs.add(context.createUnmarshaller().unmarshal(node, EmailAddress.class).getValue());
        }
        return addrs;
    }

}
