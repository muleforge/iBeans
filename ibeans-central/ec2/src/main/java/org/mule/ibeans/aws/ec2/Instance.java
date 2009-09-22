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
public class Instance
{
    public enum ImageType {

    }
    private String instanceId;
    private String imageId;
    private int instanceStateCode;
    private String instanceStateName;
    private String privateDnsName;
    private String publicDnsName;
    private String keyName;
    private int amiLaunchIndex;
    private String imageType;
    // TODO parse ISO8601 date
    private String launchTime;
    private String kernelId;
    private String ramDiskId;
    private String availabilityZone;

    public String getInstanceId()
    {
        return instanceId;
    }

    public void setInstanceId(String instanceId)
    {
        this.instanceId = instanceId;
    }

    public String getImageId()
    {
        return imageId;
    }

    public void setImageId(String imageId)
    {
        this.imageId = imageId;
    }

    public int getInstanceStateCode()
    {
        return instanceStateCode;
    }

    public void setInstanceStateCode(int instanceStateCode)
    {
        this.instanceStateCode = instanceStateCode;
    }

    public String getInstanceStateName()
    {
        return instanceStateName;
    }

    public void setInstanceStateName(String instanceStateName)
    {
        this.instanceStateName = instanceStateName;
    }

    public String getPrivateDnsName()
    {
        return privateDnsName;
    }

    public void setPrivateDnsName(String privateDnsName)
    {
        this.privateDnsName = privateDnsName;
    }

    public String getPublicDnsName()
    {
        return publicDnsName;
    }

    public void setPublicDnsName(String publicDnsName)
    {
        this.publicDnsName = publicDnsName;
    }

    public String getKeyName()
    {
        return keyName;
    }

    public void setKeyName(String keyName)
    {
        this.keyName = keyName;
    }

    public int getAmiLaunchIndex()
    {
        return amiLaunchIndex;
    }

    public void setAmiLaunchIndex(int amiLaunchIndex)
    {
        this.amiLaunchIndex = amiLaunchIndex;
    }

    public String getImageType()
    {
        return imageType;
    }

    public void setImageType(String imageType)
    {
        this.imageType = imageType;
    }

    public String getLaunchTime()
    {
        return launchTime;
    }

    public void setLaunchTime(String launchTime)
    {
        this.launchTime = launchTime;
    }

    public String getKernelId()
    {
        return kernelId;
    }

    public void setKernelId(String kernelId)
    {
        this.kernelId = kernelId;
    }

    public String getRamDiskId()
    {
        return ramDiskId;
    }

    public void setRamDiskId(String ramDiskId)
    {
        this.ramDiskId = ramDiskId;
    }

    public String getAvailabilityZone()
    {
        return availabilityZone;
    }

    public void setAvailabilityZone(String availabilityZone)
    {
        this.availabilityZone = availabilityZone;
    }


    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).
                append("amiLaunchIndex", amiLaunchIndex).
                append("availabilityZone", availabilityZone).
                append("imageId", imageId).
                append("imageType", imageType).
                append("instanceId", instanceId).
                append("instanceStateCode", instanceStateCode).
                append("instanceStateName", instanceStateName).
                append("kernelId", kernelId).
                append("keyName", keyName).
                append("launchTime", launchTime).
                append("privateDnsName", privateDnsName).
                append("publicDnsName", publicDnsName).
                append("ramDiskId", ramDiskId).
                toString();
    }
}
