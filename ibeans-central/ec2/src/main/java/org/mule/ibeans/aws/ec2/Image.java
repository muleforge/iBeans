/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.aws.ec2;

import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * TODO
 */
public class Image
{
    private boolean imagePublic;
    private String location;
    private String architecture;
    private String imageType;

    public boolean isImagePublic()
    {
        return imagePublic;
    }

    public void setImagePublic(boolean imagePublic)
    {
        this.imagePublic = imagePublic;
    }

    public String getLocation()
    {
        return location;
    }

    public void setLocation(String location)
    {
        this.location = location;
    }

    public String getArchitecture()
    {
        return architecture;
    }

    public void setArchitecture(String architecture)
    {
        this.architecture = architecture;
    }

    public String getImageType()
    {
        return imageType;
    }

    public void setImageType(String imageType)
    {
        this.imageType = imageType;
    }


    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).
                append("architecture", architecture).
                append("imagePublic", imagePublic).
                append("imageType", imageType).
                append("location", location).
                toString();
    }
}
