/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.internal;

import org.mule.api.MuleContext;
import org.mule.api.MuleException;
import org.mule.api.context.MuleContextAware;
import org.mule.api.registry.PreInitProcessor;
import org.mule.ibeans.api.application.Transformer;
import org.mule.utils.AnnotationMetaData;
import org.mule.utils.AnnotationUtils;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Will check all method level annotations to see if they are {@link org.mule.config.annotations.endpoints.Channel} annotations.
 */
public class AnnotatedTransformerObjectProcessor implements PreInitProcessor, MuleContextAware
{

    private MuleContext muleContext;

    public void setMuleContext(MuleContext context)
    {
        this.muleContext = context;
    }

    public Object process(Object object)
    {
        List<AnnotationMetaData> annos = AnnotationUtils.getMethodAnnotations(object.getClass(), Transformer.class);

        if (annos.size() == 0)
        {
            return object;
        }
        for (AnnotationMetaData data : annos)
        {
            try
            {
                Transformer anno = (Transformer) data.getAnnotation();
                AnnotatedTransformerProxy trans = new AnnotatedTransformerProxy(
                        anno.priorityWeighting(),
                        object, (Method) data.getMember(), anno.sourceTypes(),
                        null /*anno.sourceMimeType()*/, null /*anno.resultMimeType()*/);

                muleContext.getRegistry().registerTransformer(trans);
            }
            catch (MuleException e)
            {
                throw new RuntimeException(e);
            }
        }
        return object;
    }
}