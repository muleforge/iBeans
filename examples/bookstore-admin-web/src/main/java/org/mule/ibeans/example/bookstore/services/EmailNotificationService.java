/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.example.bookstore.services;

import org.mule.api.MuleMessage;
import org.mule.config.annotations.Service;
import org.mule.config.annotations.endpoints.In;
import org.mule.config.annotations.endpoints.Out;
import org.mule.config.annotations.expressions.Mule;
import org.mule.example.bookstore.Book;
import org.mule.example.bookstore.Order;
import org.mule.impl.endpoint.MEP;
import org.mule.transport.email.MailProperties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * TODO
 */
@Service(name = "Email Notification Service")
@In(uri = "vm://emailNotification", mep = MEP.InOnly)
@Out(uri = "smtp://ross@pass:smtp.gmail.com?from=bookstore@mulesoft.com")
public class EmailNotificationService
{
    /**
     * logger used by this class
     */
    protected transient final Log logger = LogFactory.getLog(EmailNotificationService.class);

    public String createNotification(Order order, @Mule("mule:message") MuleMessage message)
    {
        Book book = order.getBook();

        String body = "Thank you for placing your order for " +
                book.getTitle() + " with the Mule-powered On-line Bookstore. " +
                "Your order will be shipped  to " +
                order.getAddress() + " by the next business day.";

        String email = order.getEmail();
        message.setProperty(MailProperties.TO_ADDRESSES_PROPERTY, email);
        message.setProperty(MailProperties.SUBJECT_PROPERTY, "Your order has been placed!");
        logger.info("Sending e-mail notification to " + email);
        return body;
    }
}
