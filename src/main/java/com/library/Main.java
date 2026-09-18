package com.library;

import com.library.model.Book;
import com.library.model.BorrowRecord;
import com.library.model.Student;
import com.library.service.BookService;
import com.library.service.LibraryService;
import com.library.service.ReportService;
import com.library.service.StudentService;
import com.library.util.FileManager;
import com.library.util.LibraryData;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Main application class providing a command-line interface (CLI)
 * for the Smart Library Management System with local file persistence.
 */
public class Main {
    public static void main(String[] args) {
        BookService bookService = new BookService();
        StudentService studentService = new StudentService();
        LibraryService libraryService = new LibraryService(bookService, studentService);
        ReportService reportService = new ReportService(bookService, studentService, libraryService);
        FileManager fileManager = new FileManager();
        Scanner scanner = new Scanner(System.in);

        // Check and load persisted data or fallback to demo data
        initializeApplicationData(bookService, studentService, libraryService, fileManager);

        boolean running = true;
        while (running) {
            printMainMenu();
            System.out.print("Enter your choice (1-5): ");
            String input = scanner.nextLine().trim();

            int choice;
            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("\n[!] Invalid input. Please enter a valid number (1-5).\n");
                continue;
            }

            switch (choice) {
                case 1:
                    handleBookManagementMenu(bookService, scanner);
                    break;
                case 2:
                    handleStudentManagementMenu(studentService, scanner);
                    break;
                case 3:
                    handleBorrowReturnMenu(libraryService, scanner);
                    break;
                case 4:
                    handleReportsMenu(reportService, scanner);
                    break;
                case 5:
                    handleExit(bookService, studentService, libraryService, fileManager);
                    running = false;
                    break;
                default:
                    System.out.println("\n[!] Invalid choice. Please choose an option between 1 and 5.\n");
            }
        }

        scanner.close();
    }

    // ==========================================
    // PERSISTENCE & INITIALIZATION LIFECYCLE
    // ==========================================

    private static void initializeApplicationData(BookService bookService,
                                                  StudentService studentService,
                                                  LibraryService libraryService,
                                                  FileManager fileManager) {
        if (fileManager.dataExists()) {
            LibraryData loadedData = fileManager.loadData();
            if (loadedData != null) {
                bookService.setBooks(loadedData.getBooks());
                studentService.setStudents(loadedData.getStudents());
                libraryService.setBorrowRecords(loadedData.getBorrowRecords());
                System.out.println("[i] Loaded existing library data from '" + fileManager.getFilePath() + "'.");
                return;
            } else {
                System.out.println("[!] Warning: Saved data could not be parsed. Loading default sample data.");
            }
        } else {
            System.out.println("[i] No existing data file found. Initialized system with sample demo data.");
        }

        // Initialize sample demo data if no prior file exists or file was invalid
        loadSampleBooks(bookService);
        loadSampleStudents(studentService);
        loadSampleBorrowRecords(libraryService);
    }

    private static void handleExit(BookService bookService,
                                   StudentService studentService,
                                   LibraryService libraryService,
                                   FileManager fileManager) {
        System.out.println("\nSaving application state...");
        LibraryData dataToSave = new LibraryData(
                bookService.getAllBooks(),
                studentService.getAllStudents(),
                libraryService.getBorrowingHistory()
        );

        boolean saved = fileManager.saveData(dataToSave);
        if (saved) {
            System.out.println("[✓] Library data saved successfully to '" + fileManager.getFilePath() + "'.");
        } else {
            System.out.println("[✗] Warning: Could not save data to '" + fileManager.getFilePath() + "'.");
        }
        System.out.println("Exiting Smart Library Management System. Goodbye!");
    }

    // ==========================================
    // MAIN MENU
    // ==========================================

    private static void printMainMenu() {
        System.out.println("========================================");
        System.out.println("SMART LIBRARY MANAGEMENT SYSTEM");
        System.out.println("===============================");
        System.out.println("1. Book Management");
        System.out.println("2. Student Management");
        System.out.println("3. Borrow / Return Management");
        System.out.println("4. Reports & Statistics");
        System.out.println("5. Exit");
        System.out.println("----------------------------------------");
    }

    // ==========================================
    // BOOK MANAGEMENT SUBMENU
    // ==========================================

    private static void handleBookManagementMenu(BookService bookService, Scanner scanner) {
        boolean back = false;
        while (!back) {
            printBookMenu();
            System.out.print("Enter your choice (1-7): ");
            String input = scanner.nextLine().trim();

            int choice;
            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("\n[!] Invalid input. Please enter a valid number between 1 and 7.\n");
                continue;
            }

            switch (choice) {
                case 1:
                    handleAddBook(bookService, scanner);
                    break;
                case 2:
                    handleViewAllBooks(bookService);
                    break;
                case 3:
                    handleSearchBook(bookService, scanner);
                    break;
                case 4:
                    handleUpdateBook(bookService, scanner);
                    break;
                case 5:
                    handleDeleteBook(bookService, scanner);
                    break;
                case 6:
                    handleViewAvailableBooks(bookService);
                    break;
                case 7:
                    back = true;
                    System.out.println("\nReturning to Main Menu...\n");
                    break;
                default:
                    System.out.println("\n[!] Invalid choice. Please choose an option between 1 and 7.\n");
            }
        }
    }

    private static void printBookMenu() {
        System.out.println("\n--------- BOOK MANAGEMENT ---------");
        System.out.println("1. Add Book");
        System.out.println("2. View All Books");
        System.out.println("3. Search Book");
        System.out.println("4. Update Book");
        System.out.println("5. Delete Book");
        System.out.println("6. View Available Books");
        System.out.println("7. Back to Main Menu");
        System.out.println("-----------------------------------");
    }

    private static void handleAddBook(BookService bookService, Scanner scanner) {
        System.out.println("\n--- Add New Book ---");
        System.out.print("Enter Book ID: ");
        String id = scanner.nextLine().trim();

        System.out.print("Enter Title: ");
        String title = scanner.nextLine().trim();

        System.out.print("Enter Author: ");
        String author = scanner.nextLine().trim();

        System.out.print("Enter Category: ");
        String category = scanner.nextLine().trim();

        try {
            Book book = new Book(id, title, author, category, true);
            boolean added = bookService.addBook(book);
            if (added) {
                System.out.println("[✓] Book added successfully!\n");
            } else {
                System.out.println("[✗] Error: A book with ID '" + id + "' already exists.\n");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("[✗] Validation Error: " + e.getMessage() + "\n");
        }
    }

    private static void handleViewAllBooks(BookService bookService) {
        System.out.println("\n--- All Registered Books ---");
        ArrayList<Book> books = bookService.getAllBooks();
        displayBooks(books);
    }

    private static void handleSearchBook(BookService bookService, Scanner scanner) {
        System.out.println("\n--- Search Books ---");
        System.out.print("Enter search keyword (title, author, or category): ");
        String keyword = scanner.nextLine().trim();

        ArrayList<Book> results = bookService.searchBooks(keyword);
        if (results.isEmpty()) {
            System.out.println("[i] No books found matching keyword: \"" + keyword + "\"\n");
        } else {
            System.out.println("\nFound " + results.size() + " matching book(s):");
            displayBooks(results);
        }
    }

    private static void handleUpdateBook(BookService bookService, Scanner scanner) {
        System.out.println("\n--- Update Book Details ---");
        System.out.print("Enter Book ID to update: ");
        String id = scanner.nextLine().trim();

        Book existing = bookService.findBookById(id);
        if (existing == null) {
            System.out.println("[✗] Error: Book with ID '" + id + "' does not exist.\n");
            return;
        }

        System.out.println("Current Details: " + existing);
        System.out.print("Enter New Title: ");
        String newTitle = scanner.nextLine().trim();

        System.out.print("Enter New Author: ");
        String newAuthor = scanner.nextLine().trim();

        System.out.print("Enter New Category: ");
        String newCategory = scanner.nextLine().trim();

        boolean updated = bookService.updateBook(id, newTitle, newAuthor, newCategory);
        if (updated) {
            System.out.println("[✓] Book updated successfully!\n");
        } else {
            System.out.println("[✗] Error: Failed to update book. Please check your inputs.\n");
        }
    }

    private static void handleDeleteBook(BookService bookService, Scanner scanner) {
        System.out.println("\n--- Delete Book ---");
        System.out.print("Enter Book ID to delete: ");
        String id = scanner.nextLine().trim();

        Book existing = bookService.findBookById(id);
        if (existing == null) {
            System.out.println("[✗] Error: Book with ID '" + id + "' does not exist.\n");
            return;
        }

        if (!existing.isAvailable()) {
            System.out.println("[✗] Error: Cannot delete book '" + existing.getTitle() + 
                               "' because it is currently issued.\n");
            return;
        }

        boolean deleted = bookService.deleteBook(id);
        if (deleted) {
            System.out.println("[✓] Book with ID '" + id + "' deleted successfully!\n");
        } else {
            System.out.println("[✗] Error: Unable to delete book.\n");
        }
    }

    private static void handleViewAvailableBooks(BookService bookService) {
        System.out.println("\n--- Available Books ---");
        ArrayList<Book> available = bookService.getAvailableBooks();
        displayBooks(available);
    }

    private static void displayBooks(ArrayList<Book> books) {
        if (books.isEmpty()) {
            System.out.println("[i] No books to display.\n");
            return;
        }
        for (Book book : books) {
            System.out.println(book);
        }
        System.out.println();
    }

    // ==========================================
    // STUDENT MANAGEMENT SUBMENU
    // ==========================================

    private static void handleStudentManagementMenu(StudentService studentService, Scanner scanner) {
        boolean back = false;
        while (!back) {
            printStudentMenu();
            System.out.print("Enter your choice (1-6): ");
            String input = scanner.nextLine().trim();

            int choice;
            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("\n[!] Invalid input. Please enter a valid number between 1 and 6.\n");
                continue;
            }

            switch (choice) {
                case 1:
                    handleAddStudent(studentService, scanner);
                    break;
                case 2:
                    handleViewAllStudents(studentService);
                    break;
                case 3:
                    handleSearchStudent(studentService, scanner);
                    break;
                case 4:
                    handleUpdateStudent(studentService, scanner);
                    break;
                case 5:
                    handleDeleteStudent(studentService, scanner);
                    break;
                case 6:
                    back = true;
                    System.out.println("\nReturning to Main Menu...\n");
                    break;
                default:
                    System.out.println("\n[!] Invalid choice. Please choose an option between 1 and 6.\n");
            }
        }
    }

    private static void printStudentMenu() {
        System.out.println("\n--------- STUDENT MANAGEMENT ---------");
        System.out.println("1. Add Student");
        System.out.println("2. View All Students");
        System.out.println("3. Search Student");
        System.out.println("4. Update Student");
        System.out.println("5. Delete Student");
        System.out.println("6. Back");
        System.out.println("--------------------------------------");
    }

    private static void handleAddStudent(StudentService studentService, Scanner scanner) {
        System.out.println("\n--- Add New Student ---");
        System.out.print("Enter Student ID: ");
        String id = scanner.nextLine().trim();

        System.out.print("Enter Name: ");
        String name = scanner.nextLine().trim();

        System.out.print("Enter Email: ");
        String email = scanner.nextLine().trim();

        System.out.print("Enter Phone: ");
        String phone = scanner.nextLine().trim();

        try {
            Student student = new Student(id, name, email, phone);
            boolean added = studentService.addStudent(student);
            if (added) {
                System.out.println("[✓] Student added successfully!\n");
            } else {
                System.out.println("[✗] Error: A student with ID '" + id + "' already exists.\n");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("[✗] Validation Error: " + e.getMessage() + "\n");
        }
    }

    private static void handleViewAllStudents(StudentService studentService) {
        System.out.println("\n--- All Registered Students ---");
        ArrayList<Student> students = studentService.getAllStudents();
        displayStudents(students);
    }

    private static void handleSearchStudent(StudentService studentService, Scanner scanner) {
        System.out.println("\n--- Search Student ---");
        System.out.print("Enter search keyword (name, email, or phone): ");
        String keyword = scanner.nextLine().trim();

        ArrayList<Student> results = studentService.searchStudents(keyword);
        if (results.isEmpty()) {
            System.out.println("[i] No students found matching keyword: \"" + keyword + "\"\n");
        } else {
            System.out.println("\nFound " + results.size() + " matching student(s):");
            displayStudents(results);
        }
    }

    private static void handleUpdateStudent(StudentService studentService, Scanner scanner) {
        System.out.println("\n--- Update Student Details ---");
        System.out.print("Enter Student ID to update: ");
        String id = scanner.nextLine().trim();

        Student existing = studentService.findStudentById(id);
        if (existing == null) {
            System.out.println("[✗] Error: Student with ID '" + id + "' does not exist.\n");
            return;
        }

        System.out.println("Current Details: " + existing);
        System.out.print("Enter New Name: ");
        String newName = scanner.nextLine().trim();

        System.out.print("Enter New Email: ");
        String newEmail = scanner.nextLine().trim();

        System.out.print("Enter New Phone: ");
        String newPhone = scanner.nextLine().trim();

        boolean updated = studentService.updateStudent(id, newName, newEmail, newPhone);
        if (updated) {
            System.out.println("[✓] Student updated successfully!\n");
        } else {
            System.out.println("[✗] Error: Failed to update student. Please check your inputs.\n");
        }
    }

    private static void handleDeleteStudent(StudentService studentService, Scanner scanner) {
        System.out.println("\n--- Delete Student ---");
        System.out.print("Enter Student ID to delete: ");
        String id = scanner.nextLine().trim();

        Student existing = studentService.findStudentById(id);
        if (existing == null) {
            System.out.println("[✗] Error: Student with ID '" + id + "' does not exist.\n");
            return;
        }

        boolean deleted = studentService.deleteStudent(id);
        if (deleted) {
            System.out.println("[✓] Student with ID '" + id + "' deleted successfully!\n");
        } else {
            System.out.println("[✗] Error: Unable to delete student.\n");
        }
    }

    private static void displayStudents(ArrayList<Student> students) {
        if (students.isEmpty()) {
            System.out.println("[i] No students to display.\n");
            return;
        }
        for (Student student : students) {
            System.out.println(student);
        }
        System.out.println();
    }

    // ==========================================
    // BORROW & RETURN MANAGEMENT SUBMENU
    // ==========================================

    private static void handleBorrowReturnMenu(LibraryService libraryService, Scanner scanner) {
        boolean back = false;
        while (!back) {
            printBorrowReturnMenu();
            System.out.print("Enter your choice (1-6): ");
            String input = scanner.nextLine().trim();

            int choice;
            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("\n[!] Invalid input. Please enter a valid number between 1 and 6.\n");
                continue;
            }

            switch (choice) {
                case 1:
                    handleBorrowBook(libraryService, scanner);
                    break;
                case 2:
                    handleReturnBook(libraryService, scanner);
                    break;
                case 3:
                    handleViewActiveBorrowings(libraryService);
                    break;
                case 4:
                    handleViewBorrowingHistory(libraryService);
                    break;
                case 5:
                    handleViewStudentBorrowings(libraryService, scanner);
                    break;
                case 6:
                    back = true;
                    System.out.println("\nReturning to Main Menu...\n");
                    break;
                default:
                    System.out.println("\n[!] Invalid choice. Please choose an option between 1 and 6.\n");
            }
        }
    }

    private static void printBorrowReturnMenu() {
        System.out.println("\n--------- BORROW & RETURN MANAGEMENT ---------");
        System.out.println("1. Borrow Book");
        System.out.println("2. Return Book");
        System.out.println("3. View Active Borrowings");
        System.out.println("4. View Borrowing History");
        System.out.println("5. View Student Borrowings");
        System.out.println("6. Back");
        System.out.println("----------------------------------------------");
    }

    private static void handleBorrowBook(LibraryService libraryService, Scanner scanner) {
        System.out.println("\n--- Borrow Book ---");
        System.out.print("Enter Student ID: ");
        String studentId = scanner.nextLine().trim();

        System.out.print("Enter Book ID: ");
        String bookId = scanner.nextLine().trim();

        boolean success = libraryService.borrowBook(studentId, bookId);
        if (success) {
            System.out.println("[✓] " + libraryService.getLastMessage() + "\n");
        } else {
            System.out.println("[✗] " + libraryService.getLastMessage() + "\n");
        }
    }

    private static void handleReturnBook(LibraryService libraryService, Scanner scanner) {
        System.out.println("\n--- Return Book ---");
        System.out.print("Enter Student ID: ");
        String studentId = scanner.nextLine().trim();

        System.out.print("Enter Book ID: ");
        String bookId = scanner.nextLine().trim();

        boolean success = libraryService.returnBook(studentId, bookId);
        if (success) {
            System.out.println("[✓] " + libraryService.getLastMessage() + "\n");
        } else {
            System.out.println("[✗] " + libraryService.getLastMessage() + "\n");
        }
    }

    private static void handleViewActiveBorrowings(LibraryService libraryService) {
        System.out.println("\n--- Active Borrowings ---");
        ArrayList<BorrowRecord> active = libraryService.getActiveBorrowings();
        displayBorrowRecords(active);
    }

    private static void handleViewBorrowingHistory(LibraryService libraryService) {
        System.out.println("\n--- Borrowing History ---");
        ArrayList<BorrowRecord> history = libraryService.getBorrowingHistory();
        displayBorrowRecords(history);
    }

    private static void handleViewStudentBorrowings(LibraryService libraryService, Scanner scanner) {
        System.out.println("\n--- View Student Borrowings ---");
        System.out.print("Enter Student ID: ");
        String studentId = scanner.nextLine().trim();

        ArrayList<BorrowRecord> records = libraryService.getStudentBorrowings(studentId);
        if (records.isEmpty()) {
            System.out.println("[i] No borrowing records found for Student ID: '" + studentId + "'.\n");
        } else {
            System.out.println("\nBorrowing Records for Student '" + studentId + "':");
            displayBorrowRecords(records);
        }
    }

    private static void displayBorrowRecords(ArrayList<BorrowRecord> records) {
        if (records.isEmpty()) {
            System.out.println("[i] No borrow records to display.\n");
            return;
        }
        for (BorrowRecord record : records) {
            System.out.println(record);
        }
        System.out.println();
    }

    // ==========================================
    // REPORTS & STATISTICS SUBMENU
    // ==========================================

    private static void handleReportsMenu(ReportService reportService, Scanner scanner) {
        boolean back = false;
        while (!back) {
            printReportsMenu();
            System.out.print("Enter your choice (1-4): ");
            String input = scanner.nextLine().trim();

            int choice;
            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("\n[!] Invalid input. Please enter a valid number between 1 and 4.\n");
                continue;
            }

            switch (choice) {
                case 1:
                    System.out.println("\n" + reportService.getLibrarySummary() + "\n");
                    break;
                case 2:
                    handleViewIssuedBooks(reportService);
                    break;
                case 3:
                    handleViewStudentBorrowingSummary(reportService, scanner);
                    break;
                case 4:
                    back = true;
                    System.out.println("\nReturning to Main Menu...\n");
                    break;
                default:
                    System.out.println("\n[!] Invalid choice. Please choose an option between 1 and 4.\n");
            }
        }
    }

    private static void printReportsMenu() {
        System.out.println("\n--------- REPORTS & STATISTICS ---------");
        System.out.println("1. Library Summary");
        System.out.println("2. View Currently Issued Books");
        System.out.println("3. View Student Borrowing Summary");
        System.out.println("4. Back");
        System.out.println("----------------------------------------");
    }

    private static void handleViewIssuedBooks(ReportService reportService) {
        System.out.println("\n--- Currently Issued Books ---");
        ArrayList<Book> issued = reportService.getIssuedBooks();
        if (issued.isEmpty()) {
            System.out.println("[i] No books are currently issued.\n");
            return;
        }
        for (Book book : issued) {
            System.out.printf("[ID: %s] Title: %s | Author: %s | Category: %s\n",
                    book.getBookId(), book.getTitle(), book.getAuthor(), book.getCategory());
        }
        System.out.println();
    }

    private static void handleViewStudentBorrowingSummary(ReportService reportService, Scanner scanner) {
        System.out.println("\n--- Student Borrowing Summary ---");
        System.out.print("Enter Student ID: ");
        String studentId = scanner.nextLine().trim();

        System.out.println("\n" + reportService.getStudentBorrowingSummary(studentId) + "\n");
    }

    // ==========================================
    // SAMPLE DATA INITIALIZATION (FALLBACK / DEMO)
    // ==========================================

    private static void loadSampleBooks(BookService bookService) {
        bookService.addBook(new Book("B101", "Clean Code", "Robert C. Martin", "Software Engineering", true));
        bookService.addBook(new Book("B102", "Effective Java", "Joshua Bloch", "Programming", true));
        bookService.addBook(new Book("B103", "Introduction to Algorithms", "Thomas H. Cormen", "Computer Science", false));
        bookService.addBook(new Book("B104", "Design Patterns", "Erich Gamma", "Software Engineering", true));
    }

    private static void loadSampleStudents(StudentService studentService) {
        studentService.addStudent(new Student("S101", "Alice Johnson", "alice@university.edu", "9876543210"));
        studentService.addStudent(new Student("S102", "Bob Smith", "bob@university.edu", "8765432109"));
        studentService.addStudent(new Student("S103", "Charlie Davis", "charlie@university.edu", "7654321098"));
    }

    private static void loadSampleBorrowRecords(LibraryService libraryService) {
        // Record R001: Historical returned record (Bob Smith borrowed Effective Java and returned it)
        BorrowRecord pastRecord = new BorrowRecord("R001", "B102", "S102", 
                LocalDate.now().minusDays(10), LocalDate.now().minusDays(2));
        libraryService.addSampleRecord(pastRecord);

        // Record R002: Active borrow record (Alice Johnson currently has Introduction to Algorithms B103)
        BorrowRecord activeRecord = new BorrowRecord("R002", "B103", "S101", 
                LocalDate.now().minusDays(5), null);
        libraryService.addSampleRecord(activeRecord);
    }
}
