/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.module.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "house")
@XmlAccessorType(XmlAccessType.FIELD)
public class House
{
    private String street;
    private int windows;
    private boolean brick;

    public String getStreet()
    {
        return street;
    }

    public void setStreet(String street)
    {
        this.street = street;
    }

    public int getWindows()
    {
        return windows;
    }

    public void setWindows(int windows)
    {
        this.windows = windows;
    }

    public boolean isBrick()
    {
        return brick;
    }

    public void setBrick(boolean brick)
    {
        this.brick = brick;
    }
}
