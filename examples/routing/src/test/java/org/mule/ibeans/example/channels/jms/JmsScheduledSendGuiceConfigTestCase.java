/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.example.channels.jms;

import org.mule.ibeans.module.guice.GuiceIBeansTestSupport;

import com.google.inject.Module;

import java.util.List;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Test using a Jms Channel configured using a Guice module
 */
public class JmsScheduledSendGuiceConfigTestCase extends GuiceIBeansTestSupport
{
    @Override
    protected void addModules(List<Module> modules)
    {
        modules.add(new DummyGuiceConfigModule());
    }

    @Test
    public void jmsSend() throws Exception
    {
        String result = iBeansContext.receive("jms-publish", String.class, 2000);
        assertNotNull(result);
        assertEquals("New Message 1", result);

        result = iBeansContext.receive("jms-publish", String.class, 2000);
        assertNotNull(result);
        assertEquals("New Message 2", result);

        result = iBeansContext.receive("jms-publish", String.class, 2000);
        assertNotNull(result);
        assertEquals("New Message 3", result);
    }
}