# Academic Project Problem Statement: Smart Library Management System

## Course Information
* **Course Code:** CSE2006
* **Course Title:** Programming in Java
* **Academic Institution:** VIT Bhopal University (VITyarthi)
* **Student Name:** Kedarnath Hotta
* **Project Name:** Smart Library Management System

---

## 1. Problem Statement
Traditional paper-based or uncoordinated electronic record systems in educational and institutional libraries encounter significant operational challenges:
* Manual record-keeping leads to clerical inaccuracies, misplaced inventory, and untracked loans.
* Inability to rapidly look up book availability or search by title, author, or category.
* Difficulties in monitoring member borrowing activity and enforcing business rules (e.g., preventing checkout of already loaned titles or duplicate active loans).
* Lack of consolidated, real-time reporting regarding inventory utilization, active loans, and member history.

The goal of this project is to develop a standalone, command-line driven **Smart Library Management System** using **Java 17** that enforces strict Object-Oriented Programming (OOP) principles, guarantees robust data validation, manages in-memory collections efficiently, provides real-time reporting, and ensures local session persistence via built-in Java serialization.

---

## 2. Scope
The scope of this project encompasses:
* **Catalog Management:** Full lifecycle management of books (registration, search, update, safe deletion, and availability tracking).
* **Membership Management:** Registration, search, update, and lookup of students/library members.
* **Circulation Transactions:** Safe borrowing and returning workflows enforcing domain rules.
* **Audit & History:** Full tracking of active loans and completed return history.
* **Analytics & Reports:** Aggregated metrics and individualized student borrowing profiles.
* **Local Persistence:** Automatic serialization and deserialization of application state to `data/library.dat`.
* **Automated Unit Testing:** Full JUnit 5 test suite verifying service business logic.

*Out of Scope:* External SQL database servers (MySQL, PostgreSQL), graphical UI (JavaFX/Swing), remote web APIs, or network client-server communications.

---

## 3. Target Users
* **Librarians & Library Staff:** Primary operators managing book inventories, registering student members, issuing book loans, and processing returns.
* **Library Administrators:** Staff members monitoring operational statistics, inventory utilization, and member borrowing patterns.
* **Academic Evaluators:** Course instructors and evaluators reviewing Java language proficiency, OOP architecture, and automated test coverage.

---

## 4. High-Level Features
1. **Book Management Module:**
   * Add books with unique IDs, title, author, and category.
   * Case-insensitive search across title, author, and category.
   * Update book metadata while keeping Book ID immutable.
   * Delete available books while preventing deletion of currently issued books.
   * View all books and filter for available books.
2. **Student Management Module:**
   * Register students with unique IDs, name, email, and phone.
   * Case-insensitive multi-field search and profile updates.
   * Remove student memberships.
3. **Borrow & Return Management Module:**
   * Issue books with availability checks, student existence checks, and duplicate loan prevention.
   * Return books with automatic status flipping and return date logging using `LocalDate`.
   * View active loans and complete borrowing history.
4. **Reports & Statistics Module:**
   * Real-time library summary (Total Books, Available, Issued, Total Students, Active/Returned Borrowings).
   * List of currently issued titles.
   * Detailed borrowing summary for individual students.
5. **Local Data Persistence:**
   * Automatic saving to `data/library.dat` upon exit.
   * Automatic restoration on startup, with fallback to sample data if no save exists.
6. **Robust CLI User Experience:**
   * Numbered hierarchical submenus with input validation shielding against crashes.

---

## 5. Expected Outcome
* A production-ready, fully functional Java application demonstrating core software engineering principles:
  * Strict encapsulation, class modularity, and separation of concerns.
  * 100% automated test pass rate (33/33 JUnit 5 unit tests passing).
  * Reliable data persistence without external database engines.
  * Clear, user-friendly terminal interface suitable for academic evaluation.
