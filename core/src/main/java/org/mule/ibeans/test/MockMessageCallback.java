/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.test;

import org.mule.api.MuleMessage;

/**
 * This callback allows mocked iBean calls to alter the result MuleMessage. This is used to primarily to
 * set message properties to mimic a call to a real iBean service
 */

public interface MockMessageCallback
{
    public void onMessage(MuleMessage message);
}
