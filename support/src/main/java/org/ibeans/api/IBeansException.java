/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.ibeans.api;

import java.util.HashMap;
import java.util.Map;

/**
 * Base iBeans exception
 */
public class IBeansException extends Exception
{
    private Map<String, Object> info = new HashMap<String, Object>();

    public IBeansException(String message)
    {
        super(message);
    }

    public IBeansException(String message, Throwable cause)
    {
        super(message, cause);
    }


    public IBeansException(Throwable cause)
    {
        super(cause);
    }

    public Map<String, Object> getInfo()
    {
        return info;
    }

    public void setInfo(Map<String, Object> info)
    {
        this.info = info;
    }
}
