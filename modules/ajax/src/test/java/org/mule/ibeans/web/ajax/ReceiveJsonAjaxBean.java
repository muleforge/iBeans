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

import org.mule.ibeans.api.application.Receive;
import org.mule.ibeans.api.application.Send;

/**
 * TODO
 */
public class ReceiveJsonAjaxBean
{
    @Receive(uri = "ajax:///test2")
    @Send(uri = "vm://out")
    public String process(JsonBean bean)
    {
        return bean.toString();
    }
}
