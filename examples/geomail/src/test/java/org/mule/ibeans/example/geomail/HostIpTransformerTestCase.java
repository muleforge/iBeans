/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.example.geomail;

import org.mule.api.transformer.Transformer;
import org.mule.example.geomail.dao.Sender;
import org.mule.ibeans.test.IBeansRITestSupport;
import org.mule.module.xml.transformer.XmlToObject;
import org.mule.module.xml.transformer.XsltTransformer;
import org.mule.transformer.TransformerChain;
import org.mule.transformer.types.DataTypeFactory;
import org.mule.util.IOUtils;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

public class HostIpTransformerTestCase extends IBeansRITestSupport
{
    Transformer hostIpToSender;

    @Before
    public void init() throws Exception
    {
        XmlToObject xmlToObject = new XmlToObject();
        xmlToObject.addAlias("sender", Sender.class);
        xmlToObject.setReturnDataType(new DataTypeFactory().create(Sender.class));
        XsltTransformer hostXslt = new XsltTransformer("xsl/HostIpToSender.xsl");
        hostIpToSender = new TransformerChain(hostXslt, xmlToObject);
        hostIpToSender.setMuleContext(muleContext);
        hostIpToSender.initialise();
    }

    @Test
    public void createNotFoundSender() throws Exception
    {
        String response = IOUtils.getResourceAsString("hostip-not-found-response.xml", getClass());
        assertNotNull(response);

        Sender sender = (Sender)hostIpToSender.transform(response);
        assertNotNull(sender);
        assertNull(sender.getIp());
    }

    @Test
    public void createFoundSender() throws Exception
    {
        String response = IOUtils.getResourceAsString("hostip-found-response.xml", getClass());
        assertNotNull(response);

        Sender sender = (Sender)hostIpToSender.transform(response);
        assertNotNull(sender);
        assertNotNull(sender.getIp());
        assertEquals("-88.4588", sender.getLongitude());
        assertEquals("41.7696", sender.getLatitude());
    }
}
