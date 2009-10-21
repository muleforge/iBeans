/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans;

import org.mule.ibeans.api.client.Call;
import org.mule.ibeans.api.client.CallException;
import org.mule.ibeans.api.client.ExceptionListenerAware;
import org.mule.ibeans.api.client.params.HeaderParam;
import org.mule.ibeans.api.client.params.ParamFactory;

public interface TestImplicitPropertiesinFactoryIBean extends ExceptionListenerAware
{
    @HeaderParam(value = "Authorization")
    ParamFactory s3SignatureEvaluator = new CheckHTTPPropertiesFactory();

    @Call(uri = "http://s3.amazonaws.com/")
    public Object doStuff() throws CallException;
}