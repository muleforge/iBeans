/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.gmail;

import org.mule.ibeans.api.client.Call;
import org.mule.ibeans.api.client.CallException;
import org.mule.ibeans.api.client.State;
import org.mule.ibeans.api.client.params.Attachment;
import org.mule.ibeans.api.client.params.HeaderParam;
import org.mule.ibeans.api.client.params.Optional;
import org.mule.ibeans.api.client.params.Payload;
import org.mule.ibeans.api.client.params.PropertyParam;
import org.mule.ibeans.api.client.params.UriParam;
import org.mule.ibeans.channels.IMAP;
import org.mule.ibeans.channels.SMTP;

import javax.activation.DataSource;
import javax.mail.Message;

/**
 * TODO
 */
public interface GMailIBean
{
    @UriParam("smtp_host")
    public static final String SMTP_HOST = "smtp.gmail.com";

    @UriParam("imap_host")
    public static final String IMAP_HOST = "imap.gmail.com";

    @UriParam("pop3_host")
    public static final String POP3_HOST = "pop.gmail.com";

    @UriParam("smtp_port")
    public static final int SMTP_PORT = 465;

    @UriParam("imap_port")
    public static final int IMAP_PORT = 993;

    @UriParam("pop3_port")
    public static final int POP3_PORT = 995;

    @PropertyParam("moveToFolder")
    public static final String DEFAULT_MOVE_TO_FOLDER = "[Gmail]/Trash";

    @State
    public void init(@UriParam("fromAddress") String emailAddress, @UriParam("password") String password);

    @Call(uri = "smtps://{fromAddress}:{password}@{smtp_host}:{smtp_port}", properties = {SMTP.SMTPS_AUTH})
    public void send(@HeaderParam("toAddresses") String toAddresses, @HeaderParam("subject") String subject, @Payload Object body) throws CallException;

    @Call(uri = "smtps://{fromAddress}:{password}@{smtp_host}:{smtp_port}", properties = {SMTP.SMTPS_AUTH})
    public void send(@HeaderParam("toAddresses") String toAddresses, @HeaderParam("subject") String subject, @Payload Object body, @Attachment DataSource... attachments) throws CallException;

    @Call(uri = "smtps://{fromAddress}:{password}@{smtp_host}:{smtp_port}", properties = {SMTP.SMTPS_AUTH})
    public void send(@HeaderParam("toAddresses") String toAddresses, @Optional @HeaderParam("ccAddresses") String ccAddresses, @Optional @HeaderParam("bccAddresses") String bccAddresses, @Optional @HeaderParam("replyToAddresses") String replyTo, @HeaderParam("subject") String subject, @Payload Object body, @Optional @Attachment DataSource... attachments) throws CallException;

    @Call(uri = "imaps://{fromAddress}:{password}@{imap_host}:{imap_port}?timeout={timeout}", properties = {IMAP.KEEP_READ_MESSAGES, IMAP.MOVE_TO_FOLDER_KEY + "{moveToFolder}"})
    public Message receiveNext(@UriParam("timeout") int timeout) throws CallException;
}