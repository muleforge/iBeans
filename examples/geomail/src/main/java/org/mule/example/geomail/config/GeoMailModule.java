/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.example.geomail.config;

import org.mule.api.transformer.Transformer;
import org.mule.api.transformer.TransformerException;
import org.mule.example.geomail.components.DataGenerator;
import org.mule.example.geomail.components.LookupService;
import org.mule.example.geomail.components.SummaryService;
import org.mule.example.geomail.dao.Sender;
import org.mule.example.geomail.dao.SenderDao;
import org.mule.example.geomail.dao.impl.SenderDaoImpl;
import org.mule.module.guice.AbstractMuleGuiceModule;
import org.mule.module.xml.transformer.XmlToObject;
import org.mule.module.xml.transformer.XsltTransformer;
import org.mule.transformer.TransformerChain;
import org.mule.transformer.types.DataTypeFactory;

import com.google.inject.name.Names;
import com.wideplay.warp.persist.jpa.JpaUnit;

/**
 * TODO
 */
public class GeoMailModule extends AbstractMuleGuiceModule
{
    protected void configure()
    {
        //Bind the persistence unit with this modules
        bindConstant().annotatedWith(JpaUnit.class).to("geomail-persistence-unit");

        //This is our accessor object
        bind(SenderDao.class).to(SenderDaoImpl.class);

        //Xml to object serialization using XStream
        XmlToObject xmlToObject = new XmlToObject();
        xmlToObject.addAlias("sender", Sender.class);
        
        //Unfortunately we need this work around to ensure that the Sender.class can be found at runtime
        try
        {
            xmlToObject.getXStream().setClassLoader(getClass().getClassLoader());
        }
        catch (TransformerException e)
        {
            throw new RuntimeException(e);
        }
        xmlToObject.setReturnDataType(DataTypeFactory.create(Sender.class));

        XsltTransformer hostXslt = new XsltTransformer("xsl/HostIpToSender.xsl");
        XsltTransformer ipLocationXslt = new XsltTransformer("xsl/IP2LocationToSender.xsl");

        //Transformers for converting from service Xml to Send objects
        Transformer hostIpToSender = new TransformerChain(hostXslt, xmlToObject);
        Transformer ipLocationToSender = new TransformerChain(ipLocationXslt, xmlToObject);

        //Bind these transformer chains
        bind(Transformer.class).annotatedWith(Names.named("HostIpToSender")).toInstance(hostIpToSender);
        bind(Transformer.class).annotatedWith(Names.named("IpLocationToSender")).toInstance(ipLocationToSender);

        bindConstant().annotatedWith(Names.named("flickr.api.key")).to("3a690a103c6eabf55de5b10623021a34");
        
        //Now add our service objects

        //THis creates fake ip addresses to Map, good for testing
        bind(DataGenerator.class).asEagerSingleton();

        //If you have a real email server (not Gmail or Yahoo since they don't set the IP headers) you can use this
        //bind(MailReader.class).asEagerSingleton();

        //Performs the GEO lookup for the IP address
        bind(LookupService.class).asEagerSingleton();
        
        //Validates the sender data before publishing it to the browser
        bind(SummaryService.class).asEagerSingleton();
    }
}
