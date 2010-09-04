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

/**
 * A special parameter type that allows an iBean to create a parameter from other parameters that have already been set on the iBean. It is quite common
 * for REST services to require secure hashes of the request data, and a parameter factory can be used to generate that value. ParamFactory objects are also
 * useful for generating authentication information.
 */
public interface ParamFactory
{

    /**
     * The method used to create the parameter.
     * Note that if null is returned, the parameter is omitted from the request. include the parameter in the request with a
     * null value return and empty String. If the optional flag is false, returning a null will cause an exception.
     *
     * @param paramName the parameter name associated with the value
     * @param optional determines whether this parameter is optional. If optional a null can be returned from this method
     * @param invocationContext the current invocation context.  Can be used to look up additional information about the
     * request such as the other invocation parameters and properties
     * @return an object associated to the param name that will be used to construct the request or null if the parameter is
     * optional and could not be created. 
     */
    Object create(String paramName, boolean optional, InvocationContext invocationContext);
}