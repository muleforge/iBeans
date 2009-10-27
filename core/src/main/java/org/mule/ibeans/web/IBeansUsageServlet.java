/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.web;

import org.mule.api.MuleContext;
import org.mule.api.config.MuleProperties;
import org.mule.ibeans.config.IBeanHolder;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * User Jersey here once integrated
 */
public class IBeansUsageServlet extends HttpServlet
{
    private Collection<IBeanHolder> ibeansCache;

    @Override
    public void init() throws ServletException
    {
        MuleContext muleContext = (MuleContext) getServletContext().getAttribute(MuleProperties.MULE_CONTEXT_PROPERTY);
        ibeansCache = muleContext.getRegistry().lookupObjects(IBeanHolder.class);
    }

    /*
     TODO this is a stopgap solution for looking up iBean usage info until we have some nicer view implemented this is what you get
    */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        String path = request.getPathInfo();
        int i = path.lastIndexOf("/");
        String ibeanId = path.substring(i + 1);
        i = ibeanId.indexOf(".");
        String format = "text";
        if (i != -1)
        {
            format = ibeanId.substring(i + 1);
            ibeanId = ibeanId.substring(0, i);
        }

        for (IBeanHolder holder : ibeansCache)
        {
            if (ibeanId.equals(holder.getId()))
            {
                //TODO Format
                String usage = holder.getUsage();
                response.setContentType("text/plain");
                response.getOutputStream().print(usage);
                response.flushBuffer();
                return;
            }
        }
        log("ibean not found: " + ibeanId);
        response.sendError(404, "ibean not found: " + ibeanId);

//        RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/IBeanInfo.jsp");
//        dispatcher.forward(request,response);
    }

}
