package com.studentmanagement.ui;

import com.studentmanagement.model.StudentResult;
import com.studentmanagement.service.ResultService;
import com.studentmanagement.service.ServiceException;
import com.studentmanagement.util.GradeUtil;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

/** Read-only table showing every student's total, average, percentage and grade. */
public class ResultFrame extends JFrame {

    private static final String[] COLUMNS =
            {"ID", "Name", "Department", "Course", "Subjects", "Total", "Average", "Percentage", "Grade"};
    private static final int GRADE_COLUMN = 8;

    private final ResultService resultService;
    private final DefaultTableModel tableModel = new DefaultTableModel(COLUMNS, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable table = new JTable(tableModel);
    private final JLabel statusLabel = new JLabel(" ");

    public ResultFrame(ResultService resultService) {
        super("Student Management System - Results");
        this.resultService = resultService;

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                Navigator.showDashboard();
            }
        });

        JButton refreshButton = UiTheme.button("Refresh", UiTheme.ACCENT);
        refreshButton.addActionListener(e -> loadResults());
        JButton backButton = UiTheme.button("Back to Dashboard", UiTheme.NEUTRAL);
        backButton.addActionListener(e -> Navigator.showDashboard());

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);
        actions.add(refreshButton);
        actions.add(backButton);

        UiTheme.styleTable(table);
        table.getColumnModel().getColumn(GRADE_COLUMN).setCellRenderer(new GradeRenderer());

        JPanel center = new JPanel(new BorderLayout(0, 6));
        center.setBackground(UiTheme.BACKGROUND);
        center.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        center.add(new JScrollPane(table), BorderLayout.CENTER);
        center.add(statusLabel, BorderLayout.SOUTH);

        JPanel root = new JPanel(new BorderLayout());
        root.add(UiTheme.header("Student Results", actions), BorderLayout.NORTH);
        root.add(center, BorderLayout.CENTER);
        setContentPane(root);

        setSize(1000, 560);
        setLocationRelativeTo(null);
        loadResults();
    }

    private void loadResults() {
        try {
            List<StudentResult> results = resultService.getAllResults();
            tableModel.setRowCount(0);
            for (StudentResult r : results) {
                tableModel.addRow(new Object[]{r.studentId(), r.name(), r.department(), r.course(),
                        r.subjectCount(), String.format("%.2f", r.totalMarks()),
                        String.format("%.2f", r.average()), String.format("%.2f%%", r.percentage()), r.grade()});
            }
            long withMarks = results.stream().filter(r -> r.subjectCount() > 0).count();
            statusLabel.setText(results.size() + " student(s), " + withMarks + " with marks entered");
        } catch (ServiceException e) {
            Dialogs.error(this, e.getMessage());
        }
    }

    /** Colours the grade cell: green for A+/A, red for F, grey when there are no marks. */
    private static class GradeRenderer extends DefaultTableCellRenderer {

        GradeRenderer() {
            setHorizontalAlignment(SwingConstants.CENTER);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            String grade = String.valueOf(value);
            if (!isSelected) {
                cell.setForeground(gradeColor(grade));
            }
            return cell;
        }

        private Color gradeColor(String grade) {
            return switch (grade) {
                case "A+", "A" -> UiTheme.SUCCESS;
                case "F" -> UiTheme.DANGER;
                case GradeUtil.NO_GRADE -> Color.GRAY;
                default -> Color.BLACK;
            };
        }
    }
}
