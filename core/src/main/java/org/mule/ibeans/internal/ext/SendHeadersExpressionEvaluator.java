/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.internal.ext;

import org.mule.api.MuleContext;
import org.mule.api.MuleMessage;
import org.mule.api.context.MuleContextAware;
import org.mule.api.expression.ExpressionEvaluator;
import org.mule.api.transport.PropertyScope;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Creats a Map proxy to the current message headers so that the map reference can be used to set outbound headers on the
 * current message.
 */
public class SendHeadersExpressionEvaluator implements ExpressionEvaluator, MuleContextAware
{
    public static final String NAME = "sendHeaders";

    /**
     * logger used by this class
     */
    protected transient final Log logger = LogFactory.getLog(SendHeadersExpressionEvaluator.class);

    protected MuleContext muleContext;

    public void setMuleContext(MuleContext context)
    {
        this.muleContext = context;
    }

    public Object evaluate(String expression, MuleMessage message)
    {
        if (message == null)
        {
            return null;
        }
        return new SendHeadersMap(message);
    }

    /**
     * {@inheritDoc}
     */
    public String getName()
    {
        return NAME;
    }

    /**
     * {@inheritDoc}
     */
    public void setName(String name)
    {
        throw new UnsupportedOperationException("name");
    }


    public class SendHeadersMap implements Map
    {
        private MuleMessage message;

        public SendHeadersMap(MuleMessage message)
        {
            this.message = message;
        }

        public int size()
        {
            return message.getPropertyNames(PropertyScope.OUTBOUND).size();
        }

        public boolean isEmpty()
        {
            return message.getPropertyNames(PropertyScope.OUTBOUND).size() == 0;
        }

        public boolean containsKey(Object key)
        {
            return message.getPropertyNames(PropertyScope.OUTBOUND).contains(key);  //To change body of implemented methods use File | Settings | File Templates.
        }

        public boolean containsValue(Object value)
        {
            throw new UnsupportedOperationException("SendHeadersMap.containsValue");

//            Set names = message.getPropertyNames(PropertyScope.OUTBOUND);
//            for (Object name : names)
//            {
//                if(value.equals(get(name)!=null))
//                {
//                    return true;
//                }
//            }
//
//            return false;
        }

        public Object get(Object key)
        {
            return message.getProperty(key.toString(), PropertyScope.OUTBOUND);
        }

        public Object put(Object key, Object value)
        {
            message.setProperty(key.toString(), value, PropertyScope.OUTBOUND);
            return value;
        }

        public Object remove(Object key)
        {
            return message.removeProperty(key.toString());
        }

        public void putAll(Map t)
        {
            for (Object o : t.keySet())
            {
                put(o, t.get(0));
            }
        }

        public void clear()
        {
            throw new UnsupportedOperationException("SendHeadersMap.clear");
        }

        public Set keySet()
        {
            return message.getPropertyNames(PropertyScope.OUTBOUND);
        }

        public Collection values()
        {
            throw new UnsupportedOperationException("SendHeadersMap.values");
        }

        public Set entrySet()
        {
            throw new UnsupportedOperationException("SendHeadersMap.entrySet");
        }
    }
}