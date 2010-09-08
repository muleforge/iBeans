/*
 * $Id$
 * -------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.ibeans.api;

/**
 * TODO
 */
public class ParamFactoryHolder implements Comparable
    {
        private ParamFactory paramFactory;
        private String paramName;
        private int order;

        public ParamFactoryHolder(ParamFactory paramFactory, String paramName)
        {
            this(paramFactory, paramName, -1);
        }

        public ParamFactoryHolder(ParamFactory paramFactory, String paramName, int order)
        {
            this.paramFactory = paramFactory;
            this.paramName = paramName;
            this.order = order;
        }

        public ParamFactory getParamFactory()
        {
            return paramFactory;
        }

        public String getParamName()
        {
            return paramName;
        }

        public int getOrder()
        {
            return order;
        }

        public int compareTo(Object o)
        {
            ParamFactoryHolder holder = (ParamFactoryHolder) o;

            if ((getOrder() == holder.getOrder()))
            {
                return 0;
            }
            else if (getOrder() > holder.getOrder())
            {
                return 1;
            }
            else
            {
                return -1;
            }
        }
    }
