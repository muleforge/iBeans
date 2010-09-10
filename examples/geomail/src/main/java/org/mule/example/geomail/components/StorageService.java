/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.example.geomail.components;

import org.mule.example.geomail.dao.Sender;
import org.mule.example.geomail.dao.SenderDao;
import org.mule.module.annotationx.api.Receive;
import org.mule.module.annotationx.api.Send;

import com.google.inject.Inject;

/**
 * TODO
 */
public class StorageService
{
    @Inject
    private SenderDao senderDao;

    @Receive(uri = "vm://storage")
    @Send(uri = "ajax:///ibeans/geomail")
    public Sender store(Sender sender)
    {
        //Lets store this for next time
        senderDao.addSender(sender);
        return sender;
    }
}
