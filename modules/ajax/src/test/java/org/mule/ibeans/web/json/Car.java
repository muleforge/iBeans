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

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonAutoDetect;

/**
 * TODO
 */
@JsonAutoDetect
public class Car
{
    private String make;
    private String model;

    private List<Feature> features = new ArrayList<Feature>();

    public Car()
    {
    }

    public Car(String make, String model)
    {
        this.make = make;
        this.model = model;
    }

    public String getMake()
    {
        return make;
    }

    public void setMake(String make)
    {
        this.make = make;
    }

    public String getModel()
    {
        return model;
    }

    public void setModel(String model)
    {
        this.model = model;
    }

    public List<Feature> getFeatures()
    {
        return features;
    }

    public void setFeatures(List<Feature> features)
    {
        this.features = features;
    }
}