/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package ibeans.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * TODO
 */
public abstract class AbstractAsyncCallback<T> implements AsyncCallback<T>
{
    private IBeansConsole2 console;

    protected AbstractAsyncCallback(IBeansConsole2 console)
    {
        this.console = console;
    }

    public final void onFailure(Throwable throwable)
    {
        console.errorStatus(throwable);
    }
}
