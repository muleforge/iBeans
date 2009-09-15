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

import java.io.Serializable;

public class Plugin implements Serializable
{
    public static final String TYPE_MODULE = "Module";
    public static final String TYPE_IBEAN = "iBean";
    public static final String TYPE_WEBAPP = "WebApp";


    private String id;
    private String name;
    private String version;
    private String description;
    private String furtherInfo;
    private String url;
    private int downloads;
    private float rating;
    private boolean installed;
    private boolean enabled;
    private String type;
    private int commentsCount;
    private String author;
    private String filename;
    private String licenseName;
    private String licenseUrl;
    private String warning;
    private boolean required;
    private boolean bundled;


    public Plugin()
    {
    }

    public Plugin(String id, String name, String description, String version, String type)
    {
        this.id = id;
        this.name = name;
        this.description = description;
        this.version = version;
        this.type = type;
    }

    public String getName()
    {
        return (name == null ? id : name);
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getVersion()
    {
        return version;
    }

    public void setVersion(String version)
    {
        this.version = version;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public int getDownloads()
    {
        return downloads;
    }

    public void setDownloads(int downloads)
    {
        this.downloads = downloads;
    }

    public float getRating()
    {
        return rating;
    }

    public void setRating(float rating)
    {
        this.rating = rating;
    }

    public boolean isInstalled()
    {
        return installed;
    }

    public void setInstalled(boolean installed)
    {
        this.installed = installed;
    }

    public boolean isEnabled()
    {
        return enabled;
    }

    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public int getCommentsCount()
    {
        return commentsCount;
    }

    public void setCommentsCount(int commentsCount)
    {
        this.commentsCount = commentsCount;
    }

    public String getAuthor()
    {
        return author;
    }

    public void setAuthor(String author)
    {
        this.author = author;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getFilename()
    {
        return filename;
    }

    public void setFilename(String filename)
    {
        this.filename = filename;
    }

    public String getWarning()
    {
        return warning;
    }

    public void setWarning(String warning)
    {
        this.warning = warning;
    }

    public boolean isRequired()
    {
        return required;
    }

    public void setRequired(boolean required)
    {
        this.required = required;
    }

    public boolean isBundled()
    {
        return bundled;
    }

    public void setBundled(boolean bundled)
    {
        this.bundled = bundled;
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

    public String getFurtherInfo()
    {
        return furtherInfo;
    }

    public void setFurtherInfo(String furtherInfo)
    {
        this.furtherInfo = furtherInfo;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }

        Plugin plugin = (Plugin) o;

        if (filename != null ? !filename.equals(plugin.filename) : plugin.filename != null)
        {
            return false;
        }
        if (id != null ? !id.equals(plugin.id) : plugin.id != null)
        {
            return false;
        }
        if (name != null ? !name.equals(plugin.name) : plugin.name != null)
        {
            return false;
        }
        if (type != null ? !type.equals(plugin.type) : plugin.type != null)
        {
            return false;
        }
        if (version != null ? !version.equals(plugin.version) : plugin.version != null)
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (version != null ? version.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (filename != null ? filename.hashCode() : 0);
        return result;
    }
}