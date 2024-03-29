/*
 * $Id: ChannelType.java 15869 2009-10-23 15:55:18Z rossmason $
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.ibeans.annotation.meta;

/**
 * Possible channel types in iBeans
 */
public enum ChannelType
{
    Inbound,
    Outbound,
    Reply,
    Binding
}
