/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.example.bookstore.api;

import org.mule.example.bookstore.Order;

/**
 * Rest interface for the book admin service.
 */

public interface RSOrderService
{
    public Order orderBook(String author, String title, double price, int quantity, String address, String email);
}