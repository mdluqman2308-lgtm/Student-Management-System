package com.studentmanagement;

import com.studentmanagement.model.Mark;
import com.studentmanagement.model.Student;
import com.studentmanagement.model.StudentResult;
import com.studentmanagement.service.ResultService;
import com.studentmanagement.util.PasswordUtil;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PasswordAndResultTest {

    private static final String SALT = "c3a9f1e27b4d4e68";
    /** Same value MySQL stores for the sample admin in sql/student_management.sql. */
    private static final String ADMIN_HASH = "3ed63338036bba3e8d72e32f8b320c887f2ad223ce0f703043af21d36fc9110e";

    private final ResultService resultService = new ResultService();

    @Test
    void passwordHashMatchesTheSqlSeedData() {
        assertEquals(ADMIN_HASH, PasswordUtil.hash(SALT, "Admin@123"));
        assertTrue(PasswordUtil.matches("Admin@123", SALT, ADMIN_HASH));
        assertFalse(PasswordUtil.matches("wrong", SALT, ADMIN_HASH));
    }

    @Test
    void resultIsCalculatedFromMarks() {
        StudentResult result = resultService.calculate(student(1002, "Priya Reddy"),
                List.of(mark(82), mark(78), mark(85)));

        assertEquals(3, result.subjectCount());
        assertEquals(245.0, result.totalMarks());
        assertEquals(81.67, result.average());
        assertEquals(81.67, result.percentage());
        assertEquals("A", result.grade());
    }

    @Test
    void studentWithoutMarksHasNoGrade() {
        StudentResult result = resultService.calculate(student(1007, "Vikram Singh"), List.of());

        assertEquals(0, result.subjectCount());
        assertEquals("N/A", result.grade());
    }

    private Student student(int id, String name) {
        Student student = new Student();
        student.setStudentId(id);
        student.setName(name);
        return student;
    }

    private Mark mark(double value) {
        Mark mark = new Mark();
        mark.setMarks(value);
        return mark;
    }
}
