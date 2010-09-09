/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package ibeans.client.model;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * TODO
 */
public class AppInfo implements IsSerializable
{
    private String name;
    private String version;
    private String copyright;

    public void setName(String name)
    {
        this.name = name;
    }

    public void setVersion(String version)
    {
        this.version = version;
    }

    public void setCopyright(String copyright)
    {
        this.copyright = copyright;
    }

    public String getName()
    {
        return name;
    }

    public String getVersion()
    {
        return version;
    }

    public String getCopyright()
    {
        return copyright;
    }
}
