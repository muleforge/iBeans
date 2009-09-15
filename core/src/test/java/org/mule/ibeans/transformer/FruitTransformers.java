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

import org.mule.ibeans.api.application.Transformer;
import org.mule.tck.testmodels.fruit.Apple;
import org.mule.tck.testmodels.fruit.Banana;
import org.mule.tck.testmodels.fruit.Fruit;
import org.mule.tck.testmodels.fruit.Orange;

/**
 * Test fruit transformer
 */

public class FruitTransformers
{
    @Transformer
    public Fruit convertFruitNameToFruit(String name) throws Exception
    {
        if (name.equals("orange"))
        {
            return new Orange();
        }
        else if (name.equals("apple"))
        {
            return new Apple();
        }
        else if (name.equals("banana"))
        {
            return new Banana();
        }
        else
        {
            throw new Exception("unknown fruit: " + name);
        }
    }
}
