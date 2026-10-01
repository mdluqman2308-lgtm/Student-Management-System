package com.studentmanagement.ui;

import javax.swing.JOptionPane;
import java.awt.Component;

/** One place for every message box, so wording and icons stay consistent. */
public final class Dialogs {

    private Dialogs() {
    }

    public static void error(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void warning(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Please check", JOptionPane.WARNING_MESSAGE);
    }

    public static void info(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Information", JOptionPane.INFORMATION_MESSAGE);
    }

    public static boolean confirm(Component parent, String message) {
        int choice = JOptionPane.showConfirmDialog(parent, message, "Please confirm",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        return choice == JOptionPane.YES_OPTION;
    }
}
