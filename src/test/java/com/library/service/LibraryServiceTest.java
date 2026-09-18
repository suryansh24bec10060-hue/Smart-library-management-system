package com.library.service;

import com.library.model.Book;
import com.library.model.BorrowRecord;
import com.library.model.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for LibraryService borrow and return workflows and business rules.
 */
public class LibraryServiceTest {
    private BookService bookService;
    private StudentService studentService;
    private LibraryService libraryService;

    @BeforeEach
    public void setUp() {
        bookService = new BookService();
        studentService = new StudentService();
        libraryService = new LibraryService(bookService, studentService);

        // Setup sample books and students
        bookService.addBook(new Book("B101", "Clean Code", "Robert C. Martin", "Software Engineering", true));
        bookService.addBook(new Book("B102", "Effective Java", "Joshua Bloch", "Programming", true));
        bookService.addBook(new Book("B103", "Design Patterns", "Erich Gamma", "Software Engineering", true));

        studentService.addStudent(new Student("S101", "Alice Johnson", "alice@university.edu", "9876543210"));
        studentService.addStudent(new Student("S102", "Bob Smith", "bob@university.edu", "8765432109"));
    }

    @Test
    public void testBorrowBookSuccess() {
        boolean success = libraryService.borrowBook("S101", "B101");
        assertTrue(success, "Borrowing an available book by an existing student should succeed");
        assertEquals(1, libraryService.getActiveBorrowings().size());
    }

    @Test
    public void testBorrowingMakesBookUnavailable() {
        Book book = bookService.findBookById("B101");
        assertTrue(book.isAvailable(), "Book should initially be available");

        libraryService.borrowBook("S101", "B101");
        assertFalse(book.isAvailable(), "Book should become unavailable after borrowing");
    }

    @Test
    public void testBorrowUnavailableBookRejected() {
        libraryService.borrowBook("S101", "B101");

        // Second borrow attempt on the same book
        boolean secondBorrow = libraryService.borrowBook("S102", "B101");
        assertFalse(secondBorrow, "Borrowing an already borrowed book should be rejected");
    }

    @Test
    public void testBorrowNonExistentStudentRejected() {
        boolean result = libraryService.borrowBook("S999", "B101");
        assertFalse(result, "Borrowing with a non-existent student ID should be rejected");
        assertTrue(bookService.findBookById("B101").isAvailable(), "Book should remain available");
    }

    @Test
    public void testBorrowNonExistentBookRejected() {
        boolean result = libraryService.borrowBook("S101", "B999");
        assertFalse(result, "Borrowing with a non-existent book ID should be rejected");
        assertEquals(0, libraryService.getActiveBorrowings().size());
    }

    @Test
    public void testReturnActiveBorrowing() {
        libraryService.borrowBook("S101", "B101");
        boolean returned = libraryService.returnBook("S101", "B101");
        assertTrue(returned, "Returning an active loan should succeed");
        assertEquals(0, libraryService.getActiveBorrowings().size(), "There should be no active borrowings left");
    }

    @Test
    public void testReturnMakesBookAvailableAgain() {
        libraryService.borrowBook("S101", "B101");
        Book book = bookService.findBookById("B101");
        assertFalse(book.isAvailable());

        libraryService.returnBook("S101", "B101");
        assertTrue(book.isAvailable(), "Book should become available again after return");
    }

    @Test
    public void testReturnBookNoActiveBorrowingRejected() {
        boolean returned = libraryService.returnBook("S101", "B101");
        assertFalse(returned, "Returning a book that was never borrowed should be rejected");
    }

    @Test
    public void testActiveBorrowingListContainsActiveRecords() {
        libraryService.borrowBook("S101", "B101");
        libraryService.borrowBook("S102", "B102");

        ArrayList<BorrowRecord> active = libraryService.getActiveBorrowings();
        assertEquals(2, active.size());
        for (BorrowRecord record : active) {
            assertNull(record.getReturnDate(), "Active record returnDate must be null");
        }
    }

    @Test
    public void testReturnedBorrowingAppearsInHistory() {
        libraryService.borrowBook("S101", "B101");
        libraryService.returnBook("S101", "B101");

        ArrayList<BorrowRecord> history = libraryService.getBorrowingHistory();
        assertEquals(1, history.size(), "History should contain the completed borrow record");
        assertNotNull(history.get(0).getReturnDate(), "Returned record must have a return date set");
    }

    @Test
    public void testStudentBorrowingFiltering() {
        libraryService.borrowBook("S101", "B101");
        libraryService.borrowBook("S101", "B102");
        libraryService.borrowBook("S102", "B103");

        ArrayList<BorrowRecord> s101Records = libraryService.getStudentBorrowings("S101");
        ArrayList<BorrowRecord> s102Records = libraryService.getStudentBorrowings("S102");

        assertEquals(2, s101Records.size(), "Student S101 should have 2 borrow records");
        assertEquals(1, s102Records.size(), "Student S102 should have 1 borrow record");
    }

    @Test
    public void testBorrowRecordIdsGeneratedCorrectly() {
        libraryService.borrowBook("S101", "B101");
        libraryService.borrowBook("S102", "B102");

        ArrayList<BorrowRecord> active = libraryService.getActiveBorrowings();
        assertEquals("R001", active.get(0).getRecordId(), "First record ID should be R001");
        assertEquals("R002", active.get(1).getRecordId(), "Second record ID should be R002");
    }
}
