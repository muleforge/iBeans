/*
 * $Id:  $
 * -------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.ibeans.impl.test;

import java.util.List;

import org.ibeans.annotation.Template;
import org.ibeans.api.IBeanInvoker;
import org.ibeans.api.IBeansException;
import org.ibeans.impl.IntegrationBeanInvocationHandler;
import org.ibeans.impl.support.annotation.AnnotationMetaData;
import org.ibeans.impl.support.annotation.AnnotationUtils;
import org.ibeans.spi.IBeansPlugin;

/**
 * TODO
 */
public class MockIntegrationBeanInvocationHandler extends IntegrationBeanInvocationHandler
{
    public MockIntegrationBeanInvocationHandler(Class ibean, IBeansPlugin plugin, Object mock) throws IBeansException
    {
        super(ibean, plugin);
        //This is a bit cludgy.  The last interceptor is the invoker, so we switch it out for the mock version
        this.defaultInterceptorList.remove(defaultInterceptorList.size() - 1);
        IBeanInvoker invoker = plugin.getMockIBeanInvoker(mock);
        defaultInterceptorList.add(invoker);

        List<AnnotationMetaData> annos = AnnotationUtils.getAllMethodAnnotations(ibean);
        for (AnnotationMetaData metaData : annos)
        {
            if (metaData.getAnnotation() instanceof Template)
            {
                invoker.getTemplateHandler().getEvals().put(metaData.getMember().toString(), ((Template) metaData.getAnnotation()).value());
            }
        }
    }


}
