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

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.activation.DataSource;
import org.ibeans.api.channel.MimeType;

import org.ibeans.spi.ErrorFilter;

/**
 * TODO
 */

public interface IBeanInvocationData
{
    /**
     * Returns a map of all request Header params set on the iBean so far. The map is sorted
     * alphabetically (ascending order)
     *
     * @return a Map of all request Header params
     */
    Map<String, Object> getHeaderParams();

    void setHeaderParams(Map<String, Object> headerParams);

    void addHeaderParam(String name, Object value);

    Map<String, Object> getPayloadParams();

    void setPayloadParams(Map<String, Object> payloadParams);

    void addPayloadParam(String name, Object value);

    /**
     * Returns a map of all URI params including fixed params on the Call URI and any variablized params.
     * The map is sorted alphabetically (ascending order)
     *
     * @return A Map of all URI params
     */
    Map<String, Object> getUriParams();

    void setUriParams(Map<String, Object> uriParams);

    void addUriParam(String name, Object value);

    Map<String, Object> getPropertyParams();

    void setPropertyParams(Map<String, Object> propertyParams);

    void addPropertyParam(String name, Object value);

    List<Object> getPayloads();

    void setPayloads(List<Object> payloads);

    void addPayload(Object payload);

    Set<DataSource> getAttachments();

    void setAttachments(Set<DataSource> attachments);

    /**
     * @param attachment The attachment that will be be added
     */
    void addRequestAttachment(DataSource attachment);

    /**
     * The return type as . This is read-only in the invocation context because it is
     * determined by the return type of he annotated method
     *
     * @return the return type
     */
    DataType getReturnType();

    void setReturnType(DataType returnType);

    /**
     * Looks up a param in all of the param collections. The search order is
     * uriParams, headerParams, and then propertyParams.
     *
     * @param key The name of the parameter to find
     * @param the default value to return
     * @return The value of the parameter with the specified key or null if the
     *         parameter is not set
     */
    <T> T getParam(String key, T defaultValue);
}
