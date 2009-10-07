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

import org.mule.api.transformer.TransformerException;
import org.mule.ibeans.IBeansContext;
import org.mule.ibeans.api.application.Transformer;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.inject.Inject;
import javax.xml.namespace.QName;

import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.ExtensibleElement;
import org.apache.abdera.model.Feed;

/**
 * TODO
 */
public class IBeanCentralTransformers
{
    public static final QName METADATA_QNAME = new QName("http://galaxy.mule.org/2.0", "metadata");

    @Inject
    private IBeansContext iBeansContext;

    @Transformer
    public URL stringToUrl(String url) throws MalformedURLException
    {
        return new URL(url);
    }

    @Transformer
    public List<IBeanInfo> feedXmlToIBeanInfoList(InputStream in) throws TransformerException
    {
        Feed feed = iBeansContext.transform(in, Feed.class);
        List<IBeanInfo> results = new ArrayList<IBeanInfo>(feed.getEntries().size());
        for (Entry entry : feed.getEntries())
        {
            IBeanInfo info = entryToIBeanInfo(entry);
            results.add(info);
        }

        return results;
    }

    @Transformer
    public IBeanInfo feedXmlToIBeanInfo(InputStream in) throws TransformerException
    {
        Feed feed = iBeansContext.transform(in, Feed.class);
        if (feed.getEntries().size() == 0)
        {
            return null;
        }

        Entry entry = feed.getEntries().get(0);
        return entryToIBeanInfo(entry);
    }

    @Transformer
    public IBeanInfo entryToIBeanInfo(Entry entry) throws TransformerException
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
        info.setLicenseName(props.getProperty("License-Title"));
        info.setLicenseUrl(props.getProperty("License-Url"));
        return info;
    }

    @Transformer
    public Null stringToUrl(InputStream in) throws MalformedURLException
    {
        return Null.INSTANCE;
    }
}

