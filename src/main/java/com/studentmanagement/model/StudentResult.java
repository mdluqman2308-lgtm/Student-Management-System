package com.studentmanagement.model;

/** Calculated academic result of one student (not stored in the database). */
public record StudentResult(
        int studentId,
        String name,
        String department,
        String course,
        int subjectCount,
        double totalMarks,
        double average,
        double percentage,
        String grade) {
}
