/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.ibeans.twitter;

import org.mule.ibeans.api.client.IBeanGroup;

/**
 * An iBean interface that contains all Twitter methods
 */
@IBeanGroup
public interface TwitterIBean extends TwitterStatusIBean, TwitterTimelineIBean, TwitterBase
{

}
