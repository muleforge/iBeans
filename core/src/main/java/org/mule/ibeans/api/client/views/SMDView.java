/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.api.client.views;

import org.mule.ibeans.api.IBeansNotationHelper;

import java.lang.reflect.Method;
import java.util.Collection;

/**
 * Create a DOJO Standard Method Descriptor (SMD) descritpion of the iBean. This can be used when using the iBean
 * in AJAX from the browser.
 */
public class SMDView implements IBeanView
{
    public static final String DEFAULT_TRANSPORT = "POST";

    public static final String BASE_TARGET = "/ibeans/rpc/";

    public static final String DEFAULT_ENVELOPE = "JSON-RPC-1.2";

    private String transport = DEFAULT_TRANSPORT;
    private String envelope = DEFAULT_ENVELOPE;
    private String target;

    public SMDView(String target)
    {
        this.target = target;
    }

    public SMDView(String transport, String envelope, String target)
    {
        this.transport = transport;
        this.envelope = envelope;
        this.target = target;
    }

    public String createView(Class ibean)
    {
        String id = IBeansNotationHelper.getIBeanShortID(ibean);
        StringBuffer buf = new StringBuffer();
        buf.append("{envelope:\"").append(envelope);
        buf.append("\", transport:\n").append(transport);
        buf.append("\", target:\"").append(target);
        buf.append("\", services:{\n");

        for (int i = 0; i < ibean.getMethods().length; i++)
        {
            if (i > 0)
            {
                buf.append(", ");
            }
            Method method = ibean.getMethods()[i];
            buf.append(method.getName()).append(" : {\nparameters : [{");

            for (int x = 0; i < method.getParameterTypes().length; x++)
            {
                if (x > 0)
                {
                    buf.append(", ");
                }
                buf.append("type : \"").append(getJsonType(method.getParameterTypes()[x]));
                buf.append("\"}");
            }
            buf.append("]}");
        }
        buf.append("}");

        return buf.toString();

    }

    protected String getJsonType(Class type)
    {
        if (Number.class.isAssignableFrom(type))
        {
            return "number";
        }
        else if (String.class.isAssignableFrom(type))
        {
            return "string";
        }
        else if (Collection.class.isAssignableFrom(type))
        {
            return "array";
        }
        else if (Boolean.class.isAssignableFrom(type))
        {
            return "boolean";
        }
        else
        {
            throw new IllegalArgumentException("Non-serializable type: " + type.getName());
        }
    }


}
