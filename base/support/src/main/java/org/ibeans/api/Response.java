/*
 * $Id$
 * -------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.ibeans.api;

import java.io.InputStream;
import java.util.Set;

import javax.activation.DataHandler;
import org.ibeans.api.channel.MimeType;

/**
 * TODO
 */

public interface Response
{
    public InputStream getPayloadAsStream();

    public Object getPayload();

    Object  getHeader(String name);

    Set<String> getHeaderNames();

    DataHandler getAttachment(String name);

    Set<String> getAttachmentNames();

    String getStatusCode();

    void setStatusCode(String code);

    @Deprecated
    String getMimeType();

    DataType getDataType();

    Throwable getException();
}
