/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.config.annotations.converters;

import org.mule.api.registry.MuleRegistry;
import org.mule.api.transformer.Transformer;
import org.mule.api.expression.PropertyConverter;
import org.mule.api.MuleContext;

import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Converts a String list of Transformer names into a List of {@link org.mule.api.transformer.Transformer} objects.
 */
public class TransformerConverter implements PropertyConverter
{
    public static final String DELIM = ",";

    public Object convert(String property, MuleContext context)
    {
        if (null != property)
        {
            List<Transformer> transformers = new LinkedList<Transformer>();
            StringTokenizer st = new StringTokenizer(property, DELIM);
            while (st.hasMoreTokens())
            {
                String key = st.nextToken().trim();
                Transformer transformer = context.getRegistry().lookupTransformer(key);

                if (transformer == null)
                {
                    throw new IllegalArgumentException(key);
                }
                transformers.add(transformer);
            }
            return transformers;
        }
        else
        {
            return null;
        }

    }

    public Class getType()
    {
        return TransformerList.class;
    }
}
