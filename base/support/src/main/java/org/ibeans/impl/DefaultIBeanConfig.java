/*
 * $Id$
 * -------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.ibeans.impl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.activation.DataSource;

import org.ibeans.api.DataType;
import org.ibeans.api.IBeanInvocationData;
import org.ibeans.impl.support.ds.DataSourceComparator;


/**
 * TODO
 */
public final class DefaultIBeanConfig implements IBeanInvocationData
{
    protected Map<String, Object> headerParams = new LinkedHashMap<String, Object>();
    protected Map<String, Object> payloadParams = new LinkedHashMap<String, Object>();
    protected Map<String, Object> uriParams = new LinkedHashMap<String, Object>();
    protected Map<String, Object> propertyParams = new LinkedHashMap<String, Object>();
    protected List<Object> payloads = new ArrayList<Object>();
    protected Set<DataSource> attachments = new TreeSet<DataSource>(new DataSourceComparator());
    protected DataType returnType;


    public Map<String, Object> getHeaderParams()
    {
        return headerParams;
    }

    public void setHeaderParams(Map<String, Object> headerParams)
    {
        this.headerParams = headerParams;
    }

    public void addHeaderParam(String name, Object value)
    {
        getHeaderParams().put(name, value);
    }

    public Map<String, Object> getPayloadParams()
    {
        return payloadParams;
    }

    public void setPayloadParams(Map<String, Object> payloadParams)
    {
        this.payloadParams = payloadParams;
    }

    public void addPayloadParam(String name, Object value)
    {
        getPayloadParams().put(name, value);
    }

    public Map<String, Object> getUriParams()
    {
        return uriParams;
    }

    public void setUriParams(Map<String, Object> uriParams)
    {
        this.uriParams = uriParams;
    }

    public void addUriParam(String name, Object value)
    {
        getUriParams().put(name, value);
    }

    public Map<String, Object> getPropertyParams()
    {
        return propertyParams;
    }

    public void setPropertyParams(Map<String, Object> propertyParams)
    {
        this.propertyParams = propertyParams;
    }

    public void addPropertyParam(String name, Object value)
    {
        getPropertyParams().put(name, value);
    }

    public List<Object> getPayloads()
    {
        return payloads;
    }

    public void setPayloads(List<Object> payloads)
    {
        this.payloads = payloads;
    }

    public void addPayload(Object payload)
    {
        getPayloads().add(payload);
    }

    public Set<DataSource> getAttachments()
    {
        return attachments;
    }

    public void setAttachments(Set<DataSource> attachments)
    {
        this.attachments = attachments;
    }

    public void addRequestAttachment(DataSource attachment)
    {
        getAttachments().add(attachment);
    }

    public DataType getReturnType()
    {
        return returnType;
    }

    public void setReturnType(DataType returnType)
    {
        this.returnType = returnType;
    }

    public <T> T getParam(String key, T defaultValue)
    {
        Object value = uriParams.get(key);
        if (value == null)
        {
            value = headerParams.get(key);
            if (value == null)
            {
                value = propertyParams.get(key);
            }
        }
        if(value==null)
        {
            value = defaultValue;
        }
        return (T)value;

    }

}
