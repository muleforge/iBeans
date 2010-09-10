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

import org.mule.api.annotations.Schedule;
import org.mule.api.transformer.TransformerException;
import org.mule.module.annotationx.api.Receive;
import org.mule.module.annotationx.api.Send;

import javax.inject.Singleton;

/**
 * Generates a random walk around a city
 */
@Singleton
public class CityStroller
{

    public static final GpsCoord SAN_FRANCISCO = new GpsCoord(37.789167f, -122.419281f);
    public static final GpsCoord LONDON = new GpsCoord(51.515259f, -0.11776f);
    public static final GpsCoord VALLETTA = new GpsCoord(35.897655f, 14.511631f);


    private volatile GpsCoord currentCoord = SAN_FRANCISCO;
    private volatile boolean firstTime = true;

    @Schedule(interval = 3000, startDelay = 2000)
    @Send(uri = "ajax:///ibeans/services/gps")
    public synchronized GpsCoord generateNextCoord() throws TransformerException
    {
        if (firstTime)
        {
            firstTime = false;
        }
        else
        {
            //could use a better algorithm here or real test data for better results            
            double dist = Math.random() * 0.002;
            double angle = Math.random() * Math.PI;
            float lat = currentCoord.getLatitude() + (float) (dist * Math.sin(angle));
            float lng = currentCoord.getLongitude() + (float) (dist * Math.cos(angle));

            currentCoord = new GpsCoord(lat, lng);
        }
        return currentCoord;
    }

    @Receive(uri = "ajax:///ibeans/services/gps-city")
    public synchronized void changeCity(String city)
    {
        if (city.equalsIgnoreCase("London"))
        {
            setCurrentCoord(LONDON);
        }
        else if (city.equalsIgnoreCase("Valletta"))
        {
            setCurrentCoord(VALLETTA);
        }
        else
        {
            setCurrentCoord(SAN_FRANCISCO);
        }
    }
    
    @Receive(uri = "ajax:///ibeans/services/gps-nudgecursor")
    public synchronized void nudgeCursor(String rawDirection)
    {
        GpsCoord localCurrentCoord = getCurrentCoord();
        GpsCoord bumpedCoord = null;
        final float BUMPAMOUNT = 0.008f;

        final String NORTH = "north";
        final String SOUTH = "south";
        final String EAST  = "east";
        final String WEST  = "west";

        String direction = rawDirection.toLowerCase().trim();

        if (direction.equals(NORTH))
        {
            bumpedCoord = new GpsCoord(localCurrentCoord.getLatitude()+BUMPAMOUNT, localCurrentCoord.getLongitude());
        }
        else if (direction.equals(SOUTH))
        {
            bumpedCoord = new GpsCoord(localCurrentCoord.getLatitude()-BUMPAMOUNT, localCurrentCoord.getLongitude());
        }
        else if (direction.equals(EAST))
        {
            bumpedCoord = new GpsCoord(localCurrentCoord.getLatitude(), localCurrentCoord.getLongitude()+BUMPAMOUNT);
        }
        else if (direction.equals(WEST))
        {
            bumpedCoord = new GpsCoord(localCurrentCoord.getLatitude(), localCurrentCoord.getLongitude()-BUMPAMOUNT);
        }
        else {
            bumpedCoord = localCurrentCoord;
        }

        setCurrentCoord(bumpedCoord);
    }


    public GpsCoord getCurrentCoord()
    {
        return currentCoord;
    }

    public void setCurrentCoord(GpsCoord currentCoord)
    {
        this.currentCoord = currentCoord;
    }
}
