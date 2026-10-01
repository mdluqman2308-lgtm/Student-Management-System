package com.studentmanagement.model;

/** The student attributes the administrator can search by. */
public enum SearchField {
    STUDENT_ID("Student ID"),
    NAME("Name"),
    DEPARTMENT("Department"),
    COURSE("Course");

    private final String label;

    SearchField(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}
