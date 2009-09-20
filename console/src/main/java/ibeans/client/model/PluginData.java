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

import com.extjs.gxt.ui.client.data.ModelData;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * TODO
 */
public class PluginData implements ModelData, Serializable
{

    /**
     * Serial version
     */
    private static final long serialVersionUID = 1541720813351984842L;

    // Map interface isn't serializable, but HashMap implementation is
    private HashMap<String, Object> data;

    public PluginData(HashMap<String, Object> data)
    {
        this.data = data;
    }

    public PluginData(Plugin plugin)
    {
        data = new HashMap<String, Object>();
        if (plugin == null)
        {
            return;
        }
        add("description", plugin.getDescription());
        add("downloads", plugin.getDownloads());
        add("name", plugin.getName());
        add("rating", plugin.getRating());
        add("type", plugin.getType());
        add("url", plugin.getUrl());
        add("version", plugin.getVersion());
        add("enabled", plugin.isEnabled());
        add("installed", plugin.isInstalled());
        add("commentsCount", plugin.getCommentsCount());
        add("author", plugin.getAuthor());
        add("id", plugin.getId());
        add("filename", plugin.getFilename());
        add("bundled", plugin.isBundled());
        add("required", plugin.isRequired());
        add("licenseName", plugin.getLicenseName());
        add("licenseUrl", plugin.getLicenseUrl());
        add("furtherInfo", plugin.getFurtherInfo());
        add("downloadUrl", plugin.getDownloadUrl());
    }

    public Plugin toPlugin()
    {
        Plugin p = new Plugin();
        p.setAuthor((String) get("author"));
        p.setDescription((String) get("description"));
        p.setName((String) get("name"));
        p.setRating((Float) get("rating"));
        p.setDownloads((Integer) get("downloads"));
        p.setCommentsCount((Integer) get("commentsCount"));
        p.setType((String) get("type"));
        p.setVersion((String) get("version"));
        p.setUrl((String) get("url"));
        p.setId((String) get("id"));
        p.setFilename((String) get("filename"));
        p.setInstalled((Boolean) get("installed"));
        p.setEnabled((Boolean) get("enabled"));
        p.setBundled((Boolean) get("bundled"));
        p.setRequired((Boolean) get("required"));
        p.setLicenseName((String) get("licenseName"));
        p.setLicenseUrl((String) get("licenseUrl"));
        p.setFurtherInfo((String) get("furtherInfo"));
        p.setDownloadUrl((String) get("downloadUrl"));
        return p;
    }

    private void add(String name, Object value)
    {
        if (value != null)
        {
            data.put(name, value);
        }
    }

    public <X> X get(String s)
    {
        return (X) data.get(s);
    }

    public Map<String, Object> getProperties()
    {
        return data;
    }

    public Collection<String> getPropertyNames()
    {
        return data.keySet();
    }

    public <X> X remove(String s)
    {
        return (X) data.remove(s);
    }

    public <X> X set(String s, X x)
    {
        return (X) data.put(s, x);
    }


}