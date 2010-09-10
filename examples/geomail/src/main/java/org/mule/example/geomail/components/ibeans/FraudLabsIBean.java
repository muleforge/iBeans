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

import org.ibeans.annotation.Call;
import org.ibeans.annotation.param.BodyParam;
import org.ibeans.annotation.param.UriParam;
import org.ibeans.api.CallException;

/**
 * TODO
 */

public interface FraudLabsIBean
{
    //02-J73Z-N42F
    @Call(uri = "http://ws.fraudlabs.com/ws.fraudlabs.com_non_ssl/ip2locationwebservice.asmx/IP2Location?LICENSE={license}")
    public String ipToLocation(@UriParam("license") String licenseKey, @BodyParam("IP") String ip) throws CallException;
}