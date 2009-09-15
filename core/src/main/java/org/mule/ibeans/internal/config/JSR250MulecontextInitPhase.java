/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.internal.config;

import org.mule.api.lifecycle.LifecycleException;
import org.mule.config.i18n.CoreMessages;
import org.mule.lifecycle.phases.MuleContextInitialisePhase;
import org.mule.utils.AnnotationMetaData;
import org.mule.utils.AnnotationUtils;

import java.lang.reflect.Method;
import java.util.List;

import javax.annotation.PostConstruct;

/**
 * TODO
 */
public class JSR250MulecontextInitPhase extends MuleContextInitialisePhase
{
    @Override
    public void applyLifecycle(Object o) throws LifecycleException
    {
        //retain existing behaviour
        super.applyLifecycle(o);
        if (o == null)
        {
            return;
        }
        if (ignoreType(o.getClass()))
        {
            return;
        }
        List<AnnotationMetaData> annos = AnnotationUtils.getMethodAnnotations(o.getClass(), PostConstruct.class);
        if (annos.size() == 0)
        {
            return;
        }
        AnnotationMetaData current = null;
        try
        {
            for (AnnotationMetaData anno : annos)
            {
                current = anno;
                ((Method) anno.getMember()).invoke(o);
            }
        }
        catch (Exception e)
        {
            throw new LifecycleException(CoreMessages.failedToInvokeLifecycle(
                    (current == null ? "null" : current.getMember().getName()), o), e, this);
        }
    }
}
