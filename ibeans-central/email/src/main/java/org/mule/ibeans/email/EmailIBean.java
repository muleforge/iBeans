/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.email;

import org.mule.ibeans.api.client.Call;
import org.mule.ibeans.api.client.CallException;
import org.mule.ibeans.api.client.State;
import org.mule.ibeans.api.client.Usage;
import org.mule.ibeans.api.client.params.Attachment;
import org.mule.ibeans.api.client.params.HeaderParam;
import org.mule.ibeans.api.client.params.Optional;
import org.mule.ibeans.api.client.params.Payload;
import org.mule.ibeans.api.client.params.PropertyParam;
import org.mule.ibeans.api.client.params.UriParam;

import java.util.Map;

import javax.activation.DataSource;
import javax.mail.Message;

/**
 * TODO
 */
@Usage("This is an experimental IBean. Don't expect it to work in all scenarios!")
public interface EmailIBean
{

    @State
    public void initSmtp(@UriParam("smtp_host") String host, @UriParam("smtp_port") int port, @UriParam("user") String user, @UriParam("password") String password, @Optional @PropertyParam("") Map props);

    @State
    public void initImap(@UriParam("imap_host") String host, @UriParam("imap_port") int port, @UriParam("user") String user, @UriParam("password") String password, @Optional @PropertyParam("") Map props);

    @State
    public void initPop3(@UriParam("pop3_host") String host, @UriParam("pop3_port") int port, @UriParam("user") String user, @UriParam("password") String password, @Optional @PropertyParam("") Map props);

    @Call(uri = "smtp://{user}:{password}@{smtp_host}:{smtp_port}")
    public void send(@HeaderParam("toAddresses") String toAddresses, @HeaderParam("subject") String subject, @Payload Object body) throws CallException;

    @Call(uri = "smtps://{fromAddress}:{password}@{smtp_host}:{smtp_port}")
    public void send(@HeaderParam("toAddresses") String toAddresses, @Optional @HeaderParam("ccAddresses") String ccAddresses, @Optional @HeaderParam("bccAddresses") String bccAddresses, @Optional @HeaderParam("replyToAddresses") String replyTo, @HeaderParam("subject") String subject, @Payload Object body, @Optional @Attachment DataSource[] attachments) throws CallException;

    @Call(uri = "imap://{user}:{password}@{imap_host}:{imap_port}")
    public Message receiveNextImap() throws CallException;

    @Call(uri = "pop3://{user}:{password}@{pop3_host}:{pop3_port}")
    public Message receiveNextPop3() throws CallException;
}