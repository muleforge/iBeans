/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.ibeans.example.bookstore.resources;

import com.sun.jersey.spi.resource.Singleton;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 * Service for placing a book order.
 *
 * @see org.mule.example.bookstore.OrderService
 */
@Path("/")
@Singleton
public class BookStore
{
    private Catalog bookCatalog;

    private String name;

    public BookStore()
    {
        setName("The Animal Bookshop");
        bookCatalog = new Catalog();
    }

    public Catalog getBookCatalog()
    {
        return bookCatalog;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    @POST
    @Produces("application/json")
    @Path("order")
    public Order orderBook(@FormParam("id") long id, @FormParam("quantity") int quantity,
                           @FormParam("address") String address, @FormParam("email") String email)
    {
        Book book = bookCatalog.getBook(id);
        System.out.println("Order has been placed for book: " + book.getTitle());
        return new Order(book, quantity, address, email);
    }

    @Override
    public String toString()
    {
        return "BookStore{" +
                "name='" + name + '\'' +
                '}';
    }

}
