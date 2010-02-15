/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.mock;

import org.mule.ibeans.api.client.Call;
import org.mule.ibeans.api.client.CallException;
import org.mule.ibeans.api.client.State;
import org.mule.ibeans.api.client.Template;
import org.mule.ibeans.api.client.Usage;
import org.mule.ibeans.api.client.filters.ExpressionErrorFilter;
import org.mule.ibeans.api.client.params.ReturnType;
import org.mule.ibeans.api.client.params.UriParam;
import org.mule.ibeans.channels.MimeTypes;

@Usage("Simply pass in the ip address that you want to resolve and an XML document " +
        "is returned with the geo locations. The format can be found here: " +
        "http://api.hostip.info/?ip=12.215.42.19")
//using regex error filter because the core cannot depend on the XML module
@ExpressionErrorFilter(eval = "regex", expr = "Co-ordinates are unavailable", mimeType = MimeTypes.XML)
public interface HostipIBean
{
    @ReturnType
    public static final Class DEFAULT_RETURN_TYPE = String.class;

    @State
    void init(@ReturnType Class returnType);

    @Call(uri = "http://api.hostip.info?ip={ip}")
    public <T> T getHostInfo(@UriParam("ip") String ip) throws CallException;

    @Template("one two {number}")
    public String dummyTemplateMethod(@UriParam("number") String number);
}
