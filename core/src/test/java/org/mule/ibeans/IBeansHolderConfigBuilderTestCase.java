/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans;

import org.mule.api.config.ConfigurationBuilder;
import org.mule.ibeans.config.IBeanHolder;
import org.mule.ibeans.config.IBeanHolderConfigurationBuilder;
import org.mule.ibeans.test.IBeansTestSupport;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class IBeansHolderConfigBuilderTestCase extends IBeansTestSupport
{
    @Override
    protected void addBuilders(List<ConfigurationBuilder> builders)
    {
        builders.add(new IBeanHolderConfigurationBuilder());
    }

    @Test
    public void configBuilder() throws Exception
    {
        Collection<IBeanHolder> col = iBeansContext.getConfig().getObjectsByType(IBeanHolder.class);
        //Ensure IBeanHolder is comparable
        Set<IBeanHolder> beans = new TreeSet<IBeanHolder>(col);

        assertEquals(7, beans.size());
        String[] ids = new String[7];
        int i = 0;
        for (Iterator<IBeanHolder> iterator = beans.iterator(); iterator.hasNext(); i++)
        {
            IBeanHolder iBeanHolder = iterator.next();
            ids[i] = iBeanHolder.getId();
        }
        assertEquals("hostip", ids[0]);
        assertEquals("search", ids[1]);
        assertEquals("test", ids[2]);
        assertEquals("testexception", ids[3]);
        assertEquals("testimplicitpropertiesinfactory", ids[4]);
        assertEquals("testparamsfactory", ids[5]);
        assertEquals("testuri", ids[6]);
    }
}
