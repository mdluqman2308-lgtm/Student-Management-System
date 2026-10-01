package com.studentmanagement.ui;

import com.studentmanagement.model.Mark;
import com.studentmanagement.model.Student;
import com.studentmanagement.model.StudentResult;
import com.studentmanagement.service.MarkService;
import com.studentmanagement.service.ResultService;
import com.studentmanagement.service.ServiceException;
import com.studentmanagement.service.StudentService;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

/** Add, update, delete and view subject marks for one selected student. */
public class MarksFrame extends JFrame {

    private static final String[] COLUMNS = {"Mark ID", "Subject", "Marks (out of 100)"};

    private final MarkService markService;
    private final ResultService resultService;

    private final JComboBox<Student> studentBox = new JComboBox<>();
    private final JTextField subjectField = new JTextField(20);
    private final JTextField marksField = new JTextField(8);
    private final JLabel summaryLabel = new JLabel(" ");
    private final DefaultTableModel tableModel = new DefaultTableModel(COLUMNS, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable table = new JTable(tableModel);
    private final List<Mark> displayedMarks = new ArrayList<>();

    public MarksFrame(StudentService studentService, MarkService markService, ResultService resultService) {
        super("Student Management System - Marks");
        this.markService = markService;
        this.resultService = resultService;

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                Navigator.showDashboard();
            }
        });

        JButton backButton = UiTheme.button("Back to Dashboard", UiTheme.NEUTRAL);
        backButton.addActionListener(e -> Navigator.showDashboard());

        JPanel center = new JPanel(new BorderLayout(0, 10));
        center.setBackground(UiTheme.BACKGROUND);
        center.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        center.add(buildControlPanel(), BorderLayout.NORTH);
        center.add(buildTablePanel(), BorderLayout.CENTER);

        JPanel root = new JPanel(new BorderLayout());
        root.add(UiTheme.header("Marks Management", backButton), BorderLayout.NORTH);
        root.add(center, BorderLayout.CENTER);
        setContentPane(root);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadSelectedMark();
            }
        });

        setSize(850, 600);
        setLocationRelativeTo(null);
        loadStudents(studentService);
    }

    // ---------- UI construction ----------

    private JPanel buildControlPanel() {
        JPanel selectRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        selectRow.setOpaque(false);
        selectRow.add(UiTheme.label("Student"));
        selectRow.add(studentBox);

        JButton addButton = UiTheme.button("Add", UiTheme.SUCCESS);
        JButton updateButton = UiTheme.button("Update", UiTheme.ACCENT);
        JButton deleteButton = UiTheme.button("Delete", UiTheme.DANGER);
        JButton clearButton = UiTheme.button("Clear", UiTheme.NEUTRAL);
        addButton.addActionListener(e -> addMark());
        updateButton.addActionListener(e -> updateMark());
        deleteButton.addActionListener(e -> deleteMark());
        clearButton.addActionListener(e -> clearForm());

        JPanel markRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        markRow.setOpaque(false);
        markRow.add(UiTheme.label("Subject"));
        markRow.add(subjectField);
        markRow.add(UiTheme.label("Marks"));
        markRow.add(marksField);
        markRow.add(addButton);
        markRow.add(updateButton);
        markRow.add(deleteButton);
        markRow.add(clearButton);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Select Student and Enter Marks"),
                BorderFactory.createEmptyBorder(4, 4, 4, 4)));
        panel.add(selectRow, BorderLayout.NORTH);
        panel.add(markRow, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildTablePanel() {
        UiTheme.styleTable(table);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        summaryLabel.setFont(UiTheme.FONT_BOLD);
        summaryLabel.setForeground(UiTheme.PRIMARY);
        summaryLabel.setBorder(BorderFactory.createEmptyBorder(6, 4, 0, 4));

        JPanel panel = new JPanel(new BorderLayout(0, 4));
        panel.setOpaque(false);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(summaryLabel, BorderLayout.SOUTH);
        return panel;
    }

    // ---------- Actions ----------

    private void loadStudents(StudentService studentService) {
        try {
            List<Student> students = studentService.getAllStudents();
            studentBox.setModel(new DefaultComboBoxModel<>(students.toArray(new Student[0])));
            if (students.isEmpty()) {
                Dialogs.info(this, "There are no students yet. Add a student first.");
            }
            studentBox.addActionListener(e -> {
                subjectField.setText("");
                marksField.setText("");
                loadMarks();
            });
            loadMarks();
        } catch (ServiceException e) {
            Dialogs.error(this, e.getMessage());
        }
    }

    private void addMark() {
        Student student = selectedStudent();
        if (student == null) {
            Dialogs.warning(this, "Select a student first.");
            return;
        }
        try {
            markService.addMark(student.getStudentId(), subjectField.getText(), marksField.getText());
            clearForm();
            loadMarks();
        } catch (ServiceException e) {
            Dialogs.error(this, e.getMessage());
        }
    }

    private void updateMark() {
        Mark mark = selectedMark();
        if (mark == null) {
            Dialogs.warning(this, "Select a mark from the table first.");
            return;
        }
        try {
            markService.updateMark(mark.getMarkId(), mark.getStudentId(), subjectField.getText(),
                    marksField.getText());
            clearForm();
            loadMarks();
        } catch (ServiceException e) {
            Dialogs.error(this, e.getMessage());
        }
    }

    private void deleteMark() {
        Mark mark = selectedMark();
        if (mark == null) {
            Dialogs.warning(this, "Select a mark from the table first.");
            return;
        }
        if (!Dialogs.confirm(this, "Delete the marks for '" + mark.getSubject() + "'?")) {
            return;
        }
        try {
            markService.deleteMark(mark.getMarkId());
            clearForm();
            loadMarks();
        } catch (ServiceException e) {
            Dialogs.error(this, e.getMessage());
        }
    }

    // ---------- Helpers ----------

    private void loadMarks() {
        displayedMarks.clear();
        tableModel.setRowCount(0);
        Student student = selectedStudent();
        if (student == null) {
            summaryLabel.setText(" ");
            return;
        }
        try {
            displayedMarks.addAll(markService.getMarks(student.getStudentId()));
            for (Mark m : displayedMarks) {
                tableModel.addRow(new Object[]{m.getMarkId(), m.getSubject(), String.format("%.2f", m.getMarks())});
            }
            summaryLabel.setText(buildSummary(resultService.calculate(student, displayedMarks)));
        } catch (ServiceException e) {
            Dialogs.error(this, e.getMessage());
        }
    }

    private String buildSummary(StudentResult result) {
        if (result.subjectCount() == 0) {
            return "No marks entered for this student yet.";
        }
        return String.format("Subjects: %d   |   Total: %.2f   |   Average: %.2f   |   Percentage: %.2f%%   |   Grade: %s",
                result.subjectCount(), result.totalMarks(), result.average(), result.percentage(), result.grade());
    }

    private void loadSelectedMark() {
        Mark mark = selectedMark();
        if (mark != null) {
            subjectField.setText(mark.getSubject());
            marksField.setText(String.format("%.2f", mark.getMarks()));
        }
    }

    private void clearForm() {
        table.clearSelection();
        subjectField.setText("");
        marksField.setText("");
        subjectField.requestFocusInWindow();
    }

    private Student selectedStudent() {
        return (Student) studentBox.getSelectedItem();
    }

    private Mark selectedMark() {
        int row = table.getSelectedRow();
        return row < 0 ? null : displayedMarks.get(table.convertRowIndexToModel(row));
    }
}
