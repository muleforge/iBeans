/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.util.scan.annotations;

import org.mule.module.annotationx.api.Receive;
import org.mule.module.annotationx.api.Send;

import java.io.Serializable;

public class SampleBeanWithAnnotations implements Serializable
{

    @Receive(uri = "vm://foo")
    @Send(uri = "vm://bar")
    public String doSomething(Object data)
    {
        return null;
    }
}
