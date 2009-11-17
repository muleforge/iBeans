/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.module.xml;

import org.mule.api.MuleContext;
import org.mule.ibeans.internal.ObjectResolver;
import org.mule.utils.AnnotationUtils;

import java.lang.reflect.Method;

import javax.xml.bind.JAXBContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This resolver is used by the transform engine to inject a JAXBContext into a method that requires it.
 * A shared JAXB context can be created for the application and stored in the registry, this will get injected
 * into any transform methods that add {@link javax.xml.bind.JAXBContext} to the method signature. (Also users will
 * be able to use the {@link javax.inject.Inject} annotation to inject it into any object).
 * <p/>
 * IF there is no shared JAXB context one will be created for the transformer using the return type as the Xml root element.
 */
public class JAXBContextResolver implements ObjectResolver
{
    /**
     * logger used by this class
     */
    protected transient final Log logger = LogFactory.getLog(JAXBContextResolver.class);

    public Object findObject(Class type, Method method, MuleContext context) throws Exception
    {
        JAXBContext jax;
        Class annotatedType = method.getReturnType();
        boolean isJAXB = AnnotationUtils.hasAnnotationWithPackage("javax.xml.bind.annotation", annotatedType);
        int i = 0;
        if (!isJAXB)
        {
            for (int j = 0; j < method.getParameterTypes().length; j++)
            {
                annotatedType = method.getParameterTypes()[j];
                isJAXB = AnnotationUtils.hasAnnotationWithPackage("javax.xml.bind.annotation", method.getParameterTypes()[j]);
                if (isJAXB)
                {
                    break;
                }
            }
        }

        if (!isJAXB)
        {
            return null;
        }

        jax = context.getRegistry().lookupObject(JAXBContext.class);
        if (jax == null)
        {
            logger.info("No common JAXB context configured, creating a local one for: " + method);
            jax = JAXBContext.newInstance(annotatedType);
        }
        return jax;

    }
}
