/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.example.routing.pubsub;

import org.mule.ibeans.test.IBeansRITestSupport;
import org.mule.tck.testmodels.fruit.Apple;

import org.ibeans.api.IBeansException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests registering an annotated component with the registry programmatically and using
 * property placeholders in the annotations
 */
public class ScheduledReceiveAndSendFunctionalTestCase extends IBeansRITestSupport
{
    @Before
    public void init() throws IBeansException
    {
        registerBeans(new ScheduledSendBean());
    }

    @Test
    public void schedulePublish() throws Exception
    {
        Apple result = iBeansContext.receive("vm://apples", Apple.class, 3000);
        assertNotNull(result);
        assertTrue(result.isWashed());
    }
}
