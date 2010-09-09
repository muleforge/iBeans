/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.internal.util;

import java.util.Comparator;

import javax.activation.DataSource;

/**
 * Compares DataSource objects by name only
 */
public class DataSourceComparator implements Comparator<DataSource>
{
    public int compare(DataSource ds1, DataSource ds2)
    {
        if (ds1.getName() == null && ds2.getName() == null)
        {
            return 0;
        }
        else if (ds1.getName() == null && ds2.getName() != null)
        {
            return -1;
        }
        else if (ds1.getName() != null && ds2.getName() == null)
        {
            return 1;
        }

        return ds1.getName().compareTo(ds2.getName());
    }
}
