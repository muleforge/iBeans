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

import java.io.InputStream;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.w3c.dom.Document;

/**
 * TODO
 */
public class JAXBTransformer
{

    @Transformer(sourceTypes = String.class)
    public Car toCar(Document doc, JAXBContext context) throws JAXBException
    {
        return (Car) context.createUnmarshaller().unmarshal(doc);
    }


    //NOTE the @MessagePAyload annotation is ignorred for transformer but we're just testing that that it doesn't break things
    @Transformer
    public Feature toFeature(@MessagePayload InputStream in, @ReceivedHeaders("*") Map headers, JAXBContext context) throws JAXBException
    {
        return (Feature) context.createUnmarshaller().unmarshal(in);
    }

}
