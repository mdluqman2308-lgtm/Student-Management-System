package com.studentmanagement.util;

/** The grading scheme: converts a percentage into a letter grade. */
public final class GradeUtil {

    public static final String NO_GRADE = "N/A";

    private GradeUtil() {
    }

    /**
     * 90 and above = A+, 80 = A, 70 = B, 60 = C, 50 = D, below 50 = F.
     * Each band includes its lower limit.
     */
    public static String calculateGrade(double percentage) {
        if (percentage >= 90) {
            return "A+";
        }
        if (percentage >= 80) {
            return "A";
        }
        if (percentage >= 70) {
            return "B";
        }
        if (percentage >= 60) {
            return "C";
        }
        if (percentage >= 50) {
            return "D";
        }
        return "F";
    }
}
