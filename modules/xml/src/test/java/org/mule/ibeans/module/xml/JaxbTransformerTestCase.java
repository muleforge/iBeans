/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.module.xml;

import org.mule.DefaultMuleMessage;
import org.mule.api.MuleMessage;
import org.mule.ibeans.module.xml.model.EmailAddress;
import org.mule.ibeans.module.xml.model.Person;
import org.mule.ibeans.test.AbstractIBeansTestCase;
import org.mule.transformer.types.ListDataType;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JaxbTransformerTestCase extends AbstractIBeansTestCase
{
    public static final String PERSON_XML = "<person><name>John Doe</name><dob>01/01/1970</dob><emailAddresses><emailAddress><type>home</type><address>john.doe@gmail.com</address></emailAddress><emailAddress><type>work</type><address>jdoe@bigco.com</address></emailAddress></emailAddresses></person>";

    @Override
    protected void doSetUp() throws Exception
    {
        registerBeans(new JAXBTransformer());
    }

    public void testCustomTransform() throws Exception
    {
        Person person = iBeansContext.transform(PERSON_XML, Person.class);
        assertNotNull(person);
        assertEquals("John Doe", person.getName());
        assertEquals("01/01/1970", person.getDob());
        assertEquals(2, person.getEmailAddresses().size());
        assertEquals("home", person.getEmailAddresses().get(0).getType());
        assertEquals("john.doe@gmail.com", person.getEmailAddresses().get(0).getAddress());
        assertEquals("work", person.getEmailAddresses().get(1).getType());
        assertEquals("jdoe@bigco.com", person.getEmailAddresses().get(1).getAddress());
    }

    public void testCustomTransformWithMuleMessage() throws Exception
    {
        ByteArrayInputStream in = new ByteArrayInputStream(PERSON_XML.getBytes());
        Map props = new HashMap();
        props.put("foo", "fooValue");
        MuleMessage msg = new DefaultMuleMessage(in, props, muleContext);
        List<EmailAddress> emailAddresses = iBeansContext.transform(msg, new ListDataType<List<EmailAddress>>(EmailAddress.class));
        assertNotNull(emailAddresses);
        assertEquals(2, emailAddresses.size());
        assertEquals("home", emailAddresses.get(0).getType());
        assertEquals("john.doe@gmail.com", emailAddresses.get(0).getAddress());
        assertEquals("work", emailAddresses.get(1).getType());
        assertEquals("jdoe@bigco.com", emailAddresses.get(1).getAddress());
    }
}