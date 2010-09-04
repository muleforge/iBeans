/*
 * $Id:  $
 * -------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.ibeans.impl.test;


import org.ibeans.api.Response;

/**
 * This callback allows mocked iBean calls to alter the {@link org.ibeans.api.Response} message. This is used to primarily to
 * set message properties to mimic a call to a real iBean
 */

public interface MockMessageCallback<S extends Response>
{
    public void onMessage(S response);
}
