/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.web.json;

import org.mule.api.MuleMessage;
import org.mule.ibeans.IBeansException;
import org.mule.ibeans.test.IBeansTestSupport;
import org.mule.ibeans.web.json.model.EmailAddress;
import org.mule.ibeans.web.json.model.Item;
import org.mule.ibeans.web.json.model.Person;
import org.mule.transformer.types.CollectionDataType;
import org.mule.transformer.types.ListDataType;
import org.mule.transformer.types.SimpleDataType;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class JsonCustomTransformerTestCase extends IBeansTestSupport
{
    public static final String PERSON_JSON = "{\"emailAddresses\":[{\"type\":\"home\",\"address\":\"john.doe@gmail.com\"},{\"type\":\"work\",\"address\":\"jdoe@bigco.com\"}],\"name\":\"John Doe\",\"dob\":\"01/01/1970\"}";
    public static final String EMAIL_JSON = "{\"type\":\"home\",\"address\":\"john.doe@gmail.com\"}";
    public static final String ITEMS_JSON = "[{\"code\":\"1234\",\"description\":\"Vacuum Cleaner\",\"in-stock\":true},{\"code\":\"1234-1\",\"description\":\"Cleaner Bag\",\"in-stock\":false}]";

    @Before
    public void init() throws IBeansException
    {
        registerBeans(new JsonCustomTransformer());
    }

    @Test
    public void customTransform() throws Exception
    {
        Person person = iBeansContext.transform(PERSON_JSON, Person.class);
        assertNotNull(person);
        assertEquals("John Doe", person.getName());
        assertEquals("01/01/1970", person.getDob());
        assertEquals(2, person.getEmailAddresses().size());
        assertEquals("home", person.getEmailAddresses().get(0).getType());
        assertEquals("john.doe@gmail.com", person.getEmailAddresses().get(0).getAddress());
        assertEquals("work", person.getEmailAddresses().get(1).getType());
        assertEquals("jdoe@bigco.com", person.getEmailAddresses().get(1).getAddress());
    }

    @Test
    public void customTransformWithMuleMessage() throws Exception
    {
        ByteArrayInputStream in = new ByteArrayInputStream(EMAIL_JSON.getBytes());
        Map<String, String> props = new HashMap<String, String>();
        props.put("foo", "fooValue");
        MuleMessage msg = createMuleMessage(in, props);
        EmailAddress emailAddress = iBeansContext.transform(msg, new SimpleDataType<EmailAddress>(EmailAddress.class));
        assertNotNull(emailAddress);
        assertEquals("home", emailAddress.getType());
        assertEquals("john.doe@gmail.com", emailAddress.getAddress());
    }

    @Test
    public void customListTransform() throws Exception
    {
        List<Item> items = iBeansContext.transform(ITEMS_JSON, new CollectionDataType<List<Item>>(List.class, Item.class));
        assertNotNull(items);
        assertEquals("1234", items.get(0).getCode());
        assertEquals("Vacuum Cleaner", items.get(0).getDescription());
        assertEquals("1234-1", items.get(1).getCode());
        assertEquals("Cleaner Bag", items.get(1).getDescription());

        //Call this transformer here to test that the cached transformer from the previous invocation does not interfer with
        //Finding the List<Person> transformer
        String people_json = "[" + PERSON_JSON + "," + PERSON_JSON + "]";
        List<Person> people = iBeansContext.transform(people_json, new CollectionDataType<List<Person>>(List.class, Person.class));
        assertNotNull(people);
        assertEquals(2, people.size());
    }

    @Test
    public void differentListTransformer() throws Exception
    {
        //Test that we can resolve other collections

        String cars_json = "[" + PERSON_JSON + "," + PERSON_JSON + "]";
        List<Person> people = iBeansContext.transform(cars_json, new ListDataType<List<Person>>(Person.class));
        assertNotNull(people);
        assertEquals(2, people.size());
    }
}