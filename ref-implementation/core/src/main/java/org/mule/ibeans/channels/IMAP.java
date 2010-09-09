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
 * Defines properties that cna be used on the IMAP channel
 */
public interface IMAP
{
    /**
     * Whether once a messsge is deleted once it has been read.  The default is false (delete message once read).
     */
    public static String KEEP_READ_MESSAGES = "deleteReadMessages=false";

    /**
     * Where to move a message too once its been read.  The default behaviours is that the message is deleted.
     * Note: The user needs to add the folder name to this property key
     */
    public static final String MOVE_TO_FOLDER_KEY = "moveToFolder=";

}
