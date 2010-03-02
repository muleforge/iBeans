#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
/*
 * ${symbol_dollar}Id: EchoService.java 16404 2009-08-17 17:52:09Z ross.mason ${symbol_dollar}
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package ${package};

import org.mule.ibeans.api.application.ReceiveAndReply;

/**
 * A simple echo service
 */
public class EchoService
{
    @ReceiveAndReply(uri = "ajax:///ibeans/echo")
    public String echoThis(String data)
    {
        return data;
    }
}
