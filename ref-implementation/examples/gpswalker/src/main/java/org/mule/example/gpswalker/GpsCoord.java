/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.example.gpswalker;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * A bean that holds Geo Coordinates
 */
public class GpsCoord
{

    @JsonProperty
    private float latitude;
    @JsonProperty
    private float longitude;

    public GpsCoord(float lat, float lng)
    {
        latitude = lat;
        longitude = lng;
    }

    public float getLatitude()
    {
        return latitude;
    }

    public float getLongitude()
    {
        return longitude;
    }

}
