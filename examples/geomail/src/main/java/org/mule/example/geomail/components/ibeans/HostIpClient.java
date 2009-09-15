/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.example.geomail.components.ibeans;

import org.mule.ibeans.api.client.Call;
import org.mule.ibeans.api.client.CallException;
import org.mule.ibeans.api.client.params.PayloadParam;

/**
 * TODO
 */

public interface HostIpClient
{
    @Call(uri = "http://api.hostip.info", properties = "http.method=GET")
    public String getHostInfo(@PayloadParam("ip") String ip) throws CallException;
}
