package com.library.service;

import com.library.model.Book;
import com.library.model.BorrowRecord;
import com.library.model.Student;

import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Service class that coordinates BookService, StudentService,
 * and manages borrowing and returning transactions in the library.
 */
public class LibraryService {
    private final BookService bookService;
    private final StudentService studentService;
    private final ArrayList<BorrowRecord> borrowRecords;
    private int recordCounter;
    private String lastMessage;

    public LibraryService(BookService bookService, StudentService studentService) {
        this.bookService = bookService;
        this.studentService = studentService;
        this.borrowRecords = new ArrayList<>();
        this.recordCounter = 1;
        this.lastMessage = "";
    }

    /**
     * Issues a book to a student.
     *
     * Rules:
     * - Student must exist.
     * - Book must exist.
     * - Book must currently be available.
     * - A student cannot borrow the same book if they already have an active borrowing record for it.
     * - Creates a unique BorrowRecord ID (e.g., R001, R002...).
     * - Sets borrowDate to LocalDate.now() and returnDate to null.
     * - Changes book availability to false.
     *
     * @param studentId The ID of the borrowing student.
     * @param bookId The ID of the book to borrow.
     * @return true if successful, false if any validation fails.
     */
    public boolean borrowBook(String studentId, String bookId) {
        if (studentId == null || studentId.trim().isEmpty()) {
            this.lastMessage = "Student ID cannot be empty.";
            return false;
        }
        if (bookId == null || bookId.trim().isEmpty()) {
            this.lastMessage = "Book ID cannot be empty.";
            return false;
        }

        // 1. Verify student exists
        Student student = studentService.findStudentById(studentId);
        if (student == null) {
            this.lastMessage = "Student with ID '" + studentId.trim() + "' does not exist.";
            return false;
        }

        // 2. Verify book exists
        Book book = bookService.findBookById(bookId);
        if (book == null) {
            this.lastMessage = "Book with ID '" + bookId.trim() + "' does not exist.";
            return false;
        }

        // 3. Verify book is currently available
        if (!book.isAvailable()) {
            this.lastMessage = "Book '" + book.getTitle() + "' (ID: " + book.getBookId() + ") is currently not available.";
            return false;
        }

        // 4. Verify student does not already have an active borrow for this book
        for (BorrowRecord record : borrowRecords) {
            if (record.getReturnDate() == null &&
                record.getStudentId().equalsIgnoreCase(student.getStudentId()) &&
                record.getBookId().equalsIgnoreCase(book.getBookId())) {
                this.lastMessage = "Student '" + student.getName() + "' already has an active borrow for this book.";
                return false;
            }
        }

        // 5. Generate unique record ID
        String recordId = String.format("R%03d", recordCounter++);

        // 6. Create BorrowRecord and update book availability
        BorrowRecord record = new BorrowRecord(recordId, book.getBookId(), student.getStudentId(), LocalDate.now(), null);
        borrowRecords.add(record);
        book.setAvailable(false);

        this.lastMessage = "Book '" + book.getTitle() + "' successfully issued to " + student.getName() + " (Record ID: " + recordId + ").";
        return true;
    }

    /**
     * Processes the return of a borrowed book.
     *
     * Rules:
     * - Student must exist.
     * - Book must exist.
     * - There must be an active borrowing record for this student and book.
     * - Sets returnDate to LocalDate.now().
     * - Changes book availability to true.
     *
     * @param studentId The ID of the returning student.
     * @param bookId The ID of the book being returned.
     * @return true if successfully returned, false otherwise.
     */
    public boolean returnBook(String studentId, String bookId) {
        if (studentId == null || studentId.trim().isEmpty()) {
            this.lastMessage = "Student ID cannot be empty.";
            return false;
        }
        if (bookId == null || bookId.trim().isEmpty()) {
            this.lastMessage = "Book ID cannot be empty.";
            return false;
        }

        // 1. Verify student exists
        Student student = studentService.findStudentById(studentId);
        if (student == null) {
            this.lastMessage = "Student with ID '" + studentId.trim() + "' does not exist.";
            return false;
        }

        // 2. Verify book exists
        Book book = bookService.findBookById(bookId);
        if (book == null) {
            this.lastMessage = "Book with ID '" + bookId.trim() + "' does not exist.";
            return false;
        }

        // 3. Find matching active borrow record
        BorrowRecord activeRecord = null;
        for (BorrowRecord record : borrowRecords) {
            if (record.getReturnDate() == null &&
                record.getStudentId().equalsIgnoreCase(student.getStudentId()) &&
                record.getBookId().equalsIgnoreCase(book.getBookId())) {
                activeRecord = record;
                break;
            }
        }

        if (activeRecord == null) {
            this.lastMessage = "No active borrowing record found for Student ID '" + student.getStudentId() + 
                               "' and Book ID '" + book.getBookId() + "'.";
            return false;
        }

        // 4. Update returnDate and mark book as available
        activeRecord.setReturnDate(LocalDate.now());
        book.setAvailable(true);

        this.lastMessage = "Book '" + book.getTitle() + "' successfully returned by " + student.getName() + 
                           " on " + LocalDate.now() + ".";
        return true;
    }

    /**
     * Returns all active borrow records (where returnDate is null).
     *
     * @return A list of active BorrowRecords.
     */
    public ArrayList<BorrowRecord> getActiveBorrowings() {
        ArrayList<BorrowRecord> active = new ArrayList<>();
        for (BorrowRecord record : borrowRecords) {
            if (record.getReturnDate() == null) {
                active.add(record);
            }
        }
        return active;
    }

    /**
     * Returns the complete borrowing history, including returned books.
     *
     * @return A new list containing all BorrowRecords.
     */
    public ArrayList<BorrowRecord> getBorrowingHistory() {
        return new ArrayList<>(borrowRecords);
    }

    /**
     * Replaces internal borrow records with loaded records from persistence.
     * Updates recordCounter to ensure new records receive strictly unique IDs.
     *
     * @param loadedRecords Restored borrow records list.
     */
    public void setBorrowRecords(ArrayList<BorrowRecord> loadedRecords) {
        this.borrowRecords.clear();
        if (loadedRecords != null) {
            this.borrowRecords.addAll(loadedRecords);
            for (BorrowRecord r : loadedRecords) {
                try {
                    int num = Integer.parseInt(r.getRecordId().replaceAll("[^0-9]", ""));
                    if (num >= recordCounter) {
                        recordCounter = num + 1;
                    }
                } catch (NumberFormatException ignored) {
                }
            }
        }
    }

    /**
     * Returns all borrow records associated with a specific student.
     *
     * @param studentId The ID of the student.
     * @return A list of borrowing records for that student.
     */
    public ArrayList<BorrowRecord> getStudentBorrowings(String studentId) {
        ArrayList<BorrowRecord> results = new ArrayList<>();
        if (studentId == null || studentId.trim().isEmpty()) {
            return results;
        }
        for (BorrowRecord record : borrowRecords) {
            if (record.getStudentId().equalsIgnoreCase(studentId.trim())) {
                results.add(record);
            }
        }
        return results;
    }

    /**
     * Checks if a specified book currently has an active borrowing record.
     *
     * @param bookId The ID of the book.
     * @return true if the book is currently borrowed, false otherwise.
     */
    public boolean isBookCurrentlyBorrowed(String bookId) {
        if (bookId == null || bookId.trim().isEmpty()) {
            return false;
        }
        for (BorrowRecord record : borrowRecords) {
            if (record.getBookId().equalsIgnoreCase(bookId.trim()) && record.getReturnDate() == null) {
                return true;
            }
        }
        return false;
    }

    /**
     * Helper method to directly add a pre-existing record (e.g. for sample data setup).
     */
    public void addSampleRecord(BorrowRecord record) {
        if (record != null) {
            borrowRecords.add(record);
            // Ensure recordCounter stays ahead
            try {
                int num = Integer.parseInt(record.getRecordId().replaceAll("[^0-9]", ""));
                if (num >= recordCounter) {
                    recordCounter = num + 1;
                }
            } catch (NumberFormatException ignored) {
            }
        }
    }

    /**
     * Returns the descriptive message from the last operation.
     */
    public String getLastMessage() {
        return lastMessage;
    }
}
