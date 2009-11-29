/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.bar;

import org.mule.ibeans.api.client.CallException;
import org.mule.ibeans.api.client.Template;
import org.mule.ibeans.api.client.params.UriParam;

/**
 * A test bean that should not be found when scanning the classpath
 */
public interface NotFoundIBean
{
    @Template("http://doesnotexist.bom?param={foo}")
    public String doSomething(@UriParam("foo") String foo) throws CallException;

    @Template("http://doesnotexist.bom")
    public String doSomethingElse() throws CallException;
}