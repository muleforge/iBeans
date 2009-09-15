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

import org.mule.ibeans.api.client.Call;
import org.mule.ibeans.api.client.CallException;
import org.mule.ibeans.api.client.params.UriParam;
import static org.mule.ibeans.channels.HTTP.FOLLOW_REDIRECTS;
import static org.mule.ibeans.channels.HTTP.METHOD_GET;


public interface TwitterTimelineIBean extends TwitterBase
{
    @Call(uri = "http://www.twitter.com/statuses/friends_timeline.{format}?count={count}", properties = {METHOD_GET, FOLLOW_REDIRECTS})
    public <T> T getFriendTimeline(@UriParam("count") int count) throws CallException;

    @Call(uri = "http://www.twitter.com/statuses/public_timeline.{format}", properties = {METHOD_GET, FOLLOW_REDIRECTS})
    public <T> T getPublicTimeline() throws CallException;
}