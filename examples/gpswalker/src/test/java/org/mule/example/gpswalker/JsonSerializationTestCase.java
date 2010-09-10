/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.example.gpswalker;

import org.mule.ibeans.channels.MimeTypes;
import org.mule.ibeans.test.IBeansRITestSupport;

import org.ibeans.impl.support.datatype.SimpleDataType;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class JsonSerializationTestCase extends IBeansRITestSupport
{
    /**
     * Test that our GPS coord gets serialized correctly
     * @throws Exception on failure
     */
    @Test
    public void gpsCoordSerialization() throws Exception
    {
        GpsCoord gpsCoord = new GpsCoord(100f, 100f);
        String result = iBeansContext.transform(gpsCoord, new SimpleDataType<String>(String.class, MimeTypes.JSON));
        assertEquals("{\"latitude\":100.0,\"longitude\":100.0}", result);
    }
}
