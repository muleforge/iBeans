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
import org.mule.config.builders.AbstractConfigurationBuilder;
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
 * A configuration builder that registers iBean objects on the classpath with the MUle registry.
 * <p/>
 * The registry can then be used to query avaialble iBeans
 */
public class IBeanHolderConfigurationBuilder extends AbstractConfigurationBuilder
{
    public static final String[] DEFAULT_BASEPATHS = new String[]{"org/mule/ibeans"};

    private ClassLoader classLoader;
    private String[] basepaths;

    public IBeanHolderConfigurationBuilder()
    {
        this(DEFAULT_BASEPATHS);
    }

    public IBeanHolderConfigurationBuilder(String... basepaths)
    {
        this.classLoader = Thread.currentThread().getContextClassLoader();
        this.basepaths = basepaths;
    }

    public IBeanHolderConfigurationBuilder(ClassLoader classLoader)
    {
        this(classLoader, DEFAULT_BASEPATHS);
    }

    public IBeanHolderConfigurationBuilder(ClassLoader classLoader, String... basepaths)
    {
        this.classLoader = classLoader;
        this.basepaths = basepaths;
    }

    protected void doConfigure(MuleContext muleContext) throws Exception
    {
        Set<Class> ibeanClasses = new HashSet<Class>();
        Set<Object> transformers = new HashSet<Object>();

        for (int i = 0; i < basepaths.length; i++)
        {
            String basepath = basepaths[i];
            ClasspathScanner scanner = new ClasspathScanner(classLoader, basepath);

            try
            {
                //There will be some overlap here but only
                ibeanClasses = scanner.scanFor(Call.class);
                ibeanClasses.addAll(scanner.scanFor(Template.class));
                //Some ibeans will extend other iBeans but have not methods of there own
                ibeanClasses.addAll(scanner.scanFor(IBeanGroup.class));
                transformers.addAll(findTransformers(scanner));
            }
            catch (IOException e)
            {
                throw new ConfigurationException(e);
            }
        }

        for (Class ibeanClass : ibeanClasses)
        {
            muleContext.getRegistry().registerObject("_ibeanHolder." + ibeanClass.getName(), new IBeanHolder(ibeanClass));
        }

        for (Object transformer : transformers)
        {
            muleContext.getRegistry().registerObject("_transformer." + transformer.getClass().getName(), transformer);

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