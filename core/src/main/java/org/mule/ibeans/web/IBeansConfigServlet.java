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
import org.mule.api.registry.RegistrationException;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * A servlet used for configuration only.  IT allows key / value pairs to be made available in the iBeans context
 * and via the iBeans JavaScript client using the config bean i.e.
 * <code>
 * initIBeans();
 * var value = ibeans.config.get('myapp.password');
 * </code>
 * <p/>
 * This allows sensitive data to be externalized for Java code and JavaScript. Note that MuleSoft Tcat offer centralized
 * configuration in the same manner.  By running from a Tcat server you can do -
 * <code>
 * initIBeans();
 * var value = ibeans.config.get('${stage}/[namespace]/myapp.password');
 * </code>
 * <p/>
 * Where stage is an optional deployment grouping of configuration (resolved at run-time) and [namespace] is a configuration namespace.
 * <p/>
 * Note that when using the {@link org.mule.ibeans.web.IBeansRpcServlet} for JavaScript communication to the server, the 'config' iBean must be
 * enabled (see the {@link org.mule.ibeans.web.IBeansRpcServlet} javadoc for details).
 *
 * @deprecated This class will probably be removed before 1.0.  Instead use the Tomcat JNDI context by adding the following to the context.xml -
 *             <p/>
 *             <code>
 *             &lt;Context ...&gt;
 *             ...
 *             &lt;Environment name="myapp.password" type="java.lang.String" value="foobar"/&gt;
 *             <p/>
 *             &lt;/Context&gt;
 *             </code>
 *             <p/>
 *             Note that you can configure a Tomcat context.xml for individual webapps by putting the in your webapp under /META-INF/context.xml
 */
public class IBeansConfigServlet implements Servlet
{
    private MuleContext muleContext;

    public void init(ServletConfig servletConfig) throws ServletException
    {
        muleContext = (MuleContext) servletConfig.getServletContext().getAttribute(MuleProperties.MULE_CONTEXT_PROPERTY);

        Map<String, Object> params = new HashMap<String, Object>();
        Enumeration e = getServletConfig().getInitParameterNames();
        while (e.hasMoreElements())
        {
            String paramName = (String) e.nextElement();
            params.put(paramName, getServletConfig().getInitParameter(paramName));
        }
        //Already read, not needed
        //Add any other params to the registry so that can be made available in the context
        try
        {
            muleContext.getRegistry().registerObjects(params);
        }
        catch (RegistrationException e1)
        {
            throw new ServletException("Failed to add config params to the iBeans registry", e1);
        }
    }

    public ServletConfig getServletConfig()
    {
        return null;
    }

    public void service(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException
    {
        throw new UnsupportedOperationException("Servlet.service.  This servlet should not have a servlet mapping configured in the web.xml");
    }

    public String getServletInfo()
    {
        return null;
    }

    public void destroy()
    {
        //nothing to do
    }
}
