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
import java.util.Map;
import java.util.Set;

import org.ibeans.spi.ErrorFilter;

/**
 * TODO
 */

public interface IBeanStateData
{
    /**
     * Returns a map of all request Header params set on the iBean so far. The map is sorted
     * alphabetically (ascending order)
     *
     * @return a Map of all request Header params
     */
    Map<String, Object> getHeaderParams();

    Map<String, Object> getPayloadParams();

    /**
     * Returns a map of all URI params including fixed params on the Call URI and any variablized params.
     * The map is sorted alphabetically (ascending order)
     *
     * @return A Map of all URI params
     */
    Map<String, Object> getUriParams();

    Map<String, Object> getPropertyParams();

    Map<String, Object> getAttachmentParams();

    /**
     * The return type as . This is read-only in the invocation context because it is
     * determined by the return type of he annotated method
     *
     * @return the return type
     */
    DataType getReturnType();

    /**
     * A lot of web servers do not use the http return code, instead they return an error message as the result of the call
     * This filter is used to determine whether an error was returned from the service
     */
    Map<String, ErrorFilter> getErrorFilters();

    Map<Method, ErrorFilter> getMethodLevelErrorFilters();

    Set<ParamFactoryHolder> getUriFactoryParams();

    Set<ParamFactoryHolder> getHeaderFactoryParams();
    
    Set<ParamFactoryHolder> getPropertyFactoryParams();

    Set<ParamFactoryHolder> getAttachmentFactoryParams();

    Set<ParamFactoryHolder> getPayloadFactoryParams();
    
    Map getNamespaces();

}
