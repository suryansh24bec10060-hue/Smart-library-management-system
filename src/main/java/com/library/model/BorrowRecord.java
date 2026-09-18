package com.library.model;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Represents a record of a borrowed book by a student.
 */
public class BorrowRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    private String recordId;
    private String bookId;
    private String studentId;
    private LocalDate borrowDate;
    private LocalDate returnDate;

    // Constructor when issuing a book (returnDate starts as null)
    public BorrowRecord(String recordId, String bookId, String studentId, LocalDate borrowDate) {
        this(recordId, bookId, studentId, borrowDate, null);
    }

    // Full constructor
    public BorrowRecord(String recordId, String bookId, String studentId, LocalDate borrowDate, LocalDate returnDate) {
        setRecordId(recordId);
        setBookId(bookId);
        setStudentId(studentId);
        setBorrowDate(borrowDate);
        setReturnDate(returnDate);
    }

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        if (recordId == null || recordId.trim().isEmpty()) {
            throw new IllegalArgumentException("Record ID cannot be null or empty.");
        }
        this.recordId = recordId.trim();
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

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        if (studentId == null || studentId.trim().isEmpty()) {
            throw new IllegalArgumentException("Student ID cannot be null or empty.");
        }
        this.studentId = studentId.trim();
    }

    public LocalDate getBorrowDate() {
        return borrowDate;
    }

    public void setBorrowDate(LocalDate borrowDate) {
        if (borrowDate == null) {
            throw new IllegalArgumentException("Borrow date cannot be null.");
        }
        this.borrowDate = borrowDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        if (returnDate != null && borrowDate != null && returnDate.isBefore(borrowDate)) {
            throw new IllegalArgumentException("Return date cannot be before borrow date.");
        }
        this.returnDate = returnDate;
    }

    @Override
    public String toString() {
        String returnStatus = (returnDate != null) ? returnDate.toString() : "Not Returned";
        return "BorrowRecord [RecordID=" + recordId + ", BookID=" + bookId + 
               ", StudentID=" + studentId + ", BorrowDate=" + borrowDate + 
               ", ReturnDate=" + returnStatus + "]";
    }
}
