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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * filters key value pairs out of a query string
 */
public class UriParamFilter
{
    public String filterParamsByValue(String uri, String paramValue)
    {
        String s = "(\\?|&)[^&]+=" + paramValue + "&?";
        Pattern pattern = Pattern.compile(s);
        Matcher m = pattern.matcher(uri);
        if (m.find())
        {
            String result = m.replaceAll(m.group(1));
            if (result.endsWith("?") || result.endsWith("&"))
            {
                result = result.substring(0, result.length() - 1);
            }
            return result;
        }
        return uri;
    }
}
