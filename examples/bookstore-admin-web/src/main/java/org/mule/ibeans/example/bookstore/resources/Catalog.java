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

import com.sun.jersey.api.NotFoundException;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

/**
 * Bookstore catalog service which implements both the public interface for
 * browsing the catalog and the admin interface for adding books to the catalog.
 *
 * @see org.mule.example.bookstore.CatalogService
 * @see org.mule.example.bookstore.CatalogAdminService
 */
@Path("catalog")
public class Catalog
{
    /**
     * Simple hashmap used to store the catalog, in real life this would be a database
     */
    private Map<Long, Book> books = new HashMap<Long, Book>();

    public Catalog()
    {
        books = new HashMap<Long, Book>();

        // Add some initial test data
        addBook("J.R.R. Tolkien", "The Fellowship of the Ring", 8);
        addBook("J.R.R. Tolkien", "The Two Towers", 10);
        addBook("J.R.R. Tolkien", "The Return of the King", 10);
        addBook("C.S. Lewis", "The Lion, the Witch and the Wardrobe", 6);
        addBook("C.S. Lewis", "Prince Caspian", 8);
        addBook("C.S. Lewis", "The Voyage of the Dawn Treader", 6);
        addBook("Leo Tolstoy", "War and Peace", 8);
        addBook("Leo Tolstoy", "Anna Karenina", 6);
        addBook("Henry David Thoreau", "Walden", 8);
        addBook("Harriet Beecher Stowe", "Uncle Tom's Cabin", 6);
        addBook("George Orwell", "1984", 8);
        addBook("George Orwell", "Animal Farm", 8);
        addBook("Aldous Huxley", "Brave New World", 8);

    }

    //public long addBook(Book book)
    @POST
    @Consumes("application/x-www-form-urlencoded")
    @Path("/add")
    public long addBook(@FormParam("author") String author, @FormParam("title") String title, @FormParam("price") double price)
    {
        Book book = new Book(author, title, price);
        System.out.println("Adding book " + book.getTitle());
        long id = books.size() + 1;
        book.setId(id);
        books.put(id, book);
        return id;
    }

    @GET
    @Path("/all")
    @Produces("application/json")
    public Book[] getBooks()
    {
        Book[] bookArray = new Book[books.size()];
        return books.values().toArray(bookArray);
    }

    @GET
    @Path("items/{item}/")
    public Book getBook(@PathParam("item") long bookId)
    {
        Book i = books.get(bookId);
        if (i == null)
        {
            throw new NotFoundException("Book, " + bookId + ", is not found");
        }

        return i;
    }

    @POST
    @Path("/search")
    @Produces("application/json")
    public Book findBook(@FormParam("author") String author, @FormParam("title") String title)
    {
        for (Map.Entry<Long, Book> bookEntry : books.entrySet())
        {
            if ((author != null && bookEntry.getValue().getAuthor().contains(author)) &&
                    (title != null && bookEntry.getValue().getTitle().contains(title)))
            {
                return bookEntry.getValue();
            }
        }
        return null;
    }
}
