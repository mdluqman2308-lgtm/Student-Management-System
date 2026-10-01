package com.studentmanagement;

import com.studentmanagement.ui.Navigator;
import com.studentmanagement.ui.UiTheme;

import javax.swing.SwingUtilities;

/** Application entry point. */
public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            UiTheme.applyLookAndFeel();
            Navigator.showLogin();
        });
    }
}
