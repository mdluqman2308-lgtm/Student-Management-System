package com.studentmanagement.ui;

import com.studentmanagement.model.Admin;
import com.studentmanagement.service.AuthenticationService;
import com.studentmanagement.service.MarkService;
import com.studentmanagement.service.ResultService;
import com.studentmanagement.service.StudentService;

import javax.swing.JFrame;

/**
 * Creates the services once and moves the user between screens.
 * Only one screen is open at a time: showing a new one closes the previous one.
 */
public final class Navigator {

    private static final AuthenticationService AUTH_SERVICE = new AuthenticationService();
    private static final StudentService STUDENT_SERVICE = new StudentService();
    private static final MarkService MARK_SERVICE = new MarkService();
    private static final ResultService RESULT_SERVICE = new ResultService();

    private static JFrame currentFrame;
    private static Admin loggedInAdmin;

    private Navigator() {
    }

    public static void showLogin() {
        display(new LoginFrame(AUTH_SERVICE));
    }

    public static void onLoginSuccess(Admin admin) {
        loggedInAdmin = admin;
        showDashboard();
    }

    public static void showDashboard() {
        display(new DashboardFrame(loggedInAdmin, STUDENT_SERVICE));
    }

    public static void showStudents() {
        display(new StudentFrame(STUDENT_SERVICE, RESULT_SERVICE));
    }

    public static void showMarks() {
        display(new MarksFrame(STUDENT_SERVICE, MARK_SERVICE, RESULT_SERVICE));
    }

    public static void showResults() {
        display(new ResultFrame(RESULT_SERVICE));
    }

    public static void logout() {
        loggedInAdmin = null;
        showLogin();
    }

    private static void display(JFrame next) {
        JFrame previous = currentFrame;
        currentFrame = next;
        next.setVisible(true);
        if (previous != null) {
            previous.dispose();
        }
    }
}
