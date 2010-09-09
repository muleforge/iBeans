#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
/*
 * ${symbol_dollar}Id: ${symbol_dollar}
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package ${package};

import org.mule.ibeans.api.client.Call;
import org.mule.ibeans.api.client.Template;
import org.mule.ibeans.api.client.CallException;
import org.mule.ibeans.api.client.State;
import org.mule.ibeans.api.client.Usage;
import org.mule.ibeans.api.client.authentication.HttpBasicAuthentication;
import org.mule.ibeans.api.client.filters.JsonErrorFilter;
import org.mule.ibeans.api.client.filters.XmlErrorFilter;
import org.mule.ibeans.api.client.params.Optional;
import org.mule.ibeans.api.client.params.PayloadParam;
import org.mule.ibeans.api.client.params.ReturnType;
import org.mule.ibeans.api.client.params.UriParam;


@Usage("How to use this bean")
public interface ${artifactId}IBean // Add if you need Http Basic Authentication extends HttpBasicAuthentication
{
    //TODO Declare default values like this
    @UriParam("foo")
    public static final String DEFAULT_FOO = "bar";

    //TODO State calls allow you configure common values
    @State
    public void init(@UriParam("foo") String defaultFoo);
    
    //TODO Add one or more call methods that communicate with your service
    //NOTE the Template parameter is used to evaluate a string value, it's used here so the OOTB testcase works
    @Template("http://www.foo.com/update/{foo}")
    public String updateFoo(@PayloadParam("foo") String value) throws CallException;

    //@Call(uri = "http://www.foo.com/update/{foo}")
    //public String defaultUpdateFoo() throws CallException;
}
