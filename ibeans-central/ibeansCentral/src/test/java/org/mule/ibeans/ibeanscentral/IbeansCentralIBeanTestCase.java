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

import org.mule.ibeans.api.client.IntegrationBean;
import org.mule.ibeans.api.client.CallException;
import org.mule.ibeans.test.AbstractIBeansTestCase;
import org.mule.util.IOUtils;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

public class IbeansCentralIBeanTestCase extends AbstractIBeansTestCase
{
    @IntegrationBean
    private IbeansCentralIBean ibeanscentral;

    @Override
    public void doSetUp() throws Exception
    {
        registerBeans(new IBeanCentralTransformers());
        ibeanscentral.setCredentials("admin", "admin");
    }

    public void testSearch() throws Exception
    {
        IBeanInfo result = ibeanscentral.getIBeanByShortName("flickr");
        assertNotNull(result);
        assertEquals("Flickr iBean", result.getName());
        assertEquals("flickr", result.getShortName());

        assertNull(ibeanscentral.getIBeanByShortName("xyz"));
    }

    public void testSearchWithVersion() throws Exception
    {
        IBeanInfo result = ibeanscentral.getIBeanByShortName("flickr", "1.0-beta-5");
        assertNotNull(result);
        assertEquals("Flickr iBean", result.getName());
        assertEquals("flickr", result.getShortName());

        assertNull(ibeanscentral.getIBeanByShortName("flickr", "1.0-beta-22"));
    }

    public void testGetAll() throws Exception
    {
        List<IBeanInfo> results = ibeanscentral.getIBeans();
        assertNotNull(results);
        assertEquals(1, results.size());
        IBeanInfo result = results.get(0);
        assertEquals("Flickr iBean", result.getName());
        assertEquals("flickr", result.getShortName());
    }


    public void testGetDownloadUrl() throws Exception
    {
        IBeanInfo result = ibeanscentral.getIBeanByShortName("flickr");
        assertNotNull(result);
        assertEquals("Flickr iBean", result.getName());
        assertEquals("flickr", result.getShortName());
        URL url = ibeanscentral.getIBeanDownloadUrl(result);
        assertNotNull(url);
        assertEquals("http://" + IbeansCentralIBean.HOST + ":" + IbeansCentralIBean.PORT + "/api/registry/Mule%20iBeans/flickr-ibean.jar?version=1.0-beta-5", url.toString());
    }

    public void testGetDownload() throws Exception
    {
        IBeanInfo result = ibeanscentral.getIBeanByShortName("flickr");
        assertNotNull(result);
        assertEquals("Flickr iBean", result.getName());
        assertEquals("flickr", result.getShortName());
        InputStream download = ibeanscentral.downloadIBean(result.getDownloadUri());
        assertNotNull(download);
        byte[] bytes = IOUtils.toByteArray(download);
        assertTrue(bytes.length > 1000);
    }
}
