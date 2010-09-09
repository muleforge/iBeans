/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.web;

import org.mule.transport.ajax.container.MuleAjaxServlet;

/**
 * Necessary so that we can bind ajax protocol to ajax-servlet and make it the default. In Mule the ajax protocol is
 * default and starts up an embedded servlet container
 */
public class IBeansAjaxServlet extends MuleAjaxServlet
{
    @Override
    protected String getConnectorProtocol()
    {
        return "ajax";
    }
}
