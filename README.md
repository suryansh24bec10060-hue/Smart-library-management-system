# Smart Library Management System

A robust, modular, command-line based Library Management System developed for **VITyarthi CSE2006: Programming in Java**.

---

## 1. Project Title
**Smart Library Management System**

---

## 2. Project Overview
The **Smart Library Management System** is a standalone, terminal-driven Java application developed to automate essential library operations in an academic setting. Built strictly in pure Java 17 using core Object-Oriented Programming (OOP) paradigms and Java Collections, the system provides catalog indexing, member management, loan/return transaction tracking, real-time reporting, local file persistence, and an automated JUnit 5 test suite.

---

## 3. Problem Being Solved
Traditional, manual library tracking methods suffer from:
* Misplaced records and clerical data entry errors.
* Inability to verify real-time book availability instantly.
* Risk of double-lending books or checking out already issued inventory.
* Lack of unified reporting on active borrowings, member loan histories, and catalog statistics.
* Data loss between application restarts when relying purely on volatile memory.

This application provides a reliable, self-contained solution that guarantees inventory consistency, shields against invalid inputs, and persists state across sessions without requiring external database installations.

---

## 4. Objectives
* **Object-Oriented Excellence:** Enforce encapsulation, abstraction, and modularity across domain models and service layers.
* **Separation of Concerns:** Keep presentation, business logic, data models, and persistence completely decoupled.
* **Data Integrity:** Prevent duplicate book and student IDs, disallow deletion of checked-out books, and enforce valid loan lifecycles.
* **Comprehensive Automated Testing:** Guarantee regression-free behavior via 33 automated JUnit 5 unit tests.
* **Zero External Dependencies:** Use built-in Java serialization and core libraries for a self-contained, portable runtime.

---

## 5. Key Features
* **Interactive CLI Menu:** Hierarchical console navigation with input validation that prevents crashes from invalid numbers or strings.
* **Case-Insensitive Search:** Search books by title, author, or category; search students by name, email, or phone.
* **Availability Tracking:** Dynamic updates of book checkout status (`available = true/false`).
* **Safe Book Deletion:** Prevents deletion of books currently checked out to members.
* **Date Handling:** Uses modern, immutable `java.time.LocalDate` for recording borrowing and return timestamps.
* **Real-time Analytics:** Instant library summary metrics and individualized student borrowing profiles.
* **Automatic Local Persistence:** State is saved to `data/library.dat` upon exit and automatically restored on launch.

---

## 6. Functional Modules
1. **Book Management Module:** Registration, listing, case-insensitive search, detail updates, conditional deletion, and availability filtering.
2. **Student Management Module:** Student registration, listing, search across name/email/phone, profile updates, and deletion.
3. **Borrow & Return Management Module:** Book checkout, book return, active borrowing inspection, complete transaction history, and student-specific loan records.
4. **Reports & Statistics Module:** Comprehensive inventory and member metrics summary, list of currently checked-out titles, and student borrowing history summaries.
5. **Persistence Module:** Local object serialization and deserialization via `FileManager`.

---

## 7. Non-Functional Requirements
* **Usability:** Clean, numbered console menus with clear user prompts and feedback messages.
* **Reliability:** Invariant enforcement ensures book availability states always match active borrowing records.
* **Maintainability:** Multi-tiered architecture makes classes modular and straightforward to refactor or extend.
* **Performance:** In-memory collection traversal executes in sub-millisecond time.
* **Portability:** Compatible with Java 17 LTS and newer (tested on Java 26) across Windows, Linux, and macOS.
* **Error Resilience:** Graceful handling of invalid user inputs, non-existent entity IDs, and corrupted data files without dumping stack traces.

---

## 8. Technology Stack
* **Language:** Java 17 LTS (Java SE 17 bytecode compatibility via `--release 17`)
* **Runtime Tested:** OpenJDK / Oracle JDK 26.0.1
* **Build Tool:** Apache Maven (configured in `pom.xml`)
* **Testing Framework:** JUnit 5 (Jupiter API & Engine `5.10.2`)
* **Test Runner:** JUnit Platform Console Standalone Launcher `1.10.2` (project-local)
* **Storage:** Java Native Object Serialization (`java.io.Serializable`)

---

## 9. Project Architecture
The application adheres to a clean **Layered Architecture**:

```
Presentation (Main.java)
       │
       ▼
Service Layer (BookService, StudentService, LibraryService, ReportService)
       │
       ▼
Domain Models (Book, Student, BorrowRecord)
       │
       ▼
Persistence Layer (FileManager <-> LibraryData <-> data/library.dat)
```

* For complete architecture details, see [`docs/architecture.md`](docs/architecture.md).  
* For UML use case, class, and sequence diagrams, see [`docs/uml.md`](docs/uml.md).
* For detailed requirements, see [`docs/requirements.md`](docs/requirements.md).
* For engineering rationale, see [`docs/design-decisions.md`](docs/design-decisions.md).
* For full testing documentation, see [`docs/testing.md`](docs/testing.md).

---

## 10. Folder Structure

```text
SmartLibraryManagement/
├── pom.xml                                 # Maven configuration
├── README.md                               # Comprehensive project documentation
├── statement.md                            # Academic problem statement and objectives
├── .gitignore                              # Git ignore rules
├── docs/                                   # Detailed system documentation
│   ├── architecture.md                     # Layered architecture description
│   ├── design-decisions.md                 # Engineering rationale
│   ├── requirements.md                     # Functional & non-functional requirements
│   ├── testing.md                          # Testing approach & results
│   └── uml.md                              # Mermaid UML diagrams
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/
│   │           └── library/
│   │               ├── Main.java           # CLI Entry Point
│   │               ├── model/              # Domain Entities (Serializable)
│   │               │   ├── Book.java
│   │               │   ├── Student.java
│   │               │   └── BorrowRecord.java
│   │               ├── service/            # Business & Reporting Logic
│   │               │   ├── BookService.java
│   │               │   ├── StudentService.java
│   │               │   ├── LibraryService.java
│   │               │   └── ReportService.java
│   │               └── util/               # File Persistence
│   │                   ├── FileManager.java
│   │                   └── LibraryData.java
│   └── test/
│       └── java/
│           └── com/
│               └── library/
│                   └── service/            # JUnit 5 Automated Unit Tests
│                       ├── BookServiceTest.java
│                       ├── StudentServiceTest.java
│                       └── LibraryServiceTest.java
```

---

## 11. How to Compile and Run

### Option A: Using Standard Java Commands (No Maven Required)

1. **Compile Production Code (targeting Java 17):**
   ```powershell
   javac --release 17 -d target/classes (Get-ChildItem -Recurse -Filter "*.java" src/main | Select-Object -ExpandProperty FullName)
   ```

2. **Run the Application:**
   ```powershell
   java -cp target/classes com.library.Main
   ```

### Option B: Using Maven (If Maven is installed)

1. **Compile:**
   ```bash
   mvn clean compile
   ```
2. **Package & Run:**
   ```bash
   mvn clean package
   java -jar target/smart-library-management-1.0-SNAPSHOT.jar
   ```

---

## 12. How to Run JUnit Tests

### Primary Testing Method: Maven (Standard)
The project includes a fully configured `pom.xml` with JUnit 5 Jupiter and Maven Surefire. To compile and run all automated unit tests:
```bash
mvn test
```
Maven will automatically download the required JUnit 5 dependencies, compile all production and test sources, and execute the entire test suite.

### Alternative Testing Method: Using Standalone JUnit 5 Console Launcher
In environments where Maven is not installed globally, tests can be executed directly using the standalone JUnit 5 Console Launcher JAR:
1. Download `junit-platform-console-standalone-1.10.2.jar` (or place a copy in a local directory such as `test-tools/`).
2. Compile the test classes against the production classes and the launcher JAR:
   ```powershell
   javac --release 17 -cp "target/classes;test-tools/junit-platform-console-standalone-1.10.2.jar" -d target/test-classes (Get-ChildItem -Recurse -Filter "*.java" src/test | Select-Object -ExpandProperty FullName)
   ```
3. Execute the tests:
   ```powershell
   java -jar test-tools/junit-platform-console-standalone-1.10.2.jar execute --class-path "target/classes;target/test-classes" --scan-class-path
   ```
*(Note: Binary dependencies like the standalone test runner JAR are excluded from the source distribution archive to maintain a clean, lightweight repository).*

---

## 13. Test Results

* **Total Tests Found:** 33
* **Tests Started:** 33
* **Tests Successful:** **33**
* **Tests Failed:** **0**
* **Tests Skipped:** **0**
* **Pass Rate:** **100%**

Test suites include:
* `BookServiceTest`: 11 tests (addition, duplicate check, lookup, update, safe delete, category/author/title search, availability filtering).
* `StudentServiceTest`: 10 tests (registration, duplicate rejection, lookup, name/email/phone search, profile updates, deletion).
* `LibraryServiceTest`: 12 tests (loan creation, availability flipping, duplicate loan prevention, return processing, active vs. history tracking, ID sequencing).

For full testing documentation, see [`docs/testing.md`](docs/testing.md).

---

## 14. Persistence & File Storage Explanation
* **File Location:** Serialized data is stored locally at `data/library.dat`.
* **Object Serialization:** `FileManager` writes the composite `LibraryData` root object to disk on exit (Option 5), saving all books, students, and borrow records in a unified binary serialization stream using Java's built-in `ObjectOutputStream`.
* **Automatic Creation & Safe Fallback:** The `data/` directory is created automatically (`mkdirs()`). If `library.dat` is missing or corrupted, the system falls back to sample demo data without crashing.
* **How to Reset Data:** Delete `data/library.dat` (or the `data/` folder). Upon next launch, the system re-seeds with default demo data.

---

## 15. Sample Application Workflow
1. Launch application: Default sample books and students are loaded.
2. Select `1. Book Management` -> `1. Add Book`: Register book `B105` ("Refactoring", "Martin Fowler", "Software Engineering").
3. Select `2. Student Management` -> `1. Add Student`: Register student `S104` ("David Warner", "david@university.edu", "9876543210").
4. Select `3. Borrow / Return Management` -> `1. Borrow Book`: Check out `B105` to student `S104`. Book becomes unavailable; loan `R003` is created.
5. Select `4. Reports & Statistics` -> `1. Library Summary`: Verify Total Books = 5, Issued Books = 2, Active Borrowings = 2.
6. Select `3. Borrow / Return Management` -> `2. Return Book`: Return `B105` by `S104`. Book becomes available again.
7. Select `5. Exit`: System saves all data to `data/library.dat` and closes cleanly.
8. Re-launch: All newly added books, students, and borrowing history are restored seamlessly.

---

## 16. Future Enhancements
* **Fine Calculation Engine:** Automated penalty assessment for overdue books returned past a specified loan duration.
* **Role-Based Authentication:** Login separation for Librarians (administrative privileges) and Students (read-only / self-service borrowing).
* **Relational Database Backend:** Migration from file serialization to JDBC/PostgreSQL for enterprise scalability.
* **Graphical User Interface (GUI):** Desktop interface using JavaFX or a lightweight Web dashboard.

---

## 17. Author & Project Information
* **Course:** CSE2006 — Programming in Java
* **Curriculum:** VITyarthi / Vellore Institute of Technology (VIT)
* **Project Type:** Build Your Own Project (BYOP) Academic Capstone
* **Student Name:** Suryansh Rao
* **University:** VIT Bhopal University
* **Status:** Complete & Fully Submission-Ready
