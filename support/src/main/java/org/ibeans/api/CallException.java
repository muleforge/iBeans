/*
 * $Id: CallException.java 2 2009-09-15 10:51:49Z ross $
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
 * The exception type to throw on methods that are annotated with the {@link org.ibeans.api.Call} annotation.
 * This exception type has an error code that is specific to the channel used by the @Call annotation. For example, for HTTP
 * the error code will be the return code.
 * <p/>
 * Any additional information about the error will be stored in the freeform 'info' Map.
 */
public class CallException extends Exception
{
    private String errorCode;

    private Map<String, Object> info = new HashMap<String, Object>();

    public CallException(String message)
    {
        super(message);
    }

    public CallException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public CallException(String message, String errorCode)
    {
        super(message);
        this.errorCode = errorCode;
    }

    public CallException(String message, String errorCode, Throwable cause)
    {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode()
    {
        return errorCode;
    }

    public Object getResponsePayload()
    {
        if (info != null)
        {
            return info.get("response.payload");
        }
        return null;
    }

    public Map<String, Object> getInfo()
    {
        return info;
    }

    public void setInfo(Map<String, Object> info)
    {
        this.info = info;
    }

    @Override
    public String toString()
    {
        return "CallException{" + getMessage() +
                ", errorCode='" + errorCode + '\'' +
                ", info=" + info.toString() +
                '}';
    }
}
