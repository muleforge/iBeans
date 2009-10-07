/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.ibeanscentral;

/**
 * TODO
 */
public class IBeanInfo
{
    private String shortName;

    private String name;

    private String downloadUri;

    private String description;

    private String version;

    private String fileName;

    private String licenseName;
    private String licenseUrl;

    private String authorName;
    private String authorUrl;

    public String getShortName()
    {
        return shortName;
    }

    public void setShortName(String shortName)
    {
        this.shortName = shortName;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getDownloadUri()
    {
        return downloadUri;
    }

    public void setDownloadUri(String downloadUri)
    {
        this.downloadUri = downloadUri;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getVersion()
    {
        return version;
    }

    public void setVersion(String version)
    {
        this.version = version;
    }

    public String getFileName()
    {
        return fileName;
    }

    public String getFullFileName()
    {
        return fileName + "-" + getVersion() + ".jar";
    }

    public void setFileName(String fileName)
    {
        this.fileName = fileName;
    }

    public String getLicenseName()
    {
        return licenseName;
    }

    public void setLicenseName(String licenseName)
    {
        this.licenseName = licenseName;
    }

    public String getLicenseUrl()
    {
        return licenseUrl;
    }

    public void setLicenseUrl(String licenseUrl)
    {
        this.licenseUrl = licenseUrl;
    }

    public String getAuthorName()
    {
        return authorName;
    }

    public void setAuthorName(String authorName)
    {
        this.authorName = authorName;
    }

    public String getAuthorUrl()
    {
        return authorUrl;
    }

    public void setAuthorUrl(String authorUrl)
    {
        this.authorUrl = authorUrl;
    }
}
