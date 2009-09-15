/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.example.geomail.components;

import org.mule.example.geomail.dao.Sender;
import org.mule.ibeans.api.application.Receive;
import org.mule.ibeans.api.application.Send;

/**
 * TODO
 */
public class Validator
{
    @Receive(uri = "vm://channels/positions/validate")
    @Send(uri = "ajax://ibeans/GEOMAIL")
    public Sender validate(Sender sender)
    {
        if (sender.getLatitude() == null && sender.getLongitude() == null)
        {
            return null;
        }
        return sender;
    }
}
