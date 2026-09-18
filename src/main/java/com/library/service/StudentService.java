package com.library.service;

import com.library.model.Student;

import java.util.ArrayList;

/**
 * Service class responsible for managing registered students.
 * Uses an ArrayList to store and manage Student objects.
 */
public class StudentService {
    // Internal collection of students
    private final ArrayList<Student> students;

    public StudentService() {
        this.students = new ArrayList<>();
    }

    /**
     * Adds a new student to the system.
     * Enforces case-insensitive uniqueness of student IDs.
     *
     * @param student The student to add.
     * @return true if added successfully, false if duplicate ID or null.
     */
    public boolean addStudent(Student student) {
        if (student == null) {
            return false;
        }
        if (findStudentById(student.getStudentId()) != null) {
            return false; // Duplicate student ID found
        }
        students.add(student);
        return true;
    }

    /**
     * Returns a copy of all registered students to protect internal state.
     *
     * @return A new ArrayList containing all students.
     */
    public ArrayList<Student> getAllStudents() {
        return new ArrayList<>(students);
    }

    /**
     * Replaces internal student collection with loaded students from persistence.
     *
     * @param loadedStudents Restored students list.
     */
    public void setStudents(ArrayList<Student> loadedStudents) {
        this.students.clear();
        if (loadedStudents != null) {
            this.students.addAll(loadedStudents);
        }
    }

    /**
     * Finds a student by their unique ID (case-insensitive).
     *
     * @param studentId The student ID to search for.
     * @return The matching Student object, or null if not found.
     */
    public Student findStudentById(String studentId) {
        if (studentId == null || studentId.trim().isEmpty()) {
            return null;
        }
        for (Student s : students) {
            if (s.getStudentId().equalsIgnoreCase(studentId.trim())) {
                return s;
            }
        }
        return null;
    }

    /**
     * Searches for students matching the keyword in name, email, or phone.
     * Search is case-insensitive.
     *
     * @param keyword The search term.
     * @return A list of matching students.
     */
    public ArrayList<Student> searchStudents(String keyword) {
        ArrayList<Student> matchingStudents = new ArrayList<>();
        if (keyword == null || keyword.trim().isEmpty()) {
            return matchingStudents;
        }

        String searchKeyword = keyword.trim().toLowerCase();
        for (Student s : students) {
            boolean matchesName = s.getName().toLowerCase().contains(searchKeyword);
            boolean matchesEmail = s.getEmail().toLowerCase().contains(searchKeyword);
            boolean matchesPhone = s.getPhone().toLowerCase().contains(searchKeyword);

            if (matchesName || matchesEmail || matchesPhone) {
                matchingStudents.add(s);
            }
        }
        return matchingStudents;
    }

    /**
     * Updates an existing student's name, email, and phone.
     * Does not allow changing the student ID.
     *
     * @param studentId The ID of the student to update.
     * @param name The new name.
     * @param email The new email.
     * @param phone The new phone number.
     * @return true if updated successfully, false if student not found or invalid input.
     */
    public boolean updateStudent(String studentId, String name, String email, String phone) {
        Student student = findStudentById(studentId);
        if (student == null) {
            return false;
        }
        try {
            student.setName(name);
            student.setEmail(email);
            student.setPhone(phone);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Deletes a student from the system.
     *
     * @param studentId The ID of the student to delete.
     * @return true if deleted successfully, false if student does not exist.
     */
    public boolean deleteStudent(String studentId) {
        Student student = findStudentById(studentId);
        if (student == null) {
            return false;
        }
        return students.remove(student);
    }
}
