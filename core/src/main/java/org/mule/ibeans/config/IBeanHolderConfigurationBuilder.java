/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.config;

import org.mule.api.MuleContext;
import org.mule.api.config.ConfigurationException;
import org.mule.ibeans.api.application.Transformer;
import org.mule.ibeans.api.client.Call;
import org.mule.ibeans.api.client.IBeanGroup;
import org.mule.ibeans.api.client.Template;
import org.mule.util.ClassUtils;
import org.mule.util.scan.ClasspathScanner;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * A configuration builder that registers iBean objects on the classpath with the Mule registry.
 * <p/>
 * The registry can then be used to query avaialble iBeans.
 */
public class IBeanHolderConfigurationBuilder extends AbstractAnnotationConfigurationBuilder
{
    public static final String IBEAN_HOLDER_PREFIX = "_ibeanHolder.";
    public static final String TRANSFORMER_PREFIX = "_transformer.";

    public IBeanHolderConfigurationBuilder()
    {
    }

    public IBeanHolderConfigurationBuilder(String... basepackages)
    {
        super(basepackages);
    }

    public IBeanHolderConfigurationBuilder(ClassLoader classLoader)
    {
        super(classLoader);
    }

    public IBeanHolderConfigurationBuilder(ClassLoader classLoader, String... basepackages)
    {
        super(classLoader, basepackages);
    }

    protected String getScanPackagesProperty()
    {
        return "ibeans.scan.packages";
    }

    protected void doConfigure(MuleContext muleContext) throws Exception
    {

        Set<Class> ibeanClasses = new HashSet<Class>();
        Set<Object> transformers = new HashSet<Object>();

        ClasspathScanner scanner = createClasspathScanner();

        try
        {
            //There will be some overlap here but only
            ibeanClasses.addAll(scanner.scanFor(Call.class));
            ibeanClasses.addAll(scanner.scanFor(Template.class));
            //Some ibeans will extend other iBeans but have not methods of there own
            ibeanClasses.addAll(scanner.scanFor(IBeanGroup.class));
            transformers.addAll(findTransformers(scanner));
        }
        catch (IOException e)
        {
            throw new ConfigurationException(e);
        }

        for (Class ibeanClass : ibeanClasses)
        {
            muleContext.getRegistry().registerObject(IBEAN_HOLDER_PREFIX + ibeanClass.getName(), new IBeanHolder(ibeanClass));
        }

        for (Object transformer : transformers)
        {
            muleContext.getRegistry().registerObject(TRANSFORMER_PREFIX + transformer.getClass().getName(), transformer);
        }
    }

    protected Set<Object> findTransformers(ClasspathScanner scanner) throws ConfigurationException
    {
        Set<Class> transformerClasses;
        try
        {
            transformerClasses = scanner.scanFor(Transformer.class);
        }
        catch (IOException e)
        {
            throw new ConfigurationException(e);
        }

        Set<Object> transformers = new HashSet<Object>(transformerClasses.size());
        for (Class transformerClass : transformerClasses)
        {
            try
            {
                if (transformerClass.isLocalClass() || transformerClass.isMemberClass())
                {
                    if (logger.isDebugEnabled())
                    {
                        logger.debug("Skipping class: " + transformerClass.getName() + ". Discoverable transofmrers cannot be defined in Local or Member classes");
                    }
                }
                else
                {
                    Object transformer = ClassUtils.instanciateClass(transformerClass, ClassUtils.NO_ARGS);
                    transformers.add(transformer);
                }
            }
            catch (Exception e)
            {
                throw new ConfigurationException(e);
            }

        }
        return transformers;
    }
}