/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package ibeans.server;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import ibeans.client.ClientIBeansException;
import ibeans.client.model.Plugin;
import org.apache.abdera.Abdera;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.abdera.protocol.client.ClientResponse;
import org.apache.abdera.protocol.client.RequestOptions;
import org.apache.abdera.writer.Writer;
import org.apache.abdera.writer.WriterFactory;
import org.apache.axiom.om.util.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * TODO
 */
public class IBeansStoreClient
{
    public static final String OPTION_BASIC_AUTHORISATION = "Basic";

    /**
     * logger used by this class
     */
    protected transient final Log logger = LogFactory.getLog(IBeansStoreClient.class);

    AbderaClient client = new AbderaClient(new Abdera());

    private String username;
    private String password;
    private String url;
    private String workspace = "/Mule iBeans";

    public IBeansStoreClient(String username, String password, String url)
    {
        this.username = username;
        this.password = password;
        this.url = url;
    }

    public List<Plugin> getAllPlugins() throws ClientIBeansException
    {
        RequestOptions opts = client.getDefaultRequestOptions();

        if (username != null && password != null)
        {
            String authority = username + ":" + password;
            opts.setAuthorization(OPTION_BASIC_AUTHORISATION + " " + Base64.encode(authority.getBytes()));
        }

        ClientResponse res = client.get(url + URLEncoder.encode(workspace), opts);
        if (res.getStatus() == 200)
        {
            if ("application/atom+xml".equals(res.getContentType().getBaseType()))
            {
                Document<Feed> feedDoc = res.getDocument();

                //TODO remove
                WriterFactory writerFactory = Abdera.getInstance().getWriterFactory();
                Writer writer = writerFactory.getWriter("prettyxml");
                try
                {
                    writer.writeTo(feedDoc, System.out);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
                List<Plugin> plugins = new ArrayList<Plugin>(feedDoc.getRoot().getEntries().size());
                for (Entry entry : feedDoc.getRoot().getEntries())
                {
                    Plugin p = new Plugin();
                    p.setName(entry.getTitle());
                    p.setDescription(entry.getAttributeValue("description"));
                    p.setType(entry.getContentType().name());
                    p.setUrl(entry.getAlternateLink().toString());
                    p.setAuthor(entry.getAuthor().getName());
                    p.setDownloads(Integer.valueOf(entry.getAttributeValue("downloads")));
                    p.setRating(Float.valueOf(entry.getAttributeValue("rating")));
                    //TODO comments p.setCommentsCount(Integer.valueOf(entry.));
                    p.setLicenseName(entry.getAttributeValue("license"));
                    p.setLicenseUrl(entry.getAttributeValue("licenseUrl"));
                    p.setWarning(entry.getAttributeValue("warning"));
                    p.setVersion(entry.getAttributeValue("version"));
                    plugins.add(p);
                }
                return plugins;
            }
            else
            {
                throw new ClientIBeansException("Invalid content-type was returned: " + res.getContentType().getBaseType());
            }
        }
        else
        {
            throw new ClientIBeansException("Failed to read config from Registry, Status was: " +
                    res.getStatus() + ", " + res.getStatusText());
        }
    }
}
