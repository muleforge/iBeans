/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.web.json;

import org.mule.api.MuleContext;
import org.mule.ibeans.internal.ObjectResolver;
import org.mule.utils.AnnotationUtils;

import java.lang.reflect.Method;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * This resolver is used by the transform engine to inject a {@link org.codehaus.jackson.map.ObjectMapper} into a method that requires it.
 * A shared ObjectMapper context can be created for the application and stored in the registry, this will get injected
 * into any transform methods that add {@link org.codehaus.jackson.map.ObjectMapper} to the method signature. (Also users will
 * be able to use the {@link javax.inject.Inject} annotation to inject it into any object).
 * <p/>
 * If there is no shared Object Mapper one will be created for the transformer using the return type as the Json root element.
 */
public class JsonMapperResolver implements ObjectResolver
{
    /**
     * logger used by this class
     */
    protected transient final Log logger = LogFactory.getLog(JsonMapperResolver.class);

    public Object findObject(Class type, Method method, MuleContext context) throws Exception
    {
        ObjectMapper mapper;
        Class annotatedType = method.getReturnType();
        boolean isJson = AnnotationUtils.hasAnnotationWithPackage(Constants.ANNOTATIONS_PACKAGE_NAME, annotatedType);
        int i = 0;
        if (!isJson)
        {
            for (int j = 0; j < method.getParameterTypes().length; j++)
            {
                annotatedType = method.getParameterTypes()[j];
                isJson = AnnotationUtils.hasAnnotationWithPackage(Constants.ANNOTATIONS_PACKAGE_NAME, method.getParameterTypes()[j]);
                if (isJson)
                {
                    break;
                }
            }
        }

        if (!isJson)
        {
            return null;
        }

        mapper = context.getRegistry().lookupObject(ObjectMapper.class);
        if (mapper == null)
        {
            logger.info("No common Json Object Mapper configured, creating a local one for: " + method);
            mapper = new ObjectMapper();
        }
        return mapper;

    }
}