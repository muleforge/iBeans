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

import org.mule.ibeans.IBeansException;
import org.mule.ibeans.api.client.IntegrationBean;
import org.mule.ibeans.test.ExternalPropsIBeansTestSupport;
import org.mule.util.IOUtils;

import java.io.InputStream;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class IbeansCentralIBeanTestCase extends ExternalPropsIBeansTestSupport
{
    public static final String IBEANS_VERISON = "1.0-beta-8";

    public static final String NON_EXISTENT_VERISON = "1.0-beta-0";

    @IntegrationBean
    private IbeansCentralIBean ibeanscentral;

    @Before
    public void init() throws IBeansException
    {
        registerBeans(new IBeanCentralTransformers());
        ibeanscentral.setCredentials("ibeansconsole", "!ibeans!");
    }

    @Test
    public void search() throws Exception
    {
        IBeanInfo result = ibeanscentral.getIBeanByShortName("flickr");
        assertNotNull(result);
        assertEquals("Flickr iBean", result.getName());
        assertEquals("flickr", result.getShortName());

        assertNull(ibeanscentral.getIBeanByShortName("xyz"));
    }

    @Test
    public void searchWithVersion() throws Exception
    {
        IBeanInfo result = ibeanscentral.getIBeanByShortName("flickr", IBEANS_VERISON);
        assertNotNull(result);
        assertEquals("Flickr iBean", result.getName());
        assertEquals("flickr", result.getShortName());

        assertNull(ibeanscentral.getIBeanByShortName("flickr", NON_EXISTENT_VERISON));
    }

    //Twitter s actually a group of iBeans, make sure we it gets indexed properly
    @Test
    public void indexingWithGroups() throws Exception
    {
        IBeanInfo result = ibeanscentral.getIBeanByShortName("twitter", IBEANS_VERISON);
        assertNotNull(result);
        assertEquals("Twitter iBean", result.getName());
        assertEquals("twitter", result.getShortName());

        assertNull(ibeanscentral.getIBeanByShortName("twitter", NON_EXISTENT_VERISON));
    }

    @Test
    public void getAll() throws Exception
    {
        List<IBeanInfo> results = ibeanscentral.getIBeans();
        assertNotNull(results);
        assertTrue(results.size() > 0);
    }

    @Test
    public void getAllWithVersion() throws Exception
    {
        List<IBeanInfo> results = ibeanscentral.getIBeans(IBEANS_VERISON);
        assertNotNull(results);
        assertTrue(results.size() > 0);
    }


    //IBEANS-90
    //@Test
    // public void getDownloadUrl() throws Exception
    // {
    //     IBeanInfo result = ibeanscentral.getIBeanByShortName("flickr");
    //     assertNotNull(result);
    //     assertEquals("Flickr iBean", result.getName());
    //     assertEquals("flickr", result.getShortName());
    //     URL url = ibeanscentral.getIBeanDownloadUrl(result);
    //     assertNotNull(url);

    //      //The URL returns upper case letters in IBeansCentral. which is odd
    //      assertEquals("http://" + IbeansCentralIBean.HOST + ":" + IbeansCentralIBean.PORT + "/iBeansCentral/api/registry/Mule%20iBeans/flickr-ibean.jar?version=1.0-beta-6", url.toString());
    // }

    @Test
    public void getDownload() throws Exception
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

    @Test
    public void verify() throws Exception
    {
        assertFalse(ibeanscentral.verifyCredentials("foo123", "dffddfeer"));
        assertTrue(ibeanscentral.verifyCredentials("ibeansConsole", "!ibeans!"));
    }
}
