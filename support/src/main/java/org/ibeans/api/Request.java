/*
 * $Id:  $
 * -------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.ibeans.api;

import java.util.Set;

import javax.activation.DataHandler;

/**
 * TODO
 */

public interface Request
{
    public Object getPayload();

    public void setPayload(Object payload);

    void addHeader(String name, Object value);

    Object removeHeader(String name);

    Object getHeader(String name);

    Set<String> getHeaderNames();

    void addAttachment(String name, DataHandler handler);

    DataHandler removeAttachment(String name);

    DataHandler getAttachment(String name);

    Set<String> getAttachmentNames();

    int getTimeout();

    void setTimeout(int timeout);

    IBeanInvocationData getIBeanInvocationData();
}
