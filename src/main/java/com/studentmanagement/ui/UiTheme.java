package com.studentmanagement.ui;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.UIManager;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;

/** Shared colours, fonts and small component factories so every screen looks consistent. */
public final class UiTheme {

    public static final Color PRIMARY = new Color(25, 55, 109);
    public static final Color ACCENT = new Color(41, 128, 185);
    public static final Color SUCCESS = new Color(39, 150, 90);
    public static final Color WARNING = new Color(214, 137, 16);
    public static final Color DANGER = new Color(192, 57, 43);
    public static final Color NEUTRAL = new Color(99, 110, 114);
    public static final Color BACKGROUND = new Color(244, 246, 249);

    public static final Font FONT_REGULAR = new Font(Font.SANS_SERIF, Font.PLAIN, 13);
    public static final Font FONT_BOLD = new Font(Font.SANS_SERIF, Font.BOLD, 13);
    public static final Font FONT_TITLE = new Font(Font.SANS_SERIF, Font.BOLD, 22);

    private UiTheme() {
    }

    /** The cross-platform look and feel honours button colours on every operating system. */
    public static void applyLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            // The default look and feel still works, so nothing else is needed.
        }
    }

    public static JButton button(String text, Color background) {
        JButton button = new JButton(text);
        button.setFont(FONT_BOLD);
        button.setBackground(background);
        button.setForeground(Color.WHITE);
        button.setOpaque(true);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    public static JLabel label(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_BOLD);
        return label;
    }

    /** A dark-blue banner with a title on the left and an optional component on the right. */
    public static JPanel header(String title, java.awt.Component rightSide) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PRIMARY);
        panel.setBorder(BorderFactory.createEmptyBorder(14, 20, 14, 20));
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(FONT_TITLE);
        titleLabel.setForeground(Color.WHITE);
        panel.add(titleLabel, BorderLayout.WEST);
        if (rightSide != null) {
            panel.add(rightSide, BorderLayout.EAST);
        }
        return panel;
    }

    public static void styleTable(JTable table) {
        table.setFont(FONT_REGULAR);
        table.setRowHeight(26);
        table.setAutoCreateRowSorter(true);
        table.setFillsViewportHeight(true);
        table.getTableHeader().setFont(FONT_BOLD);
        table.getTableHeader().setReorderingAllowed(false);
        table.setSelectionBackground(new Color(190, 215, 240));
        table.setSelectionForeground(Color.BLACK);
    }
}
