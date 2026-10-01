package com.studentmanagement.ui;

import com.studentmanagement.model.SearchField;
import com.studentmanagement.model.Student;
import com.studentmanagement.model.StudentResult;
import com.studentmanagement.service.ResultService;
import com.studentmanagement.service.ServiceException;
import com.studentmanagement.service.StudentService;
import com.studentmanagement.util.ValidationUtil;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Add, update, delete, search and view student records. */
public class StudentFrame extends JFrame {

    private static final String[] COLUMNS =
            {"ID", "Name", "Email", "Phone", "Gender", "Date of Birth", "Department", "Course", "Year"};

    private final StudentService studentService;
    private final ResultService resultService;

    private final DefaultTableModel tableModel = new DefaultTableModel(COLUMNS, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable table = new JTable(tableModel);
    private final List<Student> displayedStudents = new ArrayList<>();

    private final JTextField idField = new JTextField(10);
    private final JTextField nameField = new JTextField(18);
    private final JTextField emailField = new JTextField(18);
    private final JTextField phoneField = new JTextField(18);
    private final JTextField dobField = new JTextField(18);
    private final JTextField departmentField = new JTextField(18);
    private final JTextField courseField = new JTextField(18);
    private final JComboBox<String> genderBox = new JComboBox<>(ValidationUtil.GENDERS.toArray(new String[0]));
    private final JComboBox<Integer> yearBox = new JComboBox<>(new Integer[]{1, 2, 3, 4});

    private final JComboBox<SearchField> searchFieldBox = new JComboBox<>(SearchField.values());
    private final JTextField searchField = new JTextField(20);
    private final JLabel statusLabel = new JLabel(" ");

    public StudentFrame(StudentService studentService, ResultService resultService) {
        super("Student Management System - Students");
        this.studentService = studentService;
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
        center.add(buildFormPanel(), BorderLayout.NORTH);
        center.add(buildTablePanel(), BorderLayout.CENTER);

        JPanel root = new JPanel(new BorderLayout());
        root.add(UiTheme.header("Student Management", backButton), BorderLayout.NORTH);
        root.add(center, BorderLayout.CENTER);
        setContentPane(root);

        idField.setEditable(false);
        idField.setToolTipText("Generated automatically by the database");
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadSelectedStudent();
            }
        });

        setSize(1100, 720);
        setLocationRelativeTo(null);
        loadAllStudents();
    }

    // ---------- UI construction ----------

    private JPanel buildFormPanel() {
        JPanel fields = new JPanel(new GridBagLayout());
        fields.setOpaque(false);
        addField(fields, 0, 0, "Student ID", idField);
        addField(fields, 0, 1, "Full Name *", nameField);
        addField(fields, 0, 2, "Email *", emailField);
        addField(fields, 1, 0, "Phone *", phoneField);
        addField(fields, 1, 1, "Gender *", genderBox);
        addField(fields, 1, 2, "Date of Birth * (YYYY-MM-DD)", dobField);
        addField(fields, 2, 0, "Department *", departmentField);
        addField(fields, 2, 1, "Course *", courseField);
        addField(fields, 2, 2, "Year *", yearBox);

        JButton addButton = UiTheme.button("Add", UiTheme.SUCCESS);
        JButton updateButton = UiTheme.button("Update", UiTheme.ACCENT);
        JButton deleteButton = UiTheme.button("Delete", UiTheme.DANGER);
        JButton clearButton = UiTheme.button("Clear", UiTheme.NEUTRAL);
        JButton detailsButton = UiTheme.button("View Details", UiTheme.WARNING);
        addButton.addActionListener(e -> addStudent());
        updateButton.addActionListener(e -> updateStudent());
        deleteButton.addActionListener(e -> deleteStudent());
        clearButton.addActionListener(e -> clearForm());
        detailsButton.addActionListener(e -> showDetails());

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        buttons.setOpaque(false);
        buttons.add(addButton);
        buttons.add(updateButton);
        buttons.add(deleteButton);
        buttons.add(clearButton);
        buttons.add(detailsButton);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(java.awt.Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Student Details"),
                BorderFactory.createEmptyBorder(4, 4, 4, 4)));
        panel.add(fields, BorderLayout.CENTER);
        panel.add(buttons, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildTablePanel() {
        JButton searchButton = UiTheme.button("Search", UiTheme.ACCENT);
        JButton showAllButton = UiTheme.button("Show All", UiTheme.NEUTRAL);
        searchButton.addActionListener(e -> searchStudents());
        showAllButton.addActionListener(e -> {
            searchField.setText("");
            loadAllStudents();
        });
        searchField.addActionListener(e -> searchStudents());

        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        searchBar.setOpaque(false);
        searchBar.add(UiTheme.label("Search by"));
        searchBar.add(searchFieldBox);
        searchBar.add(searchField);
        searchBar.add(searchButton);
        searchBar.add(showAllButton);

        UiTheme.styleTable(table);
        table.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        JPanel panel = new JPanel(new BorderLayout(0, 4));
        panel.setOpaque(false);
        panel.add(searchBar, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(statusLabel, BorderLayout.SOUTH);
        return panel;
    }

    private void addField(JPanel panel, int row, int column, String label, JComponent field) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 8, 5, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridy = row;

        gbc.gridx = column * 2;
        gbc.weightx = 0;
        panel.add(UiTheme.label(label), gbc);

        gbc.gridx = column * 2 + 1;
        gbc.weightx = 1;
        panel.add(field, gbc);
    }

    // ---------- Actions ----------

    private void addStudent() {
        try {
            int newId = studentService.addStudent(readForm());
            Dialogs.info(this, "Student added successfully.\nStudent ID: " + newId);
            clearForm();
            loadAllStudents();
        } catch (ServiceException e) {
            Dialogs.error(this, e.getMessage());
        }
    }

    private void updateStudent() {
        if (selectedStudentId() == 0) {
            Dialogs.warning(this, "Select a student from the table first.");
            return;
        }
        try {
            studentService.updateStudent(readForm());
            Dialogs.info(this, "Student updated successfully.");
            clearForm();
            loadAllStudents();
        } catch (ServiceException e) {
            Dialogs.error(this, e.getMessage());
        }
    }

    private void deleteStudent() {
        int studentId = selectedStudentId();
        if (studentId == 0) {
            Dialogs.warning(this, "Select a student from the table first.");
            return;
        }
        if (!Dialogs.confirm(this, "Delete student " + studentId + " and all of their marks?\n"
                + "This cannot be undone.")) {
            return;
        }
        try {
            studentService.deleteStudent(studentId);
            Dialogs.info(this, "Student deleted successfully.");
            clearForm();
            loadAllStudents();
        } catch (ServiceException e) {
            Dialogs.error(this, e.getMessage());
        }
    }

    private void searchStudents() {
        try {
            SearchField field = (SearchField) searchFieldBox.getSelectedItem();
            List<Student> found = studentService.search(field, searchField.getText());
            showStudents(found);
            if (found.isEmpty()) {
                Dialogs.info(this, "No students match your search.");
            }
        } catch (ServiceException e) {
            Dialogs.warning(this, e.getMessage());
        }
    }

    private void showDetails() {
        int row = table.getSelectedRow();
        if (row < 0) {
            Dialogs.warning(this, "Select a student from the table first.");
            return;
        }
        Student student = displayedStudents.get(table.convertRowIndexToModel(row));
        try {
            StudentResult result = resultService.getResult(student);
            Dialogs.info(this, buildDetailsText(student, result));
        } catch (ServiceException e) {
            Dialogs.error(this, e.getMessage());
        }
    }

    private String buildDetailsText(Student student, StudentResult result) {
        String academic = result.subjectCount() == 0
                ? "No marks have been entered yet."
                : String.format("Subjects: %d%nTotal: %.2f%nAverage: %.2f%nPercentage: %.2f%%%nGrade: %s",
                result.subjectCount(), result.totalMarks(), result.average(), result.percentage(), result.grade());
        return String.format("Student ID: %d%nName: %s%nEmail: %s%nPhone: %s%nGender: %s%n"
                        + "Date of Birth: %s%nDepartment: %s%nCourse: %s%nYear: %d%n%n--- Academic Result ---%n%s",
                student.getStudentId(), student.getName(), student.getEmail(), student.getPhone(),
                student.getGender(), student.getDateOfBirth(), student.getDepartment(), student.getCourse(),
                student.getYear(), academic);
    }

    // ---------- Table and form helpers ----------

    private void loadAllStudents() {
        try {
            showStudents(studentService.getAllStudents());
        } catch (ServiceException e) {
            Dialogs.error(this, e.getMessage());
        }
    }

    private void showStudents(List<Student> students) {
        displayedStudents.clear();
        displayedStudents.addAll(students);
        tableModel.setRowCount(0);
        for (Student s : students) {
            tableModel.addRow(new Object[]{s.getStudentId(), s.getName(), s.getEmail(), s.getPhone(),
                    s.getGender(), s.getDateOfBirth(), s.getDepartment(), s.getCourse(), s.getYear()});
        }
        statusLabel.setText(students.size() + " student(s) shown");
    }

    private void loadSelectedStudent() {
        int row = table.getSelectedRow();
        if (row < 0) {
            return;
        }
        Student s = displayedStudents.get(table.convertRowIndexToModel(row));
        idField.setText(String.valueOf(s.getStudentId()));
        nameField.setText(s.getName());
        emailField.setText(s.getEmail());
        phoneField.setText(s.getPhone());
        genderBox.setSelectedItem(s.getGender());
        dobField.setText(s.getDateOfBirth().toString());
        departmentField.setText(s.getDepartment());
        courseField.setText(s.getCourse());
        yearBox.setSelectedItem(s.getYear());
    }

    private void clearForm() {
        table.clearSelection();
        idField.setText("");
        nameField.setText("");
        emailField.setText("");
        phoneField.setText("");
        dobField.setText("");
        departmentField.setText("");
        courseField.setText("");
        genderBox.setSelectedIndex(0);
        yearBox.setSelectedIndex(0);
        nameField.requestFocusInWindow();
    }

    private int selectedStudentId() {
        return ValidationUtil.parsePositiveInt(idField.getText()).orElse(0);
    }

    private Student readForm() throws ServiceException {
        Student student = new Student();
        student.setStudentId(selectedStudentId());
        student.setName(nameField.getText());
        student.setEmail(emailField.getText());
        student.setPhone(phoneField.getText());
        student.setGender((String) genderBox.getSelectedItem());
        student.setDateOfBirth(readDateOfBirth());
        student.setDepartment(departmentField.getText());
        student.setCourse(courseField.getText());
        student.setYear((Integer) yearBox.getSelectedItem());
        return student;
    }

    /** Blank stays null so the service reports "required"; bad text is reported as a format error. */
    private LocalDate readDateOfBirth() throws ServiceException {
        String text = dobField.getText();
        if (ValidationUtil.isBlank(text)) {
            return null;
        }
        return ValidationUtil.parseDate(text)
                .orElseThrow(() -> new ServiceException("Date of birth must be a real date in YYYY-MM-DD format."));
    }
}
