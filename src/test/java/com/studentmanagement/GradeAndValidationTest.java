package com.studentmanagement;

import com.studentmanagement.util.GradeUtil;
import com.studentmanagement.util.ValidationUtil;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GradeAndValidationTest {

    @Test
    void gradeBoundariesFollowTheGradingScheme() {
        assertEquals("A+", GradeUtil.calculateGrade(100));
        assertEquals("A+", GradeUtil.calculateGrade(90));
        assertEquals("A", GradeUtil.calculateGrade(89.99));
        assertEquals("A", GradeUtil.calculateGrade(80));
        assertEquals("B", GradeUtil.calculateGrade(70));
        assertEquals("C", GradeUtil.calculateGrade(60));
        assertEquals("D", GradeUtil.calculateGrade(50));
        assertEquals("F", GradeUtil.calculateGrade(49.99));
        assertEquals("F", GradeUtil.calculateGrade(0));
    }

    @Test
    void emailValidation() {
        assertTrue(ValidationUtil.isValidEmail("aarav.sharma@example.com"));
        assertFalse(ValidationUtil.isValidEmail("aarav@"));
        assertFalse(ValidationUtil.isValidEmail("aarav.example.com"));
        assertFalse(ValidationUtil.isValidEmail(""));
    }

    @Test
    void phoneValidation() {
        assertTrue(ValidationUtil.isValidPhone("9876543210"));
        assertFalse(ValidationUtil.isValidPhone("98765"));
        assertFalse(ValidationUtil.isValidPhone("1234567890"));
        assertFalse(ValidationUtil.isValidPhone("98765abcde"));
    }

    @Test
    void marksValidation() {
        assertTrue(ValidationUtil.isValidMarks(0));
        assertTrue(ValidationUtil.isValidMarks(100));
        assertFalse(ValidationUtil.isValidMarks(-1));
        assertFalse(ValidationUtil.isValidMarks(100.5));
        assertTrue(ValidationUtil.parseNumber("78.5").isPresent());
        assertTrue(ValidationUtil.parseNumber("abc").isEmpty());
        assertTrue(ValidationUtil.parseNumber("NaN").isEmpty());
    }

    @Test
    void yearAndDateValidation() {
        assertTrue(ValidationUtil.isValidYear(1));
        assertTrue(ValidationUtil.isValidYear(4));
        assertFalse(ValidationUtil.isValidYear(0));
        assertFalse(ValidationUtil.isValidYear(5));
        assertTrue(ValidationUtil.isValidDateOfBirth(LocalDate.of(2004, 3, 15)));
        assertFalse(ValidationUtil.isValidDateOfBirth(LocalDate.now().plusDays(1)));
        assertTrue(ValidationUtil.parseDate("2004-02-30").isEmpty());
    }

    @Test
    void studentIdParsing() {
        assertEquals(1001, ValidationUtil.parsePositiveInt(" 1001 ").orElseThrow());
        assertTrue(ValidationUtil.parsePositiveInt("abc").isEmpty());
        assertTrue(ValidationUtil.parsePositiveInt("-5").isEmpty());
    }
}
