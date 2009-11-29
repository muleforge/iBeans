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
import org.mule.ibeans.internal.AbstractAnnotatedTransformerArgumentResolver;

import org.codehaus.jackson.map.ObjectMapper;

/**
 * This resolver is used by the transform engine to inject a {@link org.codehaus.jackson.map.ObjectMapper} into a method that requires it.
 * A shared ObjectMapper context can be created for the application and stored in the registry, this will get injected
 * into any transform methods that add {@link org.codehaus.jackson.map.ObjectMapper} to the method signature. (Also users will
 * be able to use the {@link javax.inject.Inject} annotation to inject it into any object).
 * <p/>
 * If there is no shared Object Mapper one will be created for the transformer using the return type as the Json root element.
 */
public class JsonMapperResolver extends AbstractAnnotatedTransformerArgumentResolver
{
    /**
     * {@inheritDoc}
     */
    protected Class getArgumentClass()
    {
        return ObjectMapper.class;
    }

    /**
     * {@inheritDoc}
     */
    protected Object createArgument(Class annotatedType, MuleContext context) throws Exception
    {
        return new ObjectMapper();
    }

    /**
     * {@inheritDoc}
     */
    protected String getAnnotationsPackageName()
    {
        return Constants.ANNOTATIONS_PACKAGE_NAME;
    }
}