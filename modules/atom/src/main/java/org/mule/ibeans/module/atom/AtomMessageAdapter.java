/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.module.atom;

import org.mule.api.transformer.TransformerException;
import org.mule.transport.abdera.ObjectToFeed;
import org.mule.transport.http.HttpMessageAdapter;

/**
 * TODO
 */
public class AtomMessageAdapter extends HttpMessageAdapter
{
    public AtomMessageAdapter(Object o) throws TransformerException
    {
        super(o);
        message = new ObjectToFeed().transform(getPayload());
    }
}
