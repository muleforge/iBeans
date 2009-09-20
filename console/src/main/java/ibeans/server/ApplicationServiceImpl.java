/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package ibeans.server;

import org.mule.ibeans.internal.config.IBeansInfo;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import javax.servlet.http.HttpSession;

import ibeans.client.ApplicationService;
import ibeans.client.model.AppInfo;

/**
 * TODO
 */
public class ApplicationServiceImpl extends RemoteServiceServlet implements ApplicationService
{
    public AppInfo getApplicationInfo()
    {
        AppInfo info = new AppInfo();
        info.setName(IBeansInfo.getProductName());
        info.setVersion(IBeansInfo.getProductVersion());
        info.setCopyright("Copyright 2009 " + IBeansInfo.getVendorName() + ", All rights reserved");

        if (info.getName() == null)
        {
            info.setName("Mule iBeans");
            info.setVersion("dev");
            info.setCopyright("Copyright 2009 MuleSoft Inc., All rights reserved");
        }
        return info;
    }
}
