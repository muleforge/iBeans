/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.spring;

import org.ibeans.annotation.Template;
import org.ibeans.annotation.param.UriParam;

/**
 * Dummy
 */
public interface DummyIBean
{
    @UriParam("do_something_uri")
    public static final String DO_SOMETHING_URI = "http://doesnotexist.bom?param1=";

    @Template("{do_something_uri}{foo}")
    public String doSomething(@UriParam("foo") String foo);

}
