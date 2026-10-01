package com.studentmanagement.ui;

import com.studentmanagement.model.Admin;
import com.studentmanagement.model.DashboardStats;
import com.studentmanagement.service.ServiceException;
import com.studentmanagement.service.StudentService;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;

/** Home screen shown after login: statistics and quick navigation buttons. */
public class DashboardFrame extends JFrame {

    public DashboardFrame(Admin admin, StudentService studentService) {
        super("Student Management System - Dashboard");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JLabel welcome = new JLabel("Logged in as: " + admin.getUsername());
        welcome.setForeground(Color.WHITE);
        welcome.setFont(UiTheme.FONT_REGULAR);

        JPanel root = new JPanel(new BorderLayout());
        root.add(UiTheme.header("Dashboard", welcome), BorderLayout.NORTH);
        root.add(buildBody(loadStats(studentService)), BorderLayout.CENTER);
        setContentPane(root);

        setSize(920, 460);
        setMinimumSize(new Dimension(760, 420));
        setLocationRelativeTo(null);
    }

    private DashboardStats loadStats(StudentService studentService) {
        try {
            return studentService.getDashboardStats();
        } catch (ServiceException e) {
            Dialogs.error(this, e.getMessage());
            return new DashboardStats(0, 0, 0);
        }
    }

    private JPanel buildBody(DashboardStats stats) {
        JPanel body = new JPanel(new BorderLayout(0, 30));
        body.setBackground(UiTheme.BACKGROUND);
        body.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JPanel cards = new JPanel(new GridLayout(1, 3, 20, 0));
        cards.setOpaque(false);
        cards.add(statCard("Total Students", stats.totalStudents(), UiTheme.ACCENT));
        cards.add(statCard("Total Departments", stats.totalDepartments(), UiTheme.SUCCESS));
        cards.add(statCard("Total Courses", stats.totalCourses(), UiTheme.WARNING));
        body.add(cards, BorderLayout.NORTH);

        JPanel buttons = new JPanel(new GridLayout(1, 5, 12, 0));
        buttons.setOpaque(false);
        buttons.add(actionButton("Add Student", UiTheme.ACCENT, Navigator::showStudents));
        buttons.add(actionButton("View Students", UiTheme.ACCENT, Navigator::showStudents));
        buttons.add(actionButton("Manage Marks", UiTheme.SUCCESS, Navigator::showMarks));
        buttons.add(actionButton("Results", UiTheme.WARNING, Navigator::showResults));
        buttons.add(actionButton("Logout", UiTheme.DANGER, this::confirmLogout));

        JPanel buttonArea = new JPanel(new BorderLayout(0, 10));
        buttonArea.setOpaque(false);
        buttonArea.add(UiTheme.label("Quick Actions"), BorderLayout.NORTH);
        buttonArea.add(buttons, BorderLayout.CENTER);
        body.add(buttonArea, BorderLayout.CENTER);
        return body;
    }

    private JPanel statCard(String title, int value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(5, 0, 0, 0, color),
                BorderFactory.createEmptyBorder(18, 10, 18, 10)));

        JLabel number = new JLabel(String.valueOf(value), SwingConstants.CENTER);
        number.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 38));
        number.setForeground(color);
        JLabel caption = new JLabel(title, SwingConstants.CENTER);
        caption.setFont(UiTheme.FONT_BOLD);
        caption.setForeground(UiTheme.NEUTRAL);

        card.add(number, BorderLayout.CENTER);
        card.add(caption, BorderLayout.SOUTH);
        return card;
    }

    private JButton actionButton(String text, Color color, Runnable action) {
        JButton button = UiTheme.button(text, color);
        button.setPreferredSize(new Dimension(120, 90));
        button.addActionListener(e -> action.run());
        return button;
    }

    private void confirmLogout() {
        if (Dialogs.confirm(this, "Do you want to log out?")) {
            Navigator.logout();
        }
    }
}
