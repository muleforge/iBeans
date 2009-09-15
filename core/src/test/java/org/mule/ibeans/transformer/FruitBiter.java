/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.transformer;

import org.mule.ibeans.api.application.ReceiveAndReply;
import org.mule.tck.testmodels.fruit.Fruit;

/**
 * A test service that requires Mule to implicitly convert a string to the Fruit object received by the biteFruit method
 * using a Transformer defined in the {@link org.mule.ibeans.transformer.FruitTransformers} class.
 */
public class FruitBiter
{
    @ReceiveAndReply(uri = "vm://test")
    public Fruit biteFruit(Fruit fruit)
    {
        fruit.bite();
        return fruit;
    }
}
