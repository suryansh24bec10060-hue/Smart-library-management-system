package com.library.model;

import java.io.Serializable;

/**
 * Represents a book in the library.
 */
public class Book implements Serializable {
    private static final long serialVersionUID = 1L;

    private String bookId;
    private String title;
    private String author;
    private String category;
    private boolean available;

    // Overloaded constructor defaulting available to true
    public Book(String bookId, String title, String author, String category) {
        this(bookId, title, author, category, true);
    }

    // Full constructor
    public Book(String bookId, String title, String author, String category, boolean available) {
        setBookId(bookId);
        setTitle(title);
        setAuthor(author);
        setCategory(category);
        this.available = available;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        if (bookId == null || bookId.trim().isEmpty()) {
            throw new IllegalArgumentException("Book ID cannot be null or empty.");
        }
        this.bookId = bookId.trim();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be null or empty.");
        }
        this.title = title.trim();
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        if (author == null || author.trim().isEmpty()) {
            throw new IllegalArgumentException("Author cannot be null or empty.");
        }
        this.author = author.trim();
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            throw new IllegalArgumentException("Category cannot be null or empty.");
        }
        this.category = category.trim();
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    @Override
    public String toString() {
        return "Book [ID=" + bookId + ", Title='" + title + "', Author='" + author + 
               "', Category='" + category + "', Available=" + available + "]";
    }
}
