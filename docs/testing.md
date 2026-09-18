# Testing Documentation: Smart Library Management System

## 1. Testing Strategy & Approach

The verification strategy combines two rigorous levels of testing:
1. **Automated Unit Testing (JUnit 5):** Validates all isolated business logic, invariants, edge cases, and state transitions across domain service classes without reliance on standard input/output streams.
2. **End-to-End Manual CLI Verification:** Verifies the presentation layer, hierarchical navigation loops, exception shielding, and local file serialization lifecycle.

---

## 2. Automated Unit Testing with JUnit 5

Automated testing is built using the **JUnit 5 Jupiter** framework. Test isolation is strictly enforced: each test case executes on a freshly initialized fixture instantiated inside a `@BeforeEach` setup block. No test reads from or mutates the real `data/library.dat` application storage.

### 2.1 Test Suites & Coverage

#### 1. `BookServiceTest` (`src/test/java/com/library/service/BookServiceTest.java`) — 11 Tests
* `testAddBookSuccess()`: Validates adding a new book and checking list expansion.
* `testAddBookDuplicateIdRejected()`: Asserts that duplicate book IDs are rejected without modifying the list.
* `testFindBookById()`: Confirms exact ID search retrieval.
* `testFindBookByIdCaseInsensitive()`: Asserts case-insensitive ID matching (`b101` == `B101`).
* `testSearchByTitle()`: Verifies case-insensitive substring matching against book titles.
* `testSearchByAuthor()`: Verifies case-insensitive author lookups.
* `testSearchByCategory()`: Verifies category matching.
* `testUpdateBook()`: Ensures mutable fields (title, author, category) update while `bookId` remains immutable.
* `testDeleteAvailableBook()`: Verifies deletion of an unissued book.
* `testDeleteIssuedBookRejected()`: Enforces the business rule that currently issued/borrowed books cannot be deleted.
* `testGetAvailableBooks()`: Confirms that only books with `available == true` are returned.

#### 2. `StudentServiceTest` (`src/test/java/com/library/service/StudentServiceTest.java`) — 10 Tests
* `testAddStudentSuccess()`: Tests normal registration of a new student.
* `testAddStudentDuplicateIdRejected()`: Prevents duplicate student registrations.
* `testFindStudentById()`: Confirms exact ID retrieval.
* `testFindStudentByIdCaseInsensitive()`: Asserts case-insensitive student ID matching (`s101` == `S101`).
* `testSearchByName()`: Verifies substring search by student name.
* `testSearchByEmail()`: Verifies search by email address.
* `testSearchByPhone()`: Verifies search by phone number.
* `testUpdateStudent()`: Verifies updating name, email, and phone while maintaining immutable `studentId`.
* `testDeleteStudent()`: Asserts deletion of an existing student.
* `testHandleNonExistentStudent()`: Ensures lookups, updates, and deletes of non-existent IDs return `null` or `false` gracefully without throwing unhandled exceptions.

#### 3. `LibraryServiceTest` (`src/test/java/com/library/service/LibraryServiceTest.java`) — 12 Tests
* `testBorrowBookSuccess()`: Tests successful book loan by an eligible student.
* `testBorrowingMakesBookUnavailable()`: Asserts that borrowing flips the book's `available` attribute to `false`.
* `testBorrowUnavailableBookRejected()`: Rejects loan requests for books currently checked out.
* `testBorrowNonExistentStudentRejected()`: Rejects loans when student ID is not registered.
* `testBorrowNonExistentBookRejected()`: Rejects loans when book ID is not found.
* `testReturnActiveBorrowing()`: Confirms normal return process completes successfully.
* `testReturnMakesBookAvailableAgain()`: Asserts that returning a book flips `available` back to `true`.
* `testReturnBookNoActiveBorrowingRejected()`: Prevents returning a book that has no active loan record.
* `testActiveBorrowingListContainsActiveRecords()`: Verifies `getActiveBorrowings()` only returns records with `returnDate == null`.
* `testReturnedBorrowingAppearsInHistory()`: Confirms returned books remain in historical logs with `returnDate` set.
* `testStudentBorrowingFiltering()`: Validates filtering of loan records by specific student ID.
* `testBorrowRecordIdsGeneratedCorrectly()`: Confirms sequential formatting of transaction IDs (`R001`, `R002`).

---

## 3. Automated Test Execution Results

The unit test suite was executed locally using the portable **JUnit 5 Standalone Console Launcher** (`junit-platform-console-standalone-1.10.2.jar`):

```text
[         6 containers found      ]
[         0 containers skipped    ]
[         6 containers started    ]
[         0 containers aborted    ]
[         6 containers successful ]
[         0 containers failed     ]
[        33 tests found           ]
[         0 tests skipped         ]
[        33 tests started         ]
[         0 tests aborted         ]
[        33 tests successful      ]
[         0 tests failed          ]
```

### Metrics Summary:
* **Total Tests Found:** 33
* **Total Tests Started:** 33
* **Total Tests Successful:** 33
* **Failed Tests:** 0
* **Skipped Tests:** 0
* **Pass Rate:** 100%

---

## 4. Manual Application Verification

Manual CLI tests were conducted to verify user interactions and persistence lifecycles:

| Verification Case | Action | Observed Result | Status |
| :--- | :--- | :--- | :---: |
| **Initial Boot without Storage** | Deleted `data/library.dat` and launched | App detected missing file, populated sample demo data without crashing | **Passed** |
| **Persistence on Exit** | Mutated state (added book `B200`, student `S200`, loaned `B200`), exited | Automatically created `data/` and saved state to `library.dat` | **Passed** |
| **State Reload on Restart** | Restarted application | Successfully loaded saved state; `B200` remained unavailable, `S200` existed, active loan `R003` intact | **Passed** |
| **Return and Re-availability** | Returned `B200`, exited, restarted | `B200` re-appeared in Available Books; loan moved to completed history | **Passed** |
| **Safe Deletion** | Deleted available book, exited, restarted | Deleted book did not return | **Passed** |
| **Corrupted File Shielding** | Overwrote `library.dat` with invalid text bytes | Caught `IOException`, displayed warning, safely fell back to demo data without crashing | **Passed** |
| **CLI Input Guarding** | Entered non-numeric input (`abc`, `-5`, `99`) in menus | Caught `NumberFormatException` and re-prompted cleanly | **Passed** |
