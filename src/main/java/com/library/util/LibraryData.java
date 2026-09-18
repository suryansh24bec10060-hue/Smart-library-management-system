package com.library.util;

import com.library.model.Book;
import com.library.model.BorrowRecord;
import com.library.model.Student;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Encapsulates the entire persistent state of the Smart Library Management System:
 * registered books, registered students, and borrowing records.
 */
public class LibraryData implements Serializable {
    private static final long serialVersionUID = 1L;

    private ArrayList<Book> books;
    private ArrayList<Student> students;
    private ArrayList<BorrowRecord> borrowRecords;

    public LibraryData() {
        this.books = new ArrayList<>();
        this.students = new ArrayList<>();
        this.borrowRecords = new ArrayList<>();
    }

    public LibraryData(ArrayList<Book> books, ArrayList<Student> students, ArrayList<BorrowRecord> borrowRecords) {
        this.books = (books != null) ? books : new ArrayList<>();
        this.students = (students != null) ? students : new ArrayList<>();
        this.borrowRecords = (borrowRecords != null) ? borrowRecords : new ArrayList<>();
    }

    public ArrayList<Book> getBooks() {
        return books;
    }

    public void setBooks(ArrayList<Book> books) {
        this.books = books;
    }

    public ArrayList<Student> getStudents() {
        return students;
    }

    public void setStudents(ArrayList<Student> students) {
        this.students = students;
    }

    public ArrayList<BorrowRecord> getBorrowRecords() {
        return borrowRecords;
    }

    public void setBorrowRecords(ArrayList<BorrowRecord> borrowRecords) {
        this.borrowRecords = borrowRecords;
    }
}
