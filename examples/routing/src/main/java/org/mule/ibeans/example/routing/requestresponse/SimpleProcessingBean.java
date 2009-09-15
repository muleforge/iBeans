/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.example.routing.requestresponse;

import org.mule.ibeans.api.application.ReceiveAndReply;
import org.mule.ibeans.api.application.params.MessagePayload;

import org.dom4j.Document;

/**
 * A bean the receives and replies on an URI. The {@link org.mule.ibeans.api.application.params.MessagePayload} parameter annotation
 * is used so that Mule will automatically convert the incoming message to a Dom object and the bean itself will set a value in
 * the XML document before returning.
 */
public class SimpleProcessingBean
{
    @ReceiveAndReply(uri = "vm://test")
    public Document processXml(@MessagePayload Document doc)
    {
        doc.selectSingleNode("/foo/bar").setText("hello");
        return doc;
    }
}
