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

import org.mule.api.MuleContext;
import org.mule.api.config.MuleProperties;
import org.mule.ibeans.ConfigManager;
import org.mule.ibeans.config.IBeanHolder;
import org.mule.ibeans.internal.ext.jabsorb.EnumSerializer;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.jabsorb.JSONRPCBridge;
import org.jabsorb.JSONRPCServlet;

/**
 * Will process calls on registered iBeans on the browser using the JSON RPC library from jabsorb. This servlet by default
 * will make all iBeans in the container available to the browser. Depending on what iBeans are installed, this could present
 * a security risk.  It is possible to limit which iBeans are made available but setting the 'enabled.ibeans' init parameter to
 * a comma-separated list of iBeans to enable.
 * <p/>
 * You can access configuration values specified in Tomcat JNDI or MuleSoft Tcat Registry from JavaScript. To do this
 * you need to make sure you include 'config' in the list of iBeans to enable on this servlet i.e.
 * <code>
 * &lt;servlet&gt;
 * &lt;servlet-name&gt;ibeans-rpc&lt;/servlet-name&gt;
 * &lt;servlet-class&gt;org.mule.ibeans.web.IBeansRpcServlet&lt;/servlet-class&gt;
 * &lt;init-param&gt;
 * &lt;param-name&gt;gzip_threshold&lt;/param-name&gt;
 * &lt;param-value&gt;200&lt;/param-value&gt;
 * &lt;/init-param&gt;
 * &lt;init-param&gt;
 * &lt;param-name&gt;enabled.ibeans&lt;/param-name&gt;
 * &lt;param-value&gt;flickr,twitter,aws,config&lt;/param-value&gt;
 * &lt;/init-param&gt;
 * &lt;/servlet&gt;
 * </code>
 * <p/>
 * The default value for 'enabled.ibeans' is '*' (all of them)
 */
public class IBeansRpcServlet extends JSONRPCServlet
{
    public static final String ENABLED_IBEANS_PARAM = "enabled.ibeans";

    public static final String ENABLE_ALL_IBEANS = "*";

    public static final String CONFIG_BEAN_NAME = "config";

    private String accessibleIbeans = ENABLE_ALL_IBEANS;
    private MuleContext muleContext;

    public void init() throws ServletException
    {
        super.init();
        muleContext = (MuleContext) getServletContext().getAttribute(MuleProperties.MULE_CONTEXT_PROPERTY);
        String temp = getServletConfig().getInitParameter(ENABLED_IBEANS_PARAM);
        if (temp != null)
        {
            accessibleIbeans = temp;
        }
        accessibleIbeans = accessibleIbeans.replaceAll(" ", "");
    }

    @Override
    public void service(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException
    {
        HttpSession session = ((HttpServletRequest) servletRequest).getSession();
        JSONRPCBridge bridge = (JSONRPCBridge) session.getAttribute(JSONRPCBridge.class.getSimpleName());
        if (bridge == null)
        {
            bridge = new JSONRPCBridge();
            session.setAttribute(JSONRPCBridge.class.getSimpleName(), bridge);
            initBridge(bridge);
        }
        super.service(servletRequest, servletResponse);
    }

    protected void initBridge(JSONRPCBridge bridge) throws ServletException
    {
        try
        {
            bridge.registerSerializer(new EnumSerializer());
        }
        catch (Exception e)
        {
            throw new ServletException(e);
        }

        boolean all = ENABLE_ALL_IBEANS.equals(accessibleIbeans);
        Collection<IBeanHolder> col = muleContext.getRegistry().lookupObjects(IBeanHolder.class);
        for (IBeanHolder holder : col)
        {
            if (all)
            {
                bridge.registerObject(holder.getId(), holder.create(muleContext));
            }
            else if (accessibleIbeans.contains(holder.getId()))
            {
                bridge.registerObject(holder.getId(), holder.create(muleContext));
            }
        }
        if (all || accessibleIbeans.contains(CONFIG_BEAN_NAME))
        {
            bridge.registerObject(CONFIG_BEAN_NAME, new ConfigManager(muleContext));
        }
    }
}
