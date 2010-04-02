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
import org.mule.lifecycle.NotificationLifecycleObject;
import org.mule.lifecycle.phases.MuleContextDisposePhase;
import org.mule.utils.AnnotationMetaData;
import org.mule.utils.AnnotationUtils;

import java.lang.reflect.Method;
import java.util.List;

import javax.annotation.PreDestroy;

/**
 * TODO
 */
public class JSR250MulecontextDisposePhase extends MuleContextDisposePhase
{
    public JSR250MulecontextDisposePhase()
    {
        super();
        //We need add Object.class since the default lifecyclePhase only recognises a preconfigured set of supported interfaces
        //With the JSR-250 annotations no interfaces are necessary, thus we need to scan all objects in the registry for
        //a PreDestroy annotation.  Typically there is nver more that a few hundred objects in the registry so this shouldn't
        //add to much overhead
        getOrderedLifecycleObjects().add(new NotificationLifecycleObject(Object.class));
    }

    @Override
    public void applyLifecycle(Object o) throws LifecycleException
    {
        if (o == null)
        {
            return;
        }
        if (ignoreType(o.getClass()))
        {
            return;
        }
        super.applyLifecycle(o);

        List<AnnotationMetaData> annos = AnnotationUtils.getMethodAnnotations(o.getClass(), PreDestroy.class);
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