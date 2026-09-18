package com.library.service;

import com.library.model.Book;
import com.library.model.BorrowRecord;
import com.library.model.Student;

import java.util.ArrayList;

/**
 * Service responsible for aggregating metrics, generating reports,
 * and producing statistical summaries by coordinating existing services.
 * Does not store or duplicate any entity data.
 */
public class ReportService {
    private final BookService bookService;
    private final StudentService studentService;
    private final LibraryService libraryService;

    public ReportService(BookService bookService, StudentService studentService, LibraryService libraryService) {
        this.bookService = bookService;
        this.studentService = studentService;
        this.libraryService = libraryService;
    }

    /**
     * Returns the total number of registered books in the library.
     */
    public int getTotalBooks() {
        return bookService.getAllBooks().size();
    }

    /**
     * Returns the number of books currently available for borrowing.
     */
    public int getAvailableBookCount() {
        return bookService.getAvailableBooks().size();
    }

    /**
     * Returns the number of books currently issued to students.
     */
    public int getIssuedBookCount() {
        return getTotalBooks() - getAvailableBookCount();
    }

    /**
     * Returns the total number of registered students.
     */
    public int getTotalStudents() {
        return studentService.getAllStudents().size();
    }

    /**
     * Returns the total number of borrow records (active and completed).
     */
    public int getTotalBorrowings() {
        return libraryService.getBorrowingHistory().size();
    }

    /**
     * Returns the number of currently active borrowings.
     */
    public int getActiveBorrowingCount() {
        return libraryService.getActiveBorrowings().size();
    }

    /**
     * Returns the number of completed/returned borrowings.
     */
    public int getReturnedBorrowingCount() {
        return getTotalBorrowings() - getActiveBorrowingCount();
    }

    /**
     * Generates a formatted summary containing all key library metrics.
     */
    public String getLibrarySummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("========================================\n");
        sb.append("LIBRARY SUMMARY\n");
        sb.append("===============\n");
        sb.append(String.format("Total Books        : %d\n", getTotalBooks()));
        sb.append(String.format("Available Books    : %d\n", getAvailableBookCount()));
        sb.append(String.format("Issued Books       : %d\n", getIssuedBookCount()));
        sb.append(String.format("Total Students     : %d\n", getTotalStudents()));
        sb.append(String.format("Total Borrowings   : %d\n", getTotalBorrowings()));
        sb.append(String.format("Active Borrowings  : %d\n", getActiveBorrowingCount()));
        sb.append(String.format("Returned Borrowings: %d\n", getReturnedBorrowingCount()));
        sb.append("========================================");
        return sb.toString();
    }

    /**
     * Returns a list of books that are currently issued/unavailable.
     */
    public ArrayList<Book> getIssuedBooks() {
        ArrayList<Book> issuedBooks = new ArrayList<>();
        for (Book book : bookService.getAllBooks()) {
            if (!book.isAvailable()) {
                issuedBooks.add(book);
            }
        }
        return issuedBooks;
    }

    /**
     * Generates a detailed borrowing summary for a specific student.
     * Handles non-existent or invalid student IDs gracefully.
     *
     * @param studentId The ID of the student.
     * @return Formatted summary or error message.
     */
    public String getStudentBorrowingSummary(String studentId) {
        if (studentId == null || studentId.trim().isEmpty()) {
            return "[✗] Error: Student ID cannot be empty.";
        }

        Student student = studentService.findStudentById(studentId.trim());
        if (student == null) {
            return "[✗] Error: Student with ID '" + studentId.trim() + "' does not exist.";
        }

        ArrayList<BorrowRecord> records = libraryService.getStudentBorrowings(student.getStudentId());
        int total = records.size();
        int active = 0;
        int returned = 0;

        for (BorrowRecord r : records) {
            if (r.getReturnDate() == null) {
                active++;
            } else {
                returned++;
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append("==================================================\n");
        sb.append("BORROWING SUMMARY: ").append(student.getName())
          .append(" (ID: ").append(student.getStudentId()).append(")\n");
        sb.append("Email: ").append(student.getEmail()).append(" | Phone: ").append(student.getPhone()).append("\n");
        sb.append("--------------------------------------------------\n");
        sb.append("Total Borrowings   : ").append(total).append("\n");
        sb.append("Active Borrowings  : ").append(active).append("\n");
        sb.append("Returned Borrowings: ").append(returned).append("\n");
        sb.append("--------------------------------------------------\n");

        if (records.isEmpty()) {
            sb.append("[i] No borrowing history found for this student.\n");
        } else {
            sb.append("Borrowing Details:\n");
            for (BorrowRecord r : records) {
                Book book = bookService.findBookById(r.getBookId());
                String bookTitle = (book != null) ? book.getTitle() : "Unknown Title";
                String status = (r.getReturnDate() == null) 
                        ? "Active (Borrowed: " + r.getBorrowDate() + ")" 
                        : "Returned (Borrowed: " + r.getBorrowDate() + " | Returned: " + r.getReturnDate() + ")";
                sb.append(String.format(" - Record %s: '%s' (Book ID: %s) -> %s\n", 
                        r.getRecordId(), bookTitle, r.getBookId(), status));
            }
        }
        sb.append("==================================================");
        return sb.toString();
    }
}
