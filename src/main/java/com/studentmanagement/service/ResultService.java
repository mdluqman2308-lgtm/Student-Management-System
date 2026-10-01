package com.studentmanagement.service;

import com.studentmanagement.dao.MarkDAO;
import com.studentmanagement.dao.StudentDAO;
import com.studentmanagement.model.Mark;
import com.studentmanagement.model.Student;
import com.studentmanagement.model.StudentResult;
import com.studentmanagement.util.GradeUtil;
import com.studentmanagement.util.ValidationUtil;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Calculates total, average, percentage and grade.
 * Every subject is out of 100, so percentage = total / (subjects x 100) x 100.
 */
public class ResultService {

    private final StudentDAO studentDAO = new StudentDAO();
    private final MarkDAO markDAO = new MarkDAO();

    /** Pure calculation: no database access. */
    public StudentResult calculate(Student student, List<Mark> marks) {
        int subjectCount = marks.size();
        if (subjectCount == 0) {
            return new StudentResult(student.getStudentId(), student.getName(), student.getDepartment(),
                    student.getCourse(), 0, 0, 0, 0, GradeUtil.NO_GRADE);
        }
        double total = round(marks.stream().mapToDouble(Mark::getMarks).sum());
        double average = round(total / subjectCount);
        double percentage = round(total / (subjectCount * ValidationUtil.MAX_MARKS) * 100);
        return new StudentResult(student.getStudentId(), student.getName(), student.getDepartment(),
                student.getCourse(), subjectCount, total, average, percentage,
                GradeUtil.calculateGrade(percentage));
    }

    public StudentResult getResult(Student student) throws ServiceException {
        try {
            return calculate(student, markDAO.findByStudent(student.getStudentId()));
        } catch (SQLException e) {
            throw DatabaseErrors.translate(e);
        }
    }

    /** Results for every student, using two queries in total. */
    public List<StudentResult> getAllResults() throws ServiceException {
        try {
            List<Student> students = studentDAO.findAll();
            Map<Integer, List<Mark>> marksByStudent =
                    markDAO.findAll().stream().collect(Collectors.groupingBy(Mark::getStudentId));
            return students.stream()
                    .map(student -> calculate(student, marksByStudent.getOrDefault(student.getStudentId(), List.of())))
                    .toList();
        } catch (SQLException e) {
            throw DatabaseErrors.translate(e);
        }
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
