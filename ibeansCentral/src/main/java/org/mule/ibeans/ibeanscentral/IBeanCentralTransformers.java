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

import org.mule.api.annotations.Transformer;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.xml.namespace.QName;

import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.ExtensibleElement;
import org.apache.abdera.model.Feed;

/**
 * Transformers used to convert from an ATOM feed to {@link org.mule.ibeans.ibeanscentral.IBeanInfo} objects.
 */
public class IBeanCentralTransformers
{
    public static final QName METADATA_QNAME = new QName("http://galaxy.mule.org/2.0", "metadata");

    @Transformer(sourceTypes = {InputStream.class, String.class})
    public List<IBeanInfo> feedXmlToIBeanInfoList(Feed feed)
    {
        List<IBeanInfo> results = new ArrayList<IBeanInfo>(feed.getEntries().size());
        for (Entry entry : feed.getEntries())
        {
            IBeanInfo info = entryToIBeanInfo(entry);
            results.add(info);
        }

        return results;
    }

    @Transformer(sourceTypes = {InputStream.class, String.class})
    public IBeanInfo feedXmlToIBeanInfo(Feed feed)
    {
        if (feed.getEntries().size() == 0)
        {
            return null;
        }

        Entry entry = feed.getEntries().get(0);
        return entryToIBeanInfo(entry);
    }

    public IBeanInfo entryToIBeanInfo(Entry entry)
    {
        IBeanInfo info = new IBeanInfo();

        ExtensibleElement metadata = entry.getExtension(METADATA_QNAME);

        Properties props = new Properties();
        for (Element p : metadata.getElements())
        {
            if (p.getAttributeValue("value") != null)
            {
                props.setProperty(p.getAttributeValue("name"), p.getAttributeValue("value"));
            }
        }
        String specTitle = props.getProperty("jar.manifest.Specification-Title");
        info.setFileName(specTitle);
        info.setShortName(specTitle.substring(0, specTitle.indexOf("-")));
        info.setName(props.getProperty("jar.manifest.Implementation-Title"));
        info.setVersion(props.getProperty("jar.manifest.Implementation-Version"));
        info.setDescription(props.getProperty("jar.manifest.Product-Description"));
        info.setDownloadUri(entry.getContentSrc().toString());
        info.setAuthorName(props.getProperty("jar.manifest.Implementation-Vendor"));
        info.setAuthorUrl(props.getProperty("jar.manifest.Implementation-Vendor-Url"));
        info.setLicenseName(props.getProperty("jar.manifest.License-Title"));
        info.setLicenseUrl(props.getProperty("jar.manifest.License-Url"));
        info.setUrl(props.getProperty("jar.manifest.Implementation-Url"));
        return info;
    }
}

