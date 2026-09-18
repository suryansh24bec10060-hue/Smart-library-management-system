# UML Diagrams: Smart Library Management System

This document provides visual models of the system using standard **Mermaid** notation, accurately reflecting the implemented classes, methods, and execution workflows.

---

## 1. Use Case Diagram

```mermaid
flowchart LR
    Librarian((Librarian / User))

    subgraph Smart Library Management System
        UC1([Manage Books\nAdd, View, Search, Update, Delete])
        UC2([Manage Students\nAdd, View, Search, Update, Delete])
        UC3([Borrow Book])
        UC4([Return Book])
        UC5([View Borrow History & Active Loans])
        UC6([Generate Library Reports & Summaries])
        UC7([Save and Restore Application State])
    end

    Librarian --> UC1
    Librarian --> UC2
    Librarian --> UC3
    Librarian --> UC4
    Librarian --> UC5
    Librarian --> UC6
    Librarian --> UC7
```

---

## 2. Class Diagram

```mermaid
classDiagram
    direction TB

    class Serializable {
        <<interface>>
    }

    class Book {
        -long serialVersionUID
        -String bookId
        -String title
        -String author
        -String category
        -boolean available
        +Book(String bookId, String title, String author, String category)
        +Book(String bookId, String title, String author, String category, boolean available)
        +getBookId() String
        +setBookId(String bookId) void
        +getTitle() String
        +setTitle(String title) void
        +getAuthor() String
        +setAuthor(String author) void
        +getCategory() String
        +setCategory(String category) void
        +isAvailable() boolean
        +setAvailable(boolean available) void
        +toString() String
    }

    class Student {
        -long serialVersionUID
        -String studentId
        -String name
        -String email
        -String phone
        +Student(String studentId, String name, String email, String phone)
        +getStudentId() String
        +setStudentId(String studentId) void
        +getName() String
        +setName(String name) void
        +getEmail() String
        +setEmail(String email) void
        +getPhone() String
        +setPhone(String phone) void
        +toString() String
    }

    class BorrowRecord {
        -long serialVersionUID
        -String recordId
        -String bookId
        -String studentId
        -LocalDate borrowDate
        -LocalDate returnDate
        +BorrowRecord(String recordId, String bookId, String studentId, LocalDate borrowDate)
        +BorrowRecord(String recordId, String bookId, String studentId, LocalDate borrowDate, LocalDate returnDate)
        +getRecordId() String
        +setRecordId(String recordId) void
        +getBookId() String
        +setBookId(String bookId) void
        +getStudentId() String
        +setStudentId(String studentId) void
        +getBorrowDate() LocalDate
        +setBorrowDate(LocalDate borrowDate) void
        +getReturnDate() LocalDate
        +setReturnDate(LocalDate returnDate) void
        +toString() String
    }

    class BookService {
        -ArrayList~Book~ books
        +BookService()
        +addBook(Book book) boolean
        +getAllBooks() ArrayList~Book~
        +setBooks(ArrayList~Book~ loadedBooks) void
        +findBookById(String bookId) Book
        +searchBooks(String keyword) ArrayList~Book~
        +updateBook(String bookId, String title, String author, String category) boolean
        +deleteBook(String bookId) boolean
        +getAvailableBooks() ArrayList~Book~
    }

    class StudentService {
        -ArrayList~Student~ students
        +StudentService()
        +addStudent(Student student) boolean
        +getAllStudents() ArrayList~Student~
        +setStudents(ArrayList~Student~ loadedStudents) void
        +findStudentById(String studentId) Student
        +searchStudents(String keyword) ArrayList~Student~
        +updateStudent(String studentId, String name, String email, String phone) boolean
        +deleteStudent(String studentId) boolean
    }

    class LibraryService {
        -BookService bookService
        -StudentService studentService
        -ArrayList~BorrowRecord~ borrowRecords
        -int recordCounter
        -String lastMessage
        +LibraryService(BookService bookService, StudentService studentService)
        +borrowBook(String studentId, String bookId) boolean
        +returnBook(String studentId, String bookId) boolean
        +getActiveBorrowings() ArrayList~BorrowRecord~
        +getBorrowingHistory() ArrayList~BorrowRecord~
        +setBorrowRecords(ArrayList~BorrowRecord~ loadedRecords) void
        +getStudentBorrowings(String studentId) ArrayList~BorrowRecord~
        +isBookCurrentlyBorrowed(String bookId) boolean
        +addSampleRecord(BorrowRecord record) void
        +getLastMessage() String
    }

    class ReportService {
        -BookService bookService
        -StudentService studentService
        -LibraryService libraryService
        +ReportService(BookService bookService, StudentService studentService, LibraryService libraryService)
        +getTotalBooks() int
        +getAvailableBookCount() int
        +getIssuedBookCount() int
        +getTotalStudents() int
        +getTotalBorrowings() int
        +getActiveBorrowingCount() int
        +getReturnedBorrowingCount() int
        +getLibrarySummary() String
        +getIssuedBooks() ArrayList~Book~
        +getStudentBorrowingSummary(String studentId) String
    }

    class LibraryData {
        -long serialVersionUID
        -ArrayList~Book~ books
        -ArrayList~Student~ students
        -ArrayList~BorrowRecord~ borrowRecords
        +LibraryData()
        +LibraryData(ArrayList~Book~ books, ArrayList~Student~ students, ArrayList~BorrowRecord~ borrowRecords)
        +getBooks() ArrayList~Book~
        +setBooks(ArrayList~Book~ books) void
        +getStudents() ArrayList~Student~
        +setStudents(ArrayList~Student~ students) void
        +getBorrowRecords() ArrayList~BorrowRecord~
        +setBorrowRecords(ArrayList~BorrowRecord~ borrowRecords) void
    }

    class FileManager {
        +String DEFAULT_FILE_PATH$
        -String filePath
        +FileManager()
        +FileManager(String filePath)
        +dataExists() boolean
        +saveData(LibraryData data) boolean
        +loadData() LibraryData
        +getFilePath() String
    }

    Serializable <|.. Book
    Serializable <|.. Student
    Serializable <|.. BorrowRecord
    Serializable <|.. LibraryData

    BookService o-- Book
    StudentService o-- Student
    LibraryService o-- BorrowRecord
    LibraryService --> BookService
    LibraryService --> StudentService

    ReportService --> BookService
    ReportService --> StudentService
    ReportService --> LibraryService

    FileManager ..> LibraryData
    LibraryData o-- Book
    LibraryData o-- Student
    LibraryData o-- BorrowRecord
```

---

## 3. Sequence Diagram: Borrow Book

```mermaid
sequenceDiagram
    autonumber
    actor User
    participant Main as Main (CLI)
    participant LS as LibraryService
    participant SS as StudentService
    participant BS as BookService
    participant BR as BorrowRecord

    User->>Main: Select "Borrow Book", enter studentId, bookId
    Main->>LS: borrowBook(studentId, bookId)
    LS->>SS: findStudentById(studentId)
    SS-->>LS: Student object (or null)
    
    alt Student does not exist
        LS-->>Main: false (lastMessage = "Student does not exist")
        Main-->>User: Display error message
    else Student exists
        LS->>BS: findBookById(bookId)
        BS-->>LS: Book object (or null)
        
        alt Book does not exist or unavailable
            LS-->>Main: false (lastMessage = "Book not available / not found")
            Main-->>User: Display error message
        else Book is available
            LS->>LS: Check duplicate active loan for student
            LS->>BS: book.setAvailable(false)
            LS->>BR: new BorrowRecord(newId, bookId, studentId, now, null)
            LS->>LS: borrowRecords.add(record)
            LS-->>Main: true (lastMessage = "Book successfully issued")
            Main-->>User: Display success with Record ID
        end
    end
```

---

## 4. Sequence Diagram: Return Book

```mermaid
sequenceDiagram
    autonumber
    actor User
    participant Main as Main (CLI)
    participant LS as LibraryService
    participant SS as StudentService
    participant BS as BookService
    participant BR as BorrowRecord

    User->>Main: Select "Return Book", enter studentId, bookId
    Main->>LS: returnBook(studentId, bookId)
    LS->>SS: findStudentById(studentId)
    SS-->>LS: Student object
    LS->>BS: findBookById(bookId)
    BS-->>LS: Book object

    LS->>LS: Locate active BorrowRecord (returnDate == null)
    alt No active loan record found
        LS-->>Main: false (lastMessage = "No active borrowing record found")
        Main-->>User: Display error message
    else Active record located
        LS->>BR: record.setReturnDate(LocalDate.now())
        LS->>BS: book.setAvailable(true)
        LS-->>Main: true (lastMessage = "Book successfully returned")
        Main-->>User: Display return confirmation
    end
```
