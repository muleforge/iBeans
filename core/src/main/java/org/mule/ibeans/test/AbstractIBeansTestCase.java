/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.test;

import org.mule.api.MuleContext;
import org.mule.api.NamedObject;
import org.mule.api.config.ConfigurationBuilder;
import org.mule.api.context.MuleContextFactory;
import org.mule.api.registry.RegistrationException;
import org.mule.api.transformer.DataType;
import org.mule.api.transformer.TransformerException;
import org.mule.config.DefaultMuleConfiguration;
import org.mule.config.annotations.endpoints.Channel;
import org.mule.config.builders.SimpleConfigurationBuilder;
import org.mule.context.DefaultMuleContextBuilder;
import org.mule.ibeans.IBeansContext;
import org.mule.ibeans.IBeansException;
import org.mule.ibeans.config.ChannelConfigBuilder;
import org.mule.ibeans.internal.config.IBeansMuleContextBuilder;
import org.mule.ibeans.internal.config.IBeansMuleContextFactory;
import org.mule.ibeans.transformers.CommonTransformers;
import org.mule.tck.AbstractMuleTestCase;
import org.mule.transformer.types.DataTypeFactory;
import org.mule.transformer.types.SimpleDataType;
import org.mule.util.IOUtils;
import org.mule.utils.AnnotationMetaData;
import org.mule.utils.AnnotationUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 * TODO
 */
public abstract class AbstractIBeansTestCase extends AbstractMuleTestCase
{
    protected IBeansContext iBeansContext;


    protected AbstractIBeansTestCase()
    {
        // Ensure no illegal annotations are used on this test class
        validateTestClass();
    }

    /**
     * Registers an annotated bean to the context
     *
     * @param beans
     * @throws IBeansException
     */
    public void registerBeans(Object... beans) throws IBeansException
    {
        for (int i = 0; i < beans.length; i++)
        {
            Object bean = beans[i];
            String name = bean.getClass().getSimpleName();
            if (bean instanceof NamedObject && ((NamedObject) bean).getName() != null)
            {
                name = ((NamedObject) bean).getName();
            }
            try
            {
                muleContext.getRegistry().registerObject(name, bean);
            }
            catch (RegistrationException e)
            {
                throw new IBeansException("Failed to register bean for testing: " + bean, e);
            }
        }
    }

    @Override
    protected MuleContext createMuleContext() throws Exception
    {
        setStartContext(true);
        muleContext = doCreateMuleContext();
        iBeansContext = muleContext.getRegistry().lookupObject(IBeansContext.class);
        assertNotNull("Integration context cannot be null", iBeansContext);

        //Add support for the MockIntegrationBean injector
        muleContext.getRegistry().registerObject(MockIntegrationBeansAnnotationProcessor.NAME, new MockIntegrationBeansAnnotationProcessor(muleContext));

        //Allow the testcase to use annotations
        muleContext.getRegistry().registerObject(this.getClass().getName(), this);

        //Load the common transformers
        muleContext.getRegistry().registerObject("_commonTransformers", new CommonTransformers());

        return muleContext;
    }

    protected void validateTestClass()
    {
        List<AnnotationMetaData> annos = AnnotationUtils.getAllMethodAnnotations(this.getClass());

        if (annos.size() > 0)
        {
            for (AnnotationMetaData data : annos)
            {
                if (data.getAnnotation().annotationType().isAnnotationPresent(Channel.class))
                {
                    throw new IllegalStateException("You cannot use verb annotations such as @Send or @Receive on a test class: " + data.toString());
                }
            }
        }
    }

    protected ChannelConfigBuilder createChannelBuilder(String id, String uri) throws IBeansException
    {
        return new ChannelConfigBuilder(id, uri, muleContext);
    }

    private MuleContext doCreateMuleContext() throws Exception
    {
        // Should we set up the manager for every method?
        MuleContext context;
        if (getTestInfo().isDisposeManagerPerSuite() && muleContext != null)
        {
            context = muleContext;
        }
        else
        {
            MuleContextFactory muleContextFactory = new IBeansMuleContextFactory();
            List<ConfigurationBuilder> builders = new ArrayList<ConfigurationBuilder>();
            builders.add(new SimpleConfigurationBuilder(getStartUpProperties()));
            builders.add(getBuilder());
            addBuilders(builders);
            DefaultMuleContextBuilder contextBuilder = new IBeansMuleContextBuilder();
            configureMuleContext(contextBuilder);
            context = muleContextFactory.createMuleContext(builders, contextBuilder);
            if (!isGracefulShutdown())
            {
                ((DefaultMuleConfiguration) context.getConfiguration()).setShutdownTimeout(0);
            }
        }
        return context;
    }

    protected <T> T loadData(String resource, DataType<T> type) throws IOException, TransformerException
    {
        InputStream in = IOUtils.getResourceAsStream(resource, getClass());
        assertNotNull("Resource stream for: " + resource + " must not be null", in);
        return getDataAs(in, type);
    }

    protected <T> T getDataAs(Object data, DataType<T> type) throws TransformerException
    {
        return iBeansContext.transform(data, type);
    }

    protected String loadData(String resource) throws TransformerException, IOException
    {
        return loadData(resource, new SimpleDataType<String>(String.class));
    }

    /**
     * A mock return for a method call that will load data and transform it into the return type set on the iBean.
     *
     * @param resource the resource file name that contains the data you wish to load
     * @param ibean    the ibean that is being tested
     * @return a Mockito {@link org.mockito.stubbing.Answer} implemementation that will load the data when requested
     */
    protected Answer withData(final String resource, final String mimeType, final Object ibean)
    {
        return new Answer()
        {
            public Object answer(InvocationOnMock invocation) throws Throwable
            {
                DataType ret = ((MockIBean) ibean).ibeanReturnType();
                if (ret == null)
                {
                    ret = new DataTypeFactory().createFromReturnType(invocation.getMethod());
                }
                Object data = loadData(resource, ret);
                ((MockIBean) ibean).ibeanSetMimeType(mimeType);
                return data;
            }
        };
    }

    protected Answer withXmlData(final String resource, final Object ibean)
    {
        return withData(resource, "text/xml", ibean);
    }

    protected Answer withRssData(final String resource, final Object ibean)
    {
        return withData(resource, "application/rss+xml", ibean);
    }

    protected Answer withAtomData(final String resource, final Object ibean)
    {
        return withData(resource, "application/atom+xml", ibean);
    }

    protected Answer withJsonData(final String resource, final Object ibean)
    {
        return withData(resource, "application/json", ibean);
    }

    protected Answer withTextData(final String resource, final Object ibean)
    {
        return withData(resource, "text/plain", ibean);
    }

    /**
     * A mock return for a method call that will load data and transform it into the return type set on the iBean.
     *
     * @param resource   the resource file name that contains the data you wish to load
     * @param returnType the Java type that the data should be converted to
     * @return a Mockito {@link org.mockito.stubbing.Answer} implemementation that will load the data when requested
     */
    protected Answer withData(final String resource, final Class returnType)
    {
        return new Answer()
        {
            public Object answer(InvocationOnMock
                    invocation) throws Throwable
            {
                return loadData(resource, new DataTypeFactory().create(returnType));
            }
        };
    }
}
