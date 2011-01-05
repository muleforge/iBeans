/*
 * $Id$
 * -------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.ibeans.impl.support;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.NamespaceContext;

/**
 * TODO
 */
public class NamespaceMap extends HashMap implements NamespaceContext
{
	private static final long serialVersionUID = 148484772727L;

	public NamespaceMap() {
    }

    public NamespaceMap(Map map) {
        super(map);
    }

    public void add(String prefix, String namespaceURI) {
        put(prefix, namespaceURI);
    }

    public String[] getDeclaredPrefixes() {
        Set keys = keySet();
        return (String[]) keys.toArray(new String[keys.size()]);
    }

    public String getNamespaceURI(String prefix) {
        return get(prefix).toString();
    }

    public String getPrefix(String namespaceURI) {
        Iterator iterator = entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            if (entry.getValue().toString().equals(namespaceURI)) {
                return (String) entry.getKey();
            }
        }
        return null;
    }

    public Iterator getPrefixes(String namespaceURI) {
        ArrayList list = new ArrayList();
        Iterator iterator = entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            if (entry.getValue().toString().equals(namespaceURI)) {
                list.add(entry.getKey());
            }
        }
        return list.iterator();
    }
}
