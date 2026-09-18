# Design Decisions: Smart Library Management System

This document captures the key architectural and technological design decisions made during the development of the Smart Library Management System for the CSE2006 Programming in Java course.

---

## 1. Why Java?
* **Course Alignment & Portability:** Java is the core language for CSE2006, offering write-once-run-anywhere (WORA) portability across operating systems.
* **Strong Type Safety:** Compile-time checking catches type mismatches and interface discrepancies early, reducing runtime defects.
* **Rich Standard Library:** Built-in packages for collections (`java.util`), date/time (`java.time`), and serialization (`java.io`) eliminate the need for heavy third-party libraries.

---

## 2. Why Object-Oriented Programming (OOP)?
* **Encapsulation:** Protects internal data invariants by keeping entity fields private and exposing controlled public accessors/mutators with validation.
* **Modularity:** Encapsulating domain concepts (`Book`, `Student`, `BorrowRecord`) as distinct classes makes the codebase clean, readable, and easy to extend.
* **Maintainability:** Clear boundaries between classes ensure that changes to entity representation do not ripple across the entire system.

---

## 3. Why `ArrayList` / Java Collections?
* **Dynamic Sizing:** Unlike primitive fixed-size arrays (`Book[]`), an `ArrayList` dynamically expands and shrinks as books, students, and borrow records are added or removed.
* **Type Safety via Generics:** Parameterizing collections (`ArrayList<Book>`, `ArrayList<Student>`, `ArrayList<BorrowRecord>`) guarantees compile-time type safety without casting.
* **Academic Appropriateness:** Teaches core traversal, search algorithms, and collection manipulation using standard Java loops without masking logic behind complex frameworks.

---

## 4. Why a Service-Layer Architecture?
* **Separation of Concerns (SoC):** Separating the user interface (`Main.java`) from business logic (`BookService`, `StudentService`, `LibraryService`) ensures each layer has a single responsibility.
* **Independent Testability:** Business methods can be tested directly with JUnit 5 without simulating terminal user input or parsing console strings.
* **Future Adaptability:** If a GUI (JavaFX) or Web API is added in the future, the entire service layer remains reusable without rewriting business rules.

---

## 5. Why `java.time.LocalDate`?
* **Modern & Immutable:** Replaced the legacy `java.util.Date` and `Calendar` APIs with thread-safe, immutable calendar representations.
* **Semantic Accuracy:** A library loan relies on calendar dates (day, month, year) rather than exact timestamps with timezones. `LocalDate` models this domain requirement precisely.
* **Built-in Comparison & Math:** Enables straightforward date arithmetic (`.isBefore()`, `.isAfter()`, `ChronoUnit.DAYS.between()`) for calculating loan durations and overdue periods.

---

## 6. Why Store IDs in `BorrowRecord` (Rather Than Entire Objects)?
* **Decoupling (Loose Coupling):** Avoids deep reference graphs and circular dependencies between books, students, and transaction records.
* **Preventing Stale Data:** If a student updates their phone number or email, or a book's title is modified, the loan record references the current entity via its unique ID rather than holding a frozen, outdated object clone.
* **Serialization Simplicity:** Storing scalar IDs prevents serialization bloat and complex object graph cycles when saving state to disk.

---

## 7. Why Local File Serialization?
* **Zero External Dependencies:** Built directly into Java (`java.io.Serializable`, `ObjectOutputStream`, `ObjectInputStream`), avoiding the overhead of external SQL engines or third-party drivers.
* **Self-Contained Deployment:** The application runs out of the box on any machine with a JDK, storing its data locally in `data/library.dat`.
* **Unified State Persistence:** Encapsulating collections inside a single `LibraryData` root container ensures the entire system state is written and read as one consistent snapshot.

---

## 8. Why a Command-Line Interface (CLI)?
* **Focus on Core Language Competencies:** Aligns with CSE2006 academic objectives by emphasizing algorithms, data structures, and OOP design rather than GUI layout boilerplate.
* **Lightweight & Universal:** Runs identically in PowerShell, Linux terminals, and macOS terminals without display server dependencies or window manager glitches.

---

## 9. Why JUnit 5 Automated Testing Was Added?
* **Regression Prevention:** Ensures that adding new features (such as persistence or reporting) does not silently break core borrow, return, or deletion rules.
* **Verifiable Correctness:** Provides 33 automated tests that execute in milliseconds to prove system reliability beyond manual visual inspection.
