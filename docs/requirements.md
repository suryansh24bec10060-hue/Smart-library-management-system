# Requirements Specification: Smart Library Management System

This document outlines the functional and non-functional requirements implemented in the Smart Library Management System. Every item corresponds directly to tested, working capabilities within the codebase.

---

## 1. Functional Requirements (FR)

### FR1: Book Management
* **FR1.1:** The system shall allow registering new books with Book ID, Title, Author, and Category.
* **FR1.2:** The system shall enforce uniqueness of Book IDs (case-insensitive) and reject duplicate entries.
* **FR1.3:** The system shall list all registered books and display their current availability status.
* **FR1.4:** The system shall support case-insensitive searching across book title, author, or category.
* **FR1.5:** The system shall allow updating title, author, and category while preventing alteration of the Book ID.
* **FR1.6:** The system shall allow deleting an existing book only if the book is not currently issued on loan.

### FR2: Student Management
* **FR2.1:** The system shall allow registering students with Student ID, Name, Email, and Phone number.
* **FR2.2:** The system shall enforce case-insensitive uniqueness of Student IDs.
* **FR2.3:** The system shall display all registered students.
* **FR2.4:** The system shall allow case-insensitive searching by student name, email, or phone.
* **FR2.5:** The system shall allow updating a student's profile details while preventing modification of the Student ID.
* **FR2.6:** The system shall support deleting registered student records.

### FR3: Borrow Book
* **FR3.1:** The system shall issue a book only if the student exists, the book exists, and the book's availability is `true`.
* **FR3.2:** The system shall reject duplicate active loans of the same book by the same student.
* **FR3.3:** The system shall generate a unique sequential borrow record ID (e.g., `R001`, `R002`).
* **FR3.4:** The system shall set `borrowDate` to the current system date (`LocalDate.now()`) and initialize `returnDate` to `null`.
* **FR3.5:** The system shall mark the book's availability as `false` upon checkout.

### FR4: Return Book
* **FR4.1:** The system shall process a return only when an active borrow record (`returnDate == null`) exists for the specified student and book.
* **FR4.2:** The system shall set the borrow record's `returnDate` to the current date (`LocalDate.now()`).
* **FR4.3:** The system shall reset the book's availability status back to `true`.

### FR5: Borrowing History
* **FR5.1:** The system shall display all currently active borrowings (unresolved loans).
* **FR5.2:** The system shall display the complete borrowing history, including completed and active loans.
* **FR5.3:** The system shall filter and display all borrowing records belonging to a specific student ID.

### FR6: Reports & Statistics
* **FR6.1:** The system shall compute aggregate counts: Total Books, Available Books, Issued Books, Total Students, Total Borrowings, Active Borrowings, and Returned Borrowings.
* **FR6.2:** The system shall display a formatted Library Summary reflecting real-time system metrics.
* **FR6.3:** The system shall list all currently issued/unavailable books.
* **FR6.4:** The system shall generate an individualized borrowing summary for any valid student ID.

### FR7: Persistent Data Storage
* **FR7.1:** The system shall serialize application state (books, students, and borrow records) to a local file (`data/library.dat`) on exit.
* **FR7.2:** The system shall automatically restore previously saved state upon launch.
* **FR7.3:** The system shall automatically create the `data/` directory if it does not already exist.
* **FR7.4:** The system shall load default sample data if no data file exists on first run.

### FR8: Input Validation & Error Handling
* **FR8.1:** The system shall validate domain constraints (e.g., non-empty strings, email structure containing `@` and `.`).
* **FR8.2:** The system shall catch non-numeric inputs in CLI menus and prompt the user without crashing.
* **FR8.3:** The system shall catch corrupted or unreadable data files and gracefully fall back to sample data.

---

## 2. Non-Functional Requirements (NFR)

### NFR1: Usability
* The system shall provide an intuitive, numbered console menu hierarchy with clear breadcrumbs and dedicated "Back" options to return to the parent menu.

### NFR2: Reliability
* The system shall maintain internal data consistency at all times. A book marked as borrowed must correspond to an active loan record, and returning that book must reliably restore availability.

### NFR3: Maintainability
* The architecture shall strictly separate Presentation (`Main`), Business Logic (`*Service`), Domain Entities (`*Model`), and Persistence (`*Manager`). Modifications to one layer shall not force rewrites of another.

### NFR4: Performance
* All in-memory search, lookup, and reporting operations shall execute in sub-second time for hundreds of records using standard collection traversal.

### NFR5: Data Persistence
* The local data store shall utilize standard Java object serialization (`java.io.Serializable`) without requiring external database drivers or network dependencies.

### NFR6: Error Handling
* The system shall prevent raw exceptions (e.g., `NullPointerException`, `NumberFormatException`, `IOException`) from leaking to the console interface, ensuring user-friendly diagnostic messages instead.
