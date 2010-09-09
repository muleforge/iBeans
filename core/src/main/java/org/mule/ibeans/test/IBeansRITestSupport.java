/*
 * $Id: IBeansTestSupport.java 346 2010-05-18 19:57:33Z ross $
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.test;

import org.mule.DefaultMuleMessage;
import org.mule.api.MuleContext;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.api.NamedObject;
import org.mule.api.annotations.meta.Channel;
import org.mule.api.config.ConfigurationBuilder;
import org.mule.api.config.MuleProperties;
import org.mule.api.context.MuleContextBuilder;
import org.mule.api.context.MuleContextFactory;
import org.mule.api.registry.RegistrationException;
import org.mule.api.transformer.TransformerException;
import org.mule.config.builders.DefaultsConfigurationBuilder;
import org.mule.config.builders.SimpleConfigurationBuilder;
import org.mule.context.DefaultMuleContextBuilder;
import org.mule.ibeans.IBeansContext;
import org.mule.ibeans.config.PropertiesConfigurationBuilder;
import org.mule.ibeans.internal.config.IBeansMuleContextBuilder;
import org.mule.ibeans.internal.config.IBeansMuleContextFactory;
import org.mule.ibeans.transformers.CommonTransformers;
import org.mule.module.annotationx.config.ChannelConfigBuilder;
import org.mule.module.ibeans.config.MockIntegrationBeansAnnotationProcessor;
import org.mule.module.ibeans.spi.MuleIBeansPlugin;
import org.mule.tck.TestingWorkListener;
import org.mule.util.FileUtils;
import org.mule.util.IOUtils;
import org.mule.util.StringMessageUtils;
import org.mule.util.annotation.AnnotationMetaData;
import org.mule.util.annotation.AnnotationUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.activation.MimeTypeParseException;

import org.ibeans.annotation.Return;
import org.ibeans.api.DataType;
import org.ibeans.api.IBeansException;
import org.ibeans.api.Response;
import org.ibeans.api.channel.MimeType;
import org.ibeans.api.channel.MimeTypes;
import org.ibeans.impl.support.datatype.DataTypeFactory;
import org.ibeans.impl.support.datatype.SimpleDataType;
import org.ibeans.impl.test.MockIBean;
import org.ibeans.impl.test.MockMessageCallback;
import org.ibeans.spi.IBeansPlugin;
import org.junit.After;
import org.junit.Before;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.junit.Assert.assertNotNull;

/**
 * A Helper class for Test cases that manages the creation and destruction of the iBeans context
 * for the test.  This class alos offers callback methods for registering annotated objects with the
 * context {@link #registerBeans(Object...)} and support for easily adding properties to the context using
 * {@link #addStartUpProperties(java.util.Properties)}
 */
public abstract class IBeansRITestSupport
{
    /**
     * Top-level directories under <code>.mule</code> which are not deleted on each
     * test case recycle. This is required, e.g. to play nice with transaction manager
     * recovery service object store.
     */
    public static final String[] IGNORED_DOT_MULE_DIRS = new String[]{"transaction-log"};

    /** the underlying MuleContext for this iBeans test, typically developers will not need to use this */
    protected MuleContext muleContext;

    /**
     * The iBeans context available for this test *
     */
    protected IBeansContext iBeansContext;

    /**
     * Whether to start the iBeans context automatically, the default is true, but sometimes the developer may need
     * to delay starting the context until an external action can finish i.e. draining an email inbox or JMS queue
     * before test begins
     */
    protected boolean startContext = true;

    protected IBeansPlugin plugin;

   protected IBeansPlugin createPlugin()
   {
       return new MuleIBeansPlugin(muleContext);
   }

    protected IBeansRITestSupport()
    {
        // Ensure no illegal annotations are used on this test class
        validateTestClass();
    }

    /**
     * Whether to start the iBeans context automatically, the default is true, but sometimes the developer may need
     * to delay starting the context until an external action can finish i.e. draining an email inbox or JMS queue
     * before test begins
     * @return true if the context will be started when the tests starts (the default) or false otherwise
     */
    public boolean isStartContext()
    {
        return startContext;
    }

    /**
     * Whether to start the iBeans context automatically, the default is true, but sometimes the developer may need
     * to delay starting the context until an external action can finish i.e. draining an email inbox or JMS queue
     * before test begins
     * @param startContext true if the context will be started when the tests starts or false otherwise
     */
    protected void setStartContext(boolean startContext)
    {
        this.startContext = startContext;
    }

    /**
     * Start the context if not already started. The only reason to call this method from a test is if
     * {@link #setStartContext(boolean)} was called with false.
     * @throws Exception if the context fails to start
     */
    protected void startContext() throws Exception
    {
        if (isStartContext() && null != muleContext && !muleContext.isStarted())
        {
            muleContext.start();
        }
    }

    /**
     * Allows developers to override this template method to add properties to the contet at start up
     * @param properties the properties that will get added to the context
     */
    protected void addStartUpProperties(Properties properties)
    {
        //no-op, developers can override this method to add properties to the context
    }

    /**
     * Registers an annotated bean to the context
     *
     * @param beans one or more object instance annotated with iBean configuration
     * @throws org.mule.ibeans.IBeansException
     *          if one of the beans fails to register
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


    /**
     * Allows the test case to register additional {@link org.mule.api.config.ConfigurationBuilder} instnces which will
     * be used to construct the iBeansContext. It is unlikely that developers will need to do this, but iBeans itself
     * does use this in testing scenarios
     * @param builders a colloection of builders that will be used to configure the instance
     */
    protected void addBuilders(List<ConfigurationBuilder> builders)
    {
        //No op
    }

    /**
     * Override this method to set properties of the MuleContextBuilder before it is
     * used to create the MuleContext.
     *
     * @param contextBuilder The context builder before the context has been created
     */
    protected void configureMuleContext(MuleContextBuilder contextBuilder)
    {
        contextBuilder.setWorkListener(new TestingWorkListener());
    }

    /**
     * Responsible for creating the iBeans context and underlying MuleContext
     * @throws Exception if for any reason the context can not be created
     */
    @Before
    public final void initialiseIBeans() throws Exception
    {
        System.out.println(StringMessageUtils.getBoilerPlate("Testing: " + toString(), '=', 80));
        createContexts();
        startContext();
    }

    /**
     * Responsible for disposing of the iBeans context and underlying MuleContext, cleaning up any residual
     * data
     */
    @After
    public final void disposeIBeans()
    {
        try
        {
            if (muleContext != null && !(muleContext.isDisposed() || muleContext.isDisposing()))
            {
                muleContext.dispose();

                final String workingDir = muleContext.getConfiguration().getWorkingDirectory();
                // do not delete TM recovery object store, everything else is good to go
                FileUtils.deleteTree(FileUtils.newFile(workingDir), IGNORED_DOT_MULE_DIRS);
            }
            FileUtils.deleteTree(FileUtils.newFile("./ActiveMQ"));
        }
        finally
        {
            muleContext = null;
        }
    }

    /**
     * Responsible for actually creating the iBeans context. This method delegates to {@link #createMuleContext()} to
     * construct the underlying MuleContext.
     * @throws Exception if either of the contexted cannot be created
     */
    protected void createContexts() throws Exception
    {
        setStartContext(true);
        muleContext = createMuleContext();
        iBeansContext = muleContext.getRegistry().lookupObject(IBeansContext.class);
        assertNotNull("Integration context cannot be null", iBeansContext);

        //Add support for the MockIntegrationBean injector
        muleContext.getRegistry().registerObject(MockIntegrationBeansAnnotationProcessor.NAME, new MockIntegrationBeansAnnotationProcessor());

        //Allow the testcase to use annotations
        muleContext.getRegistry().registerObject(this.getClass().getName(), this);

        //Load the common transformers
        muleContext.getRegistry().registerObject("_commonTransformers", new CommonTransformers());

         plugin = createPlugin();
    }

    /**
     * Creates th underlying MuleContext for this ibeans test case
     * @return the new MuleContext instance
     * @throws Exception if the instance cannot be created
     */
    private MuleContext createMuleContext() throws Exception
    {
        MuleContext context;

        MuleContextFactory muleContextFactory = new IBeansMuleContextFactory();
        List<ConfigurationBuilder> builders = new ArrayList<ConfigurationBuilder>();
        Properties p = new Properties();
        addStartUpProperties(p);
        builders.add(new SimpleConfigurationBuilder(p));
        builders.add(new DefaultsConfigurationBuilder());
        //Enable annotations processing but do not use the scanning builder
        builders.add(new TestSupportConfigurationBuilder());
        PropertiesConfigurationBuilder pcb = new PropertiesConfigurationBuilder();
        pcb.setLoadFromUserHome(true);
        builders.add(pcb);
        addBuilders(builders);
        DefaultMuleContextBuilder contextBuilder = new IBeansMuleContextBuilder();
        configureMuleContext(contextBuilder);
        context = muleContextFactory.createMuleContext(builders, contextBuilder);

        return context;
    }

    /**
     * Validates that no {@link org.mule.config.annotations.endpoints.Channel} annotations are used on the Test implementation.
     */
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

    /**
     * Short cut method for creating a {@link ChannelBuilder}
     * @param id the id for the channel, this is also the registry id
     * @param uri the URI to construct the channel from
     * @return A new Channel builder instance
     * @throws IBeansException if the id is not unique or the URI is invalid
     */
    protected ChannelConfigBuilder createChannelBuilder(String id, String uri) throws IBeansException
    {
        try
        {
            return new ChannelConfigBuilder(id, uri, muleContext);
        }
        catch (MuleException e)
        {
            throw new IBeansException(e);
        }
    }


    protected <T> T loadData(String resource, DataType<T> type) throws IOException, TransformerException, MimeTypeParseException
    {
        InputStream in = IOUtils.getResourceAsStream(resource, getClass());
        assertNotNull("Resource stream for: " + resource + " must not be null", in);
        return getDataAs(in, type);
    }

    protected <T> T getDataAs(InputStream data, DataType<T> type) throws TransformerException, MimeTypeParseException
    {
        return iBeansContext.transform(data, type);
    }

    protected String loadData(String resource) throws TransformerException, IOException, MimeTypeParseException
    {
        return loadData(resource, new SimpleDataType<String>(String.class));
    }

    protected Answer withXmlData(final String resource, MockMessageCallback callback, final Object ibean)
    {
        return withData(resource, MimeTypes.XML.toString(), callback, ibean);
    }

    protected Answer withRssData(final String resource, MockMessageCallback callback, final Object ibean)
    {
        return withData(resource, MimeTypes.RSS.toString(), callback, ibean);
    }

    protected Answer withAtomData(final String resource, MockMessageCallback callback, final Object ibean)
    {
        return withData(resource, MimeTypes.ATOM.toString(), callback, ibean);
    }

    protected Answer withJsonData(final String resource, MockMessageCallback callback, final Object ibean)
    {
        return withData(resource, MimeTypes.JSON.toString(), callback, ibean);
    }

    protected Answer withTextData(final String resource, MockMessageCallback callback, final Object ibean)
    {
        return withData(resource, MimeTypes.TEXT.toString(), callback, ibean);
    }

////
    protected Answer withXmlData(final String resource, final Object ibean)
    {
        return withData(resource, MimeTypes.XML, null, ibean);
    }

    protected Answer withRssData(final String resource, final Object ibean)
    {
        return withData(resource, MimeTypes.RSS, null, ibean);
    }

    protected Answer withAtomData(final String resource, final Object ibean)
    {
        return withData(resource, MimeTypes.ATOM, null, ibean);
    }

    protected Answer withJsonData(final String resource, final Object ibean)
    {
        return withData(resource, MimeTypes.JSON, null, ibean);
    }

    protected Answer withTextData(final String resource, final Object ibean)
    {
        return withData(resource, MimeTypes.TEXT, null, ibean);
    }

    /**
     * A mock return for a method call that will load data and transform it into the return type set on the iBean.
     *
     * @param resource   the resource file name that contains the data you wish to load
     * @param returnType the Java type that the data should be converted to
     * @return a Mockito {@link org.mockito.stubbing.Answer} implementation that will load the data when requested
     */
    protected Answer withData(final String resource, final Class returnType)
    {
        return new Answer()
        {
            public Object answer(InvocationOnMock
                    invocation) throws Throwable
            {
                return loadData(resource, DataTypeFactory.create(returnType));
            }
        };
    }

    /**
     * A mock return for a method call that will load data and transform it into the return type set on the iBean.
     *
     * @param resource the resource file name that contains the data you wish to load
     * @param ibean    the ibean that is being tested
     * @param mimeType the mime type of the data
     * @param callback a callback can be used to manipulate the MuleMessage before it it gets returned
     * @return a Mockito {@link org.mockito.stubbing.Answer} implementation that will load the data when requested
     */
    protected Answer withData(final String resource, final MimeType mimeType, final MockMessageCallback callback, final Object ibean)
    {
        return new Answer()
        {
            public Object answer(InvocationOnMock invocation) throws Throwable
            {
                MimeType mime = mimeType;
                DataType ret = ((MockIBean)ibean).ibeanReturnType();
                if(ret!=null) ret.setMimeType(mime.toString());
                Object data;

                /**
                 * We need to have some special handling when dealing with a Mockito mock
                 * 1) If the return type on the ibeans is not set, use the method return type
                 * 2) the return annotation changes the return type so use the one defined on the actual Method
                 * 3) If the return type and the method return type are not assignable, then use the method return type
                 */
                if (ret == null || invocation.getMethod().isAnnotationPresent(Return.class) ||
                        !invocation.getMethod().getReturnType().isAssignableFrom(ret.getType()))
                {
                    ret = DataTypeFactory.createFromReturnType(invocation.getMethod());
                    mime = null;
                }

                data = loadData(resource, ret);
                ((MockIBean)ibean).ibeanSetMimeType(mime);
                ((MockIBean)ibean).ibeanSetMessageCallback(callback);

                Response response;
                Map<String, Object> headers = null;
                if(mime!=null)
                {
                    headers = new HashMap<String, Object>();
                    headers.put(MuleProperties.CONTENT_TYPE_PROPERTY, mime.toString());
                }
                response = plugin.createResponse(data, headers, null);
                if(callback!=null)
                {
                    callback.onMessage(response);
                }
                return data;
            }
        };
    }






















    /**
     * A mock return for a method call that will load data and transform it into the return type set on the iBean.
     *
     * @param resource the resource file name that contains the data you wish to load
     * @param ibean    the ibean that is being tested
     * @return a Mockito {@link org.mockito.stubbing.Answer} implementation that will load the data when requested
     */
    protected Answer withData(final String resource, final String mimeType, final MockMessageCallback callback, final Object ibean)
    {
        return new Answer()
        {
            public Object answer(InvocationOnMock invocation) throws Throwable
            {
                String mime = mimeType;
                DataType ret = ((MockIBean)ibean).ibeanReturnType();
                if(ret!=null) ret.setMimeType(mime);
                Object data;

                /**
                 * We need to have some special handling when dealing with a Mockito mock
                 * 1) If the return type on the ibeans is not set, use the method return type
                 * 2) the return annotation changes the return type so use the one defined on the actual Method
                 * 3) If the return type and the method return type are not assignable, then use the method return type
                 */
                if (ret == null || invocation.getMethod().isAnnotationPresent(Return.class) ||
                        !invocation.getMethod().getReturnType().isAssignableFrom(ret.getType()))
                {
                    ret = new DataTypeFactory().createFromReturnType(invocation.getMethod());
                    mime = null;
                }

                data = loadData(resource, ret);
                if(mime!=null)
                {
                    ((MockIBean)ibean).ibeanSetMimeType(new MimeType(mime));
                }
                ((MockIBean)ibean).ibeanSetMessageCallback(callback);

                return data;
            }
        };
    }

    protected MuleMessage createMuleMessage(Object payload, Map properties)
    {
        return new DefaultMuleMessage(payload, properties, null, null, muleContext);
    }

    protected MuleMessage createMuleMessage(Object payload)
    {
        return new DefaultMuleMessage(payload, muleContext);
    }

    public int generateId()
    {
        Double d = Math.random() * 10000000L;
        return d.intValue();
    }
}