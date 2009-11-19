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
import org.mule.api.registry.ResolverException;
import org.mule.api.registry.TransformCriteria;
import org.mule.api.registry.TransformerResolver;
import org.mule.api.transformer.Transformer;
import org.mule.config.i18n.CoreMessages;
import org.mule.module.json.transformers.JsonToObject;
import org.mule.module.json.transformers.ObjectToJson;
import org.mule.utils.AnnotationUtils;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.map.MapperConfig;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * TODO
 */
public class JsonTransformerResolver implements TransformerResolver, MuleContextAware, Disposable
{
    /**
     * logger used by this class
     */
    protected transient final Log logger = LogFactory.getLog(JsonTransformerResolver.class);
    private MuleContext muleContext;

    //We cache the the transformers, this will get cleared when the server shuts down
    private Map<String, Transformer> transformerCache = new WeakHashMap<String, Transformer>();

    //We cache the JAXB classes so we don't scan them each time a transformer is needed
    private Set<Class> jsonClasses = new HashSet<Class>();

    public void setMuleContext(MuleContext context)
    {
        muleContext = context;
    }

    public Transformer resolve(TransformCriteria criteria) throws ResolverException
    {
        boolean marshal = false;
        Class annotatedType = null;

        //Check the cache before we start scanning classes
        String cacheKey = null;
        if (jsonClasses.contains(criteria.getOutputType()))
        {
            cacheKey = criteria.getOutputType().getName() + "-unmarshal";
            annotatedType = criteria.getOutputType();
            marshal = false;
        }
        else
        {
            for (int i = 0; i < criteria.getInputTypes().length; i++)
            {
                if (jsonClasses.contains(criteria.getInputTypes()[i]))
                {
                    cacheKey = criteria.getInputTypes()[i].getName() + "-marshal";
                    annotatedType = criteria.getInputTypes()[i];
                    marshal = true;
                    break;
                }

            }
        }
        //Check the cache
        Transformer t = transformerCache.get(cacheKey);

        if (t != null)
        {
            return t;
        }

        ObjectMapper mapper;
        try
        {
            //Scan the input and out put classes for Json annotations, we'll only do this once for
            //each transform combination
            if (annotatedType == null)
            {
                annotatedType = criteria.getOutputType();

                boolean isJson = (
                        AnnotationUtils.hasAnnotationWithPackage(Constants.ANNOTATIONS_PACKAGE_NAME, annotatedType));
                if (!isJson)
                {
                    marshal = true;
                    for (int j = 0; j < criteria.getInputTypes().length; j++)
                    {
                        annotatedType = criteria.getInputTypes()[j];
                        isJson = AnnotationUtils.hasAnnotationWithPackage(Constants.ANNOTATIONS_PACKAGE_NAME, annotatedType);
                        if (isJson)
                        {
                            break;
                        }
                        annotatedType = null;
                    }
                }
            }

            if (annotatedType == null)
            {
                return null;
            }

            jsonClasses.add(annotatedType);
            mapper = muleContext.getRegistry().lookupObject(ObjectMapper.class);
            if (mapper == null)
            {
                logger.info("No common Json Mapper context configured, creating a local one for: " + annotatedType);
                mapper = new ObjectMapper();
            }

            if (marshal)
            {
                ObjectToJson otj = new ObjectToJson();
                otj.setSourceClass(annotatedType);
                otj.setReturnClass(criteria.getOutputType());
                otj.setMapper(mapper);
                muleContext.getRegistry().applyProcessorsAndLifecycle(otj);
                t = otj;
            }
            else
            {
                JsonToObject jto = new JsonToObject();
                jto.setReturnClass(criteria.getOutputType());
                jto.setMapper(mapper);
                muleContext.getRegistry().applyProcessorsAndLifecycle(jto);
                t = jto;
            }

            transformerCache.put(cacheKey, t);
            return t;

        }
        catch (Exception e)
        {
            //TODO
            throw new ResolverException(CoreMessages.createStaticMessage("Failed to unmarshal"), e);
        }
    }

    protected ScanResult findMixinForCriteria(MapperConfig mc, TransformCriteria criteria)
    {
        if (mc.findMixInClassFor(criteria.getOutputType()) != null)
        {
            return new ScanResult(false, criteria.getOutputType());
        }
        else
        {
            for (int i = 0; i < criteria.getInputTypes().length; i++)
            {
                if (mc.findMixInClassFor(criteria.getInputTypes()[i]) != null)
                {
                    return new ScanResult(true, criteria.getInputTypes()[i]);
                }
            }
        }
        return null;
    }


    public void transformerChange(Transformer transformer, RegistryAction registryAction)
    {
        //nothing to do
    }

    public void dispose()
    {
        transformerCache.clear();
        jsonClasses.clear();
    }


    private class ScanResult
    {
        private boolean inbound;
        private Class annotatedType;

        private ScanResult(boolean inbound, Class annotatedType)
        {
            this.inbound = inbound;
            this.annotatedType = annotatedType;
        }

        public boolean isInbound()
        {
            return inbound;
        }

        public Class getAnnotatedType()
        {
            return annotatedType;
        }
    }
}