package com.library.service;

import com.library.model.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for StudentService operations and business rules.
 */
public class StudentServiceTest {
    private StudentService studentService;

    @BeforeEach
    public void setUp() {
        studentService = new StudentService();
        studentService.addStudent(new Student("S101", "Alice Johnson", "alice@university.edu", "9876543210"));
        studentService.addStudent(new Student("S102", "Bob Smith", "bob@university.edu", "8765432109"));
        studentService.addStudent(new Student("S103", "Charlie Davis", "charlie@university.edu", "7654321098"));
    }

    @Test
    public void testAddStudentSuccess() {
        Student newStudent = new Student("S104", "David Warner", "david@university.edu", "6543210987");
        boolean result = studentService.addStudent(newStudent);
        assertTrue(result, "Student should be added successfully");
        assertNotNull(studentService.findStudentById("S104"));
        assertEquals(4, studentService.getAllStudents().size());
    }

    @Test
    public void testAddStudentDuplicateIdRejected() {
        Student duplicateStudent = new Student("S101", "Different Name", "diff@university.edu", "1122334455");
        boolean result = studentService.addStudent(duplicateStudent);
        assertFalse(result, "Duplicate student ID should be rejected");
        assertEquals(3, studentService.getAllStudents().size());
    }

    @Test
    public void testFindStudentById() {
        Student student = studentService.findStudentById("S102");
        assertNotNull(student, "Student should be found by exact ID");
        assertEquals("Bob Smith", student.getName());
    }

    @Test
    public void testFindStudentByIdCaseInsensitive() {
        Student studentLower = studentService.findStudentById("s101");
        Student studentUpper = studentService.findStudentById("S101");
        assertNotNull(studentLower, "Student lookup should be case-insensitive");
        assertEquals(studentUpper, studentLower);
    }

    @Test
    public void testSearchByName() {
        ArrayList<Student> results = studentService.searchStudents("Alice");
        assertEquals(1, results.size());
        assertEquals("S101", results.get(0).getStudentId());
    }

    @Test
    public void testSearchByEmail() {
        ArrayList<Student> results = studentService.searchStudents("bob@university.edu");
        assertEquals(1, results.size());
        assertEquals("Bob Smith", results.get(0).getName());
    }

    @Test
    public void testSearchByPhone() {
        ArrayList<Student> results = studentService.searchStudents("7654321098");
        assertEquals(1, results.size());
        assertEquals("Charlie Davis", results.get(0).getName());
    }

    @Test
    public void testUpdateStudent() {
        boolean updated = studentService.updateStudent("S101", "Alice Williams", "alice.williams@university.edu", "9998887776");
        assertTrue(updated, "Student details should be updated successfully");

        Student student = studentService.findStudentById("S101");
        assertNotNull(student);
        assertEquals("Alice Williams", student.getName());
        assertEquals("alice.williams@university.edu", student.getEmail());
        assertEquals("9998887776", student.getPhone());
        assertEquals("S101", student.getStudentId(), "Student ID must not change");
    }

    @Test
    public void testDeleteStudent() {
        boolean deleted = studentService.deleteStudent("S102");
        assertTrue(deleted, "Existing student should be deleted successfully");
        assertNull(studentService.findStudentById("S102"), "Deleted student should no longer exist");
        assertEquals(2, studentService.getAllStudents().size());
    }

    @Test
    public void testHandleNonExistentStudent() {
        assertNull(studentService.findStudentById("S999"), "Lookup for non-existent student should return null");
        assertFalse(studentService.deleteStudent("S999"), "Deleting non-existent student should return false");
        assertFalse(studentService.updateStudent("S999", "Ghost", "ghost@test.com", "1234567890"),
                "Updating non-existent student should return false");
    }
}
