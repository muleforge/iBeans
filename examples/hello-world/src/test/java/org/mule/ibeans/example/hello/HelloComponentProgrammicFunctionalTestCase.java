/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.example.hello;

import org.mule.api.MuleException;
import org.mule.ibeans.test.AbstractIBeansTestCase;
import org.mule.util.IOUtils;

import java.io.IOException;
import java.util.Locale;
import java.util.Properties;

/**
 * Tests registering an annotated component with the registry programmatically and using
 * property placeholders in the annotations
 */
public class HelloComponentProgrammicFunctionalTestCase extends AbstractIBeansTestCase
{
    private AnnotatedHelloComponent hello;

    @Override
    protected Properties getStartUpProperties()
    {
        //these will be made available at start up
        Properties p = new Properties();
        try
        {
            p.load(IOUtils.getResourceAsStream("hello-annotation-test.properties", getClass()));
            return p;
        }
        catch (IOException e)
        {
            fail(e.getMessage());
            return null;
        }
    }

    protected void doSetUp() throws Exception
    {
        hello = new AnnotatedHelloComponent();
        //Note we are using the english locale
        hello.setMessageLocale(Locale.ENGLISH);
        hello.setName("helloService");

        registerBeans(hello);
        registerBeans(new AnnotatedGreetingComponent());
    }

    public void testHelloComponent() throws Exception
    {

        String message = iBeansContext.request("helloEndpoint", String.class, "Ross");
        assertNotNull(message);
        assertEquals("Good day to you Ross", message);

        //Lets unregister this service and re-register with a different Locale
        //muleContext.getRegistry().unregisterObject("helloService");
        iBeansContext.unregisterApplicationIBean(AnnotatedHelloComponent.class.getName());

        try
        {
            iBeansContext.request("helloEndpoint", "Ross");
            fail("The hello endpoint should no longer be available");
        }
        catch (MuleException e)
        {
            //Expected
        }


        hello.setMessageLocale(Locale.GERMAN);

        iBeansContext.registerApplicationIBean("helloService", hello);
        message = iBeansContext.request("helloEndpoint", String.class, "Ross");

        assertNotNull(message);
        assertEquals("Guten tag Ross", message);
    }
}