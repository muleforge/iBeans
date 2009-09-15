/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.example.routing.splitter;

import org.mule.ibeans.api.application.Receive;
import org.mule.ibeans.api.application.Send;

/**
 * This is a concept test case that shows an example of using a splitter annotation. Routing may be be wrapped in a DSL api.
 */
public class SplitterBridge
{
    @Receive(uri = "vm://in", id = "in")
    @Send(uri = "vm://out", id = "out", split = "#[xpath-node:/Batch/Trade]")
    //@Splitter(evaluator = "xpath", expression = "/Batch/Trade")
    public Object bridge(Object payload)
    {
        return payload;
    }
}
