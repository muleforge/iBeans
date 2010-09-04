/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.web.ajax;

import org.mule.module.annotationx.api.Receive;
import org.mule.module.annotationx.api.Send;

/**
 * TODO
 */
public class SendStringAjaxBean
{
    @Receive(uri = "vm://in")
    @Send(uri = "ajax:///test1")
    public String process(String data)
    {
        return data + " Received";
    }
}