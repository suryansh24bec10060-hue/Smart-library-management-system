package com.library.service;

import com.library.model.Book;

import java.util.ArrayList;

/**
 * Service class responsible for managing books in the library.
 * Demonstrates encapsulation, collections (ArrayList), and basic validation.
 */
public class BookService {
    // Internal collection of books
    private final ArrayList<Book> books;

    public BookService() {
        this.books = new ArrayList<>();
    }

    /**
     * Adds a new book to the library.
     * Prevents duplicate book IDs.
     *
     * @param book The book to add.
     * @return true if added successfully, false if duplicate ID or null.
     */
    public boolean addBook(Book book) {
        if (book == null) {
            return false;
        }
        // Check for duplicate ID (case-insensitive)
        if (findBookById(book.getBookId()) != null) {
            return false;
        }
        books.add(book);
        return true;
    }

    /**
     * Returns all registered books.
     * Returns a copy of the list to protect internal state.
     *
     * @return A new ArrayList containing all books.
     */
    public ArrayList<Book> getAllBooks() {
        return new ArrayList<>(books);
    }

    /**
     * Replaces internal book collection with loaded books from persistence.
     *
     * @param loadedBooks Restored books list.
     */
    public void setBooks(ArrayList<Book> loadedBooks) {
        this.books.clear();
        if (loadedBooks != null) {
            this.books.addAll(loadedBooks);
        }
    }

    /**
     * Searches for a book by its unique ID.
     *
     * @param bookId The ID of the book.
     * @return The matching Book object, or null if not found.
     */
    public Book findBookById(String bookId) {
        if (bookId == null || bookId.trim().isEmpty()) {
            return null;
        }
        for (Book b : books) {
            if (b.getBookId().equalsIgnoreCase(bookId.trim())) {
                return b;
            }
        }
        return null;
    }

    /**
     * Searches for books matching the keyword in title, author, or category.
     * The search is case-insensitive.
     *
     * @param keyword The search term.
     * @return A list of matching books.
     */
    public ArrayList<Book> searchBooks(String keyword) {
        ArrayList<Book> matchingBooks = new ArrayList<>();
        if (keyword == null || keyword.trim().isEmpty()) {
            return matchingBooks;
        }

        String searchKeyword = keyword.trim().toLowerCase();
        for (Book b : books) {
            boolean matchesTitle = b.getTitle().toLowerCase().contains(searchKeyword);
            boolean matchesAuthor = b.getAuthor().toLowerCase().contains(searchKeyword);
            boolean matchesCategory = b.getCategory().toLowerCase().contains(searchKeyword);

            if (matchesTitle || matchesAuthor || matchesCategory) {
                matchingBooks.add(b);
            }
        }
        return matchingBooks;
    }

    /**
     * Updates an existing book's details. Does not allow changing book ID.
     *
     * @param bookId The ID of the book to update.
     * @param title The updated title.
     * @param author The updated author.
     * @param category The updated category.
     * @return true if successfully updated, false if book does not exist or invalid input.
     */
    public boolean updateBook(String bookId, String title, String author, String category) {
        Book book = findBookById(bookId);
        if (book == null) {
            return false;
        }
        try {
            book.setTitle(title);
            book.setAuthor(author);
            book.setCategory(category);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Deletes a book from the library.
     * Does not allow deleting a book that is currently issued/unavailable.
     *
     * @param bookId The ID of the book to delete.
     * @return true if deleted successfully, false if book does not exist or is currently issued.
     */
    public boolean deleteBook(String bookId) {
        Book book = findBookById(bookId);
        if (book == null) {
            return false;
        }
        // Business rule: Cannot delete a book that is currently issued
        if (!book.isAvailable()) {
            return false;
        }
        return books.remove(book);
    }

    /**
     * Returns only the books whose availability is true.
     *
     * @return A list of currently available books.
     */
    public ArrayList<Book> getAvailableBooks() {
        ArrayList<Book> availableBooks = new ArrayList<>();
        for (Book b : books) {
            if (b.isAvailable()) {
                availableBooks.add(b);
            }
        }
        return availableBooks;
    }
}
