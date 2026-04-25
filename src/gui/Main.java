package gui;

import javax.swing.*;

/**
 * TechNova – Faculty Management System
 * Entry point for the application.
 *
 * Run this class to start the application.
 * Ensure MySQL is running and the 'technova' database exists.
 *
 * Required: mysql-connector-j-8.x.jar on classpath
 *
 * Compile (from src/ folder):
 *   javac -cp .;path/to/mysql-connector.jar db/*.java dao/*.java gui/*.java
 *
 * Run:
 *   java -cp .;path/to/mysql-connector.jar gui.Main
 */
public class Main {
    public static void main(String[] args) {
        // Apply system look and feel for better OS integration
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        // Improve font rendering on Windows
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
