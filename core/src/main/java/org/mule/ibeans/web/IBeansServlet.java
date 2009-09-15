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
import org.mule.ibeans.internal.ext.servlet.ExtendedMuleReceiverServlet;
import org.mule.routing.filters.WildcardFilter;
import org.mule.util.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * The IBeansServlet tells Tomcat that you'll be using iBeans in your application and a new iBeans context will be set up for you.
 * <p/>
 * There are two configuration parameters for this servlet, both are optional.
 * <b>config.builder</b> - the short name of the configuration builder to use when using iBeans. The configuration builder is responsible for
 * discovering objects to configure and initialising them with the container.  By default iBeans uses the 'ibeans' ConfigurationBuilder which
 * discovers and parses Mule iBeans annotations such as {@link org.mule.ibeans.api.client.IntegrationBean}, {@link org.mule.ibeans.api.application.Schedule}
 * {@link org.mule.ibeans.api.application.Send}, {@link org.mule.ibeans.api.application.Transformer}, etc.
 * <p/>
 * Another configuration option is to use Google Guice modules to configure your objects.  Guice is a DI (Dependency Injection) framework that ca be used to
 * wire together your application objects.  Using 'guice' as your configuration builder means that iBeans will not discover your annotated objects, instead
 * they should be loaded via a Guice module.
 * The default is 'ibeans'.
 * <p/>
 * <b>static.file.types</b> A comma-separated list of file types that the IBeansServlet will allow to be loaded from the classpath.  This is used to
 * load the ibeans.js files since they are loaded in a Jar on the classpath.  The default value for this is '*.js', this is required if using the iBeans AJAX
 * client in the browser.  You can other file type i.e. '*.js,*.txt'.
 */
public class IBeansServlet extends ExtendedMuleReceiverServlet
{
    public static final String CONFIG_BUILDER_PARAM = "config.builder";
    public static final String STATIC_FILE_TYPES_PARAM = "static.file.types";

    public static final String DEFAULT_MIME_TYPE = "application/octet-stream";
    public static final String DEFAULT_STATIC_FILE_TYPES = "*.js";

    private WildcardFilter staticFileFilter;
    private IBeansServletContextListener listener;

    @Override
    protected MuleContext setupMuleContext() throws ServletException
    {
        MuleContext muleContext = (MuleContext) getServletContext().getAttribute(MuleProperties.MULE_CONTEXT_PROPERTY);
        if (muleContext == null)
        {
            String configBuilder = getServletConfig().getInitParameter(CONFIG_BUILDER_PARAM);
            if (configBuilder == null)
            {
                configBuilder = IBeansServletContextListener.DEFAULT_CONFIG_BUILDER;
            }
            listener = new IBeansServletContextListener(configBuilder);
            listener.initialize(getServletContext());
        }
        muleContext = (MuleContext) getServletContext().getAttribute(MuleProperties.MULE_CONTEXT_PROPERTY);
        if (muleContext == null)
        {
            throw new ServletException("The MuleContext was not created successfully.  Check previous log errors for the cause");
        }
        return muleContext;
    }

    @Override
    protected void doInit() throws ServletException
    {
        super.doInit();

        Map<String, Object> params = new HashMap<String, Object>();
        Enumeration e = getServletConfig().getInitParameterNames();
        while (e.hasMoreElements())
        {
            String paramName = (String) e.nextElement();
            params.put(paramName, getServletConfig().getInitParameter(paramName));
        }
        //Already read, not needed
        params.remove(CONFIG_BUILDER_PARAM);
        String staticfiles = (String) params.remove(STATIC_FILE_TYPES_PARAM);
        if (staticfiles == null)
        {
            staticfiles = DEFAULT_STATIC_FILE_TYPES;
        }
        staticFileFilter = new WildcardFilter(staticfiles);
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

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        if (staticFileFilter.accept(request.getPathInfo()))
        {
            loadJarResource(request.getPathInfo(), response);
            return;
        }
        super.doGet(request, response);
    }

    @Override
    public void destroy()
    {
        super.destroy();
        if (listener != null)
        {
            listener.destroy();
        }
    }

    protected void loadJarResource(String file, HttpServletResponse resp) throws ServletException
    {
        InputStream in = null;
        try
        {
            String localFile = file;
            if (localFile.startsWith("/"))
            {
                localFile = localFile.substring(1);
            }

            in = IOUtils.getResourceAsStream(localFile, getClass(), false, false);
            if (in == null)
            {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("Unable to find file: " + file);
                return;
            }
            byte[] buffer;

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            IOUtils.copyLarge(in, baos);

            buffer = baos.toByteArray();

            String mimetype = DEFAULT_MIME_TYPE;
            if (getServletContext() != null)
            {
                String temp = getServletContext().getMimeType(file);
                if (temp != null)
                {
                    mimetype = temp;
                }
            }

            resp.setContentType(mimetype);
            resp.setContentLength(buffer.length);
            resp.setHeader("Content-Disposition", "attachment; filename=\"" + file + "\"");
            resp.getOutputStream().write(buffer);
        }
        catch (Exception e)
        {
            throw new ServletException(e);
        }
        finally
        {
            try
            {
                if (in != null)
                {
                    in.close();
                }
                resp.getOutputStream().flush();
            }
            catch (IOException e)
            {
                log(e.getMessage(), e);
            }
        }
    }
}

