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
import org.mule.ibeans.api.client.params.Optional;
import org.mule.ibeans.api.client.params.PayloadParam;
import org.mule.ibeans.api.client.params.UriParam;


public interface TwitterStatusIBean extends TwitterBase
{
    @Call(uri = "http://www.twitter.com/statuses/update.{format}")
    public <T> T statusesUpdate(@PayloadParam("status") String status) throws CallException;

    @Call(uri = "http://www.twitter.com/statuses/update.{format}")
    public <T> T statusesUpdate(@PayloadParam("status") String status, @Optional @PayloadParam("in_reply_to_status_id") String replyId) throws CallException;

    @Call(uri = "http://www.twitter.com/statuses/show/{id}.{format}")
    public <T> T statusesShow(@UriParam("id") String id) throws CallException;

    @Call(uri = "http://www.twitter.com/statuses/show/{id}.{format}")
    public <T> T statusesShow(@UriParam("id") String id, @UriParam("format") String format) throws Exception;
}