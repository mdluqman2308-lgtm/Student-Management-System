package com.studentmanagement.ui;

import com.studentmanagement.model.Admin;
import com.studentmanagement.service.AuthenticationService;
import com.studentmanagement.service.ServiceException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

/** Administrator login screen. */
public class LoginFrame extends JFrame {

    private final AuthenticationService authenticationService;
    private final JTextField usernameField = new JTextField(20);
    private final JPasswordField passwordField = new JPasswordField(20);
    private JButton loginButton;

    public LoginFrame(AuthenticationService authenticationService) {
        super("Student Management System - Login");
        this.authenticationService = authenticationService;
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout());
        root.add(UiTheme.header("Student Management System", null), BorderLayout.NORTH);
        root.add(buildForm(), BorderLayout.CENTER);
        setContentPane(root);

        getRootPane().setDefaultButton(loginButton);
        pack();
        setResizable(false);
        setLocationRelativeTo(null);
    }

    private JPanel buildForm() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(UiTheme.BACKGROUND);
        form.setBorder(BorderFactory.createEmptyBorder(25, 40, 25, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel heading = new JLabel("Administrator Login");
        heading.setFont(UiTheme.FONT_BOLD.deriveFont(16f));
        heading.setForeground(UiTheme.PRIMARY);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        form.add(heading, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1;
        form.add(UiTheme.label("Username"), gbc);
        gbc.gridx = 1;
        form.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        form.add(UiTheme.label("Password"), gbc);
        gbc.gridx = 1;
        form.add(passwordField, gbc);

        loginButton = UiTheme.button("Login", UiTheme.ACCENT);
        loginButton.addActionListener(e -> handleLogin());
        JButton exitButton = UiTheme.button("Exit", UiTheme.NEUTRAL);
        exitButton.addActionListener(e -> System.exit(0));

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttons.setOpaque(false);
        buttons.add(exitButton);
        buttons.add(loginButton);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(16, 8, 8, 8);
        form.add(buttons, gbc);

        JLabel hint = new JLabel("Press Enter to log in");
        hint.setForeground(Color.GRAY);
        gbc.gridy = 4;
        gbc.insets = new Insets(0, 8, 0, 8);
        form.add(hint, gbc);
        return form;
    }

    private void handleLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        try {
            Admin admin = authenticationService.login(username, password);
            Navigator.onLoginSuccess(admin);
        } catch (ServiceException e) {
            Dialogs.error(this, e.getMessage());
            passwordField.setText("");
            passwordField.requestFocusInWindow();
        }
    }
}
