/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.channels;

/**
 * TODO
 */

public interface SMTP
{
    /**
     * If true, attempt to authenticate the user using the AUTH command. The default is false.
     */
    public static final String SMTPS_AUTH = "mail.smtps.auth=true";

    /**
     * If true, attempt to authenticate the user using the AUTH command. The default is false.
     */
    public static final String SMTP_AUTH = "mail.smtp.auth=true";
}
