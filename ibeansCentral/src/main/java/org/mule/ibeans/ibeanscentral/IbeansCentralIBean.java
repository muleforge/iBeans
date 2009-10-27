/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.ibeans.ibeanscentral;

import org.mule.ibeans.api.client.Call;
import org.mule.ibeans.api.client.CallException;
import org.mule.ibeans.api.client.Return;
import org.mule.ibeans.api.client.State;
import org.mule.ibeans.api.client.Template;
import org.mule.ibeans.api.client.Usage;
import org.mule.ibeans.api.client.authentication.HttpBasicAuthentication;
import org.mule.ibeans.api.client.params.Payload;
import org.mule.ibeans.api.client.params.PayloadParam;
import org.mule.ibeans.api.client.params.PropertyParam;
import org.mule.ibeans.api.client.params.UriParam;
import org.mule.ibeans.channels.HTTP;

import java.io.InputStream;
import java.net.URL;
import java.util.List;


@Usage("How to use this bean")
public interface IbeansCentralIBean extends HttpBasicAuthentication
{
    @UriParam("host")
    static final String HOST = "ibeans.mulesoft.org";

    @UriParam("port")
    static final int PORT = 80;

    @UriParam("ibeans_workspace")
    static final String IBEANS_WORKSPACE = "iBeans";

    @UriParam("sandbox_workspace")
    static final String SANDBOX_WORKSPACE = "Sandbox";

    @UriParam("api_base")
    static final String API_BASE = "/central/api/registry";

    @State
    void init(@PropertyParam("username") String username, @PropertyParam("password") String password);

    @Call(uri = "http://{host}:{port}{api_base}?q=select where parent:name like '.jar' and child:type.name = 'iBean'", properties = HTTP.FOLLOW_REDIRECTS)
    List<IBeanInfo> getIBeans() throws CallException;

    @Call(uri = "http://{host}:{port}{api_base}?q=select where parent:name like '.jar' and child:type.name = 'iBean' and jar.manifest.Specification-Title = '{shortName}-ibean'", properties = HTTP.FOLLOW_REDIRECTS)
    IBeanInfo getIBeanByShortName(@UriParam("shortName") String shortName) throws CallException;

    @Call(uri = "http://{host}:{port}{api_base}?q=select where parent:name like '.jar' and child:type.name = 'iBean' and jar.manifest.Specification-Title = '{shortName}-ibean' and name = '{version}'", properties = HTTP.FOLLOW_REDIRECTS)
    IBeanInfo getIBeanByShortName(@UriParam("shortName") String shortName, @UriParam("version") String version) throws CallException;

    @Call(uri = "http://{host}:{port}{download_uri}", properties = HTTP.FOLLOW_REDIRECTS)
    InputStream downloadIBean(@UriParam("download_uri") String uri) throws CallException;

    @Template("http://{host}:{port}#[bean:downloadUri]")
    URL getIBeanDownloadUrl(@Payload IBeanInfo info) throws CallException;

    @Call(uri = "http://{host}:{port}/central/j_acegi_security_check")
    @Return("#[header:Location != *login_error*]")
    Boolean verifyCredentials(@PayloadParam("j_username") String username, @PayloadParam("j_password") String password) throws CallException;

    //public IBeanInfo uploadIBean(@UriParam("name") String name, @UriParam("version") String version, @Payload File ibeanJar) throws CallException;
}