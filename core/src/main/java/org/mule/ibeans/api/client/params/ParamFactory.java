package org.mule.ibeans.api.client.params;

/**
 * A special parameter type that allows an iBean to create a parameter from other parameters that have already been set on the iBean. It is quite common
 * for REST services to require secure hashes of the request data, and a parameter factory can be used to generate that value. ParamFactory objects are also
 * useful for generating authentication information.
 */
public interface ParamFactory
{

    /**
     * The method used to create the parameter.
     * Note that if null is returned, the parameter is omitted from the request.
     *
     * @param paramName
     * @param optional
     * @param invocationContext
     * @return
     */
    Object create(String paramName, boolean optional, InvocationContext invocationContext);
}