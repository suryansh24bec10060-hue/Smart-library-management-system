package com.library.service;

import com.library.model.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for BookService operations and business rules.
 */
public class BookServiceTest {
    private BookService bookService;

    @BeforeEach
    public void setUp() {
        bookService = new BookService();
        bookService.addBook(new Book("B101", "Clean Code", "Robert C. Martin", "Software Engineering", true));
        bookService.addBook(new Book("B102", "Effective Java", "Joshua Bloch", "Programming", true));
        bookService.addBook(new Book("B103", "Introduction to Algorithms", "Thomas H. Cormen", "Computer Science", false));
    }

    @Test
    public void testAddBookSuccess() {
        Book newBook = new Book("B104", "Design Patterns", "Erich Gamma", "Software Engineering", true);
        boolean result = bookService.addBook(newBook);
        assertTrue(result, "Book should be added successfully");
        assertNotNull(bookService.findBookById("B104"), "Added book should be retrievable");
        assertEquals(4, bookService.getAllBooks().size(), "Total books should increase to 4");
    }

    @Test
    public void testAddBookDuplicateIdRejected() {
        Book duplicateBook = new Book("B101", "Another Title", "Another Author", "General", true);
        boolean result = bookService.addBook(duplicateBook);
        assertFalse(result, "Duplicate book ID should be rejected");
        assertEquals(3, bookService.getAllBooks().size(), "Total books should remain unchanged");
    }

    @Test
    public void testFindBookById() {
        Book found = bookService.findBookById("B102");
        assertNotNull(found, "Book should be found by exact ID");
        assertEquals("Effective Java", found.getTitle());
    }

    @Test
    public void testFindBookByIdCaseInsensitive() {
        Book foundLower = bookService.findBookById("b101");
        Book foundUpper = bookService.findBookById("B101");
        assertNotNull(foundLower, "Book lookup should be case-insensitive");
        assertEquals(foundUpper, foundLower, "Case-insensitive lookup should return the exact same instance");
    }

    @Test
    public void testSearchByTitle() {
        ArrayList<Book> results = bookService.searchBooks("Clean");
        assertEquals(1, results.size(), "Should find exactly 1 match for 'Clean'");
        assertEquals("B101", results.get(0).getBookId());
    }

    @Test
    public void testSearchByAuthor() {
        ArrayList<Book> results = bookService.searchBooks("Joshua Bloch");
        assertEquals(1, results.size(), "Should find exactly 1 match for author");
        assertEquals("Effective Java", results.get(0).getTitle());
    }

    @Test
    public void testSearchByCategory() {
        ArrayList<Book> results = bookService.searchBooks("Computer Science");
        assertEquals(1, results.size(), "Should find match by category");
        assertEquals("B103", results.get(0).getBookId());
    }

    @Test
    public void testUpdateBook() {
        boolean updated = bookService.updateBook("B101", "Clean Code 2nd Ed", "Robert Martin", "Craftsmanship");
        assertTrue(updated, "Book details should be updated successfully");

        Book book = bookService.findBookById("B101");
        assertNotNull(book);
        assertEquals("Clean Code 2nd Ed", book.getTitle());
        assertEquals("Robert Martin", book.getAuthor());
        assertEquals("Craftsmanship", book.getCategory());
        assertEquals("B101", book.getBookId(), "Book ID must not be changed during update");
    }

    @Test
    public void testDeleteAvailableBook() {
        boolean deleted = bookService.deleteBook("B101");
        assertTrue(deleted, "Available book should be deleted successfully");
        assertNull(bookService.findBookById("B101"), "Deleted book should no longer exist in collection");
        assertEquals(2, bookService.getAllBooks().size());
    }

    @Test
    public void testDeleteIssuedBookRejected() {
        boolean deleted = bookService.deleteBook("B103");
        assertFalse(deleted, "Issued/unavailable book must not be deleted");
        assertNotNull(bookService.findBookById("B103"), "Issued book should still be in collection");
        assertEquals(3, bookService.getAllBooks().size());
    }

    @Test
    public void testGetAvailableBooks() {
        ArrayList<Book> available = bookService.getAvailableBooks();
        assertEquals(2, available.size(), "Should only contain 2 available books");
        for (Book book : available) {
            assertTrue(book.isAvailable(), "Each book in available list must have available == true");
        }
    }
}
