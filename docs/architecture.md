# System Architecture: Smart Library Management System

## 1. Architectural Overview

The **Smart Library Management System** is designed following a clean, decoupled **Multi-Layered Architecture**. The design enforces strict Separation of Concerns (SoC) and the Single Responsibility Principle (SRP), ensuring that business logic, data models, persistence, and user interfaces operate independently.

```
+-------------------------------------------------------------+
|               Presentation Layer (CLI / Main)               |
|            - Menu rendering, user input via Scanner         |
|            - Input format validation & error display        |
+-------------------------------------------------------------+
                              |
                              v
+-------------------------------------------------------------+
|                        Service Layer                        |
|  - BookService: Catalog management & search                 |
|  - StudentService: Membership & profile management          |
|  - LibraryService: Loan/Return workflows & business rules   |
|  - ReportService: Statistics & metrics aggregation          |
+-------------------------------------------------------------+
                              |
                              v
+-------------------------------------------------------------+
|                      Domain Model Layer                     |
|  - Book: Entity representing catalog items                  |
|  - Student: Entity representing registered members          |
|  - BorrowRecord: Entity representing loan transactions      |
+-------------------------------------------------------------+
                              |
                              v
+-------------------------------------------------------------+
|                      Persistence Layer                      |
|  - LibraryData: Composite serializable data transfer object |
|  - FileManager: Java Object Streams (data/library.dat)      |
+-------------------------------------------------------------+
```

---

## 2. Layer Responsibilities

### 2.1 Presentation Layer (`com.library.Main`)
* **Role:** Serves as the interactive Command-Line Interface (CLI).
* **Responsibilities:**
  * Displays hierarchical menus (Main Menu, Book Management, Student Management, Borrow & Return, Reports & Statistics).
  * Captures user input using `java.util.Scanner`.
  * Protects against runtime crashes by catching non-integer menu inputs (`NumberFormatException`).
  * Delegates business operations to the corresponding service classes.
  * Manages the application lifecycle: loads persistent data at launch and triggers data persistence on exit.

### 2.2 Service Layer (`com.library.service`)
* **Role:** Encapsulates all domain business rules, validations, and workflows.
* **Services:**
  * **`BookService`:** Manages the collection of books (`ArrayList<Book>`). Enforces unique `bookId` constraints, case-insensitive searches across title/author/category, updates, and conditional deletion (prevents deleting issued books).
  * **`StudentService`:** Manages the collection of students (`ArrayList<Student>`). Enforces case-insensitive `studentId` uniqueness, profile updates, and lookups.
  * **`LibraryService`:** Coordinates interactions between books, students, and loan transactions (`ArrayList<BorrowRecord>`). Enforces lending rules (e.g., student must exist, book must be available, student cannot duplicate an active loan on the same book, sets `LocalDate.now()`, flips availability flags).
  * **`ReportService`:** Calculates real-time system metrics (total books, available vs. issued counts, total members, active vs. returned loan counts) and formats student borrowing summaries.

### 2.3 Domain Model Layer (`com.library.model`)
* **Role:** Defines core business entities and their internal state invariants.
* **Entities:**
  * **`Book`:** Holds `bookId`, `title`, `author`, `category`, and `available` flag. Implements `Serializable`.
  * **`Student`:** Holds `studentId`, `name`, `email`, and `phone`. Implements `Serializable`.
  * **`BorrowRecord`:** Holds `recordId`, `bookId`, `studentId`, `borrowDate` (`LocalDate`), and nullable `returnDate` (`LocalDate`). Implements `Serializable`.

### 2.4 Persistence Layer (`com.library.util`)
* **Role:** Manages local, file-based data storage and restoration using Java Object Serialization.
* **Components:**
  * **`LibraryData`:** A serializable composite root object containing `ArrayList<Book>`, `ArrayList<Student>`, and `ArrayList<BorrowRecord>`.
  * **`FileManager`:** Handles `FileInputStream`/`FileOutputStream` and `ObjectInputStream`/`ObjectOutputStream` operations targeting `data/library.dat`. Automatically initializes missing parent directories and traps I/O or corruption errors gracefully.

---

## 3. Service Interactions & Data Flow

To maintain a **Single Source of Truth (SSOT)**, `ReportService` and `LibraryService` do not duplicate data collections:

```
                      +-------------------+
                      |    ReportService  |
                      +-------------------+
                       /        |        \
                      /         |         \
                     v          v          v
         +-------------+  +--------------+  +----------------+
         | BookService |  |StudentService|  | LibraryService |
         +-------------+  +--------------+  +----------------+
                |                |                  |
                v                v                  v
         ArrayList<Book>  ArrayList<Student>  ArrayList<BorrowRecord>
```

* When issuing a loan, `LibraryService` validates student existence against `StudentService`, verifies book availability against `BookService`, marks the book unavailable, and appends a new `BorrowRecord`.
* When generating statistical reports, `ReportService` queries `BookService`, `StudentService`, and `LibraryService` in real time, ensuring reports always match current memory state.

---

## 4. Local Persistence Mechanism

* **Unified Object Serialization:** Instead of writing individual entities in disparate files, `FileManager` persists the root `LibraryData` container object in a single serialization stream to `data/library.dat`.
* **State Restoration:** On application start, `FileManager.loadData()` deserializes the object graph. `BookService.setBooks()`, `StudentService.setStudents()`, and `LibraryService.setBorrowRecords()` inject the restored collections into the active services.
* **ID Synchronization:** `LibraryService.setBorrowRecords()` scans existing record IDs (e.g., `R001`, `R002`) and sets the internal counter to the next sequential number, preventing ID collisions on future transactions.
* **Graceful Degradation:** If `data/library.dat` is missing (first launch), default demo sample data is loaded. If the file is corrupted, a descriptive warning is output and demo data is loaded without crashing the program.
