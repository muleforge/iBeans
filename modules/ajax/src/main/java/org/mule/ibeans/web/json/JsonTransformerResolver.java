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
import org.mule.api.context.MuleContextAware;
import org.mule.api.lifecycle.Disposable;
import org.mule.api.registry.RegistrationException;
import org.mule.api.registry.ResolverException;
import org.mule.api.registry.TransformerResolver;
import org.mule.api.transformer.DataType;
import org.mule.api.transformer.Transformer;
import org.mule.config.i18n.CoreMessages;
import org.mule.module.json.transformers.JsonToObject;
import org.mule.module.json.transformers.ObjectToJson;
import org.mule.transformer.simple.ObjectToString;

import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * TODO
 */
public class JsonTransformerResolver implements TransformerResolver, MuleContextAware, Disposable
{
    public static final String JSON_MIME_TYPE = "application/json";
    /**
     * logger used by this class
     */
    protected transient final Log logger = LogFactory.getLog(JsonTransformerResolver.class);
    private MuleContext muleContext;

    //We cache the the transformers, this will get cleared when the server shuts down
    private Map<String, Transformer> transformerCache = new WeakHashMap<String, Transformer>();

    private JsonMapperResolver resolver;

    public void setMuleContext(MuleContext context)
    {
        muleContext = context;
    }

    public Transformer resolve(DataType source, DataType result) throws ResolverException
    {
        //Check the cache
        Transformer t = transformerCache.get(source.toString() + result.toString());

        if (t != null)
        {
            return t;
        }

        try
        {
            ObjectMapper mapper = getMapperResolver().resolve(ObjectMapper.class, source, result, muleContext);

            if(mapper==null)
            {
                return null;
            }
            boolean marshal;
            Class annotatedType;

            //Check the class caches before we start scanning classes
            if (getMapperResolver().getMatchingClasses().contains(result.getType()))
            {
                annotatedType = result.getType();
                //Set the correct mime type on the raw type
                source.setMimeType(JSON_MIME_TYPE);
                marshal = false;
            }
            else if (getMapperResolver().getMatchingClasses().contains(source.getType()))
            {
                annotatedType = source.getType();
                //Set the correct mime type on the raw type
                result.setMimeType(JSON_MIME_TYPE);
                marshal = true;
            }
            else
            {
                return null;
            }


            //At this point we know we are dealing with Json, now lets check the registry to see if there is an exact
            //transformer that matches our criteria
            List<Transformer> ts = muleContext.getRegistry().lookupTransformers(source, result);
            //ObjectToString continues to cause pain to auto transforms, here
            //we check explicitly since we want to generate a Json transformer if
            //one does not already exist in the context
            if (ts.size() == 1 && !(ts.get(0) instanceof ObjectToString))
            {
                t = ts.get(0);
            }
            else if (marshal)
            {
                ObjectToJson otj = new ObjectToJson();
                otj.setSourceClass(annotatedType);
                otj.setReturnDataType(result);
                otj.setMapper(mapper);
                muleContext.getRegistry().applyProcessorsAndLifecycle(otj);
                t = otj;
            }
            else
            {
                JsonToObject jto = new JsonToObject();
                jto.setReturnDataType(result);
                jto.setMapper(mapper);
                muleContext.getRegistry().applyProcessorsAndLifecycle(jto);
                t = jto;
            }

            transformerCache.put(source.toString() + result.toString(), t);
            return t;

        }
        catch (Exception e)
        {
            throw new ResolverException(CoreMessages.createStaticMessage("Failed to unmarshal"), e);
        }
    }

    public void transformerChange(Transformer transformer, RegistryAction registryAction)
    {
        //nothing to do
    }

    public void dispose()
    {
        transformerCache.clear();
    }

    protected JsonMapperResolver getMapperResolver() throws ResolverException
    {
        if(resolver==null)
        {
            try
            {
                resolver = muleContext.getRegistry().lookupObject(JsonMapperResolver.class);
            }
            catch (RegistrationException e)
            {
                throw new ResolverException(e.getI18nMessage(), e);
            }
        }
        return resolver;
    }
}