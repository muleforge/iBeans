/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.internal.util;

import org.mule.util.StringUtils;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * filters key value pairs out of a query string
 */
//TODO: this should really be done with RegEx but I was having a lot of difficulty. See the history for this file for a solution
// that almost works, maybe someone else can figure out the last piece
public class UriParamFilter
{
    public String filterParamsByValue(String uri, String paramValue)
    {
        int i = uri.indexOf("?");
        if (i == -1)
        {
            return uri;
        }
        String query = uri.substring(i + 1);
        String base = uri.substring(0, i + 1);
        StringBuffer newQuery = new StringBuffer();

        TreeMap p = getPropertiesFromQueryString(query);
        for (Iterator<Map.Entry<Object, Object>> iterator = p.entrySet().iterator(); iterator.hasNext();)
        {
            Map.Entry<Object, Object> entry = iterator.next();
            if (!paramValue.equals(entry.getValue()))
            {
                newQuery.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
        }
        String result = base + newQuery.toString();
        if (result.endsWith("?") || result.endsWith("&"))
        {
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }

    private TreeMap getPropertiesFromQueryString(String query)
    {
        TreeMap props = new TreeMap();

        if (query == null)
        {
            return props;
        }

        query = new StringBuffer(query.length() + 1).append('&').append(query).toString();

        int x = 0;
        while ((x = addProperty(query, x, '&', props)) != -1)
        {
            // run
        }

        return props;
    }

    private int addProperty(String query, int start, char separator, TreeMap properties)
    {
        int i = query.indexOf(separator, start);
        int i2 = query.indexOf(separator, i + 1);
        String pair;
        if (i > -1 && i2 > -1)
        {
            pair = query.substring(i + 1, i2);
        }
        else if (i > -1)
        {
            pair = query.substring(i + 1);
        }
        else
        {
            return -1;
        }
        int eq = pair.indexOf('=');

        if (eq <= 0)
        {
            String key = pair;
            String value = StringUtils.EMPTY;
            properties.put(key, value);
        }
        else
        {
            String key = pair.substring(0, eq);
            String value = (eq == pair.length() ? StringUtils.EMPTY : pair.substring(eq + 1));
            properties.put(key, value);
        }
        return i2;
    }

}
