/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.example.routing.params;

import org.mule.api.annotations.param.InboundHeaders;
import org.mule.module.annotationx.api.ReceiveAndReply;
import org.mule.tck.testmodels.fruit.Banana;
import org.mule.tck.testmodels.fruit.Fruit;

import java.util.List;
import java.util.Map;

/**
 * TODO
 */
public class HeadersWithTypeBean
{
    @ReceiveAndReply(uri = "vm://header", id = "header")
    public Banana processHeader(@InboundHeaders("banana") Banana banana)
    {
        return banana;
    }

    @ReceiveAndReply(uri = "vm://headers", id = "headers")
    public Map<String, Fruit> processHeaders(@InboundHeaders("apple, orange") Map<String, Fruit> fruit)
    {
        return fruit;
    }

    @ReceiveAndReply(uri = "vm://headersList", id = "headersList")
    public List<Fruit> processHeadersList(@InboundHeaders("apple, banana, orange?") List<Fruit> fruit)
    {
        return fruit;
    }

    @ReceiveAndReply(uri = "vm://headersAllList", id = "headersAllList")
    public List<Fruit> processHeadersAllList(@InboundHeaders("*") List<Fruit> fruit)
    {
        return fruit;
    }

    @ReceiveAndReply(uri = "vm://headersAll", id = "headersAll")
    public Map<String, Fruit> processHeadersAll(@InboundHeaders("*") Map<String, Fruit> fruit)
    {
        return fruit;
    }

    @ReceiveAndReply(uri = "vm://headersCount", id = "headersCount")
    public int processHeadersCount(@InboundHeaders("#") int count)
    {
        //TODO type conversion not worknig for primitives it seems
        return count;
    }

}