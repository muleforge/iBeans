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

import org.mule.ibeans.api.application.Transformer;
import org.mule.tck.testmodels.fruit.Apple;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import javax.annotation.PostConstruct;

import org.codehaus.jackson.map.ObjectMapper;

/**
 * TODO
 */
public class JsonCustomTransformerWithMixins
{
    private ObjectMapper mapper;

    @PostConstruct
    public void init()
    {
        mapper = new ObjectMapper();
        mapper.getSerializationConfig().addMixInAnnotations(Apple.class, AppleMixin.class);
        mapper.getDeserializationConfig().addMixInAnnotations(Apple.class, AppleMixin.class);
    }

    @Transformer(sourceTypes = {InputStream.class, byte[].class})
    public Apple toApple(String in) throws IOException
    {
        return mapper.readValue(in, Apple.class);
    }

    @Transformer
    public String fromApple(Apple apple) throws IOException
    {
        StringWriter w = new StringWriter();
        mapper.writeValue(w, apple);
        return w.toString();

    }

}