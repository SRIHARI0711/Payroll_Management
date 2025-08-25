package main;

import gui.LoginFrame;
import utils.DatabaseConnection;

import javax.swing.*;

/**
 * Main class for the Payroll Management System
 */
public class PayrollManagementSystem {
    
    public static void main(String[] args) {
        // Set system properties for better UI experience
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        
        // Set look and feel to system default
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Could not set system look and feel: " + e.getMessage());
            // Continue with default look and feel
        }
        
        // Set default font for better readability
        try {
            UIManager.put("defaultFont", new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 12));
        } catch (Exception e) {
            System.err.println("Could not set default font: " + e.getMessage());
        }
        
        // Test database connection on startup
        System.out.println("Starting Payroll Management System...");
        System.out.println("Testing database connection...");
        
        if (!DatabaseConnection.testConnection()) {
            // Show error dialog if database connection fails
            SwingUtilities.invokeLater(() -> {
                String errorMessage = "Failed to connect to the database.\n\n" +
                    "Please ensure that:\n" +
                    "1. MySQL server is running\n" +
                    "2. Database 'payroll_management' exists\n" +
                    "3. Database credentials in DatabaseConfig.java are correct\n" +
                    "4. MySQL Connector/J JAR is in the classpath\n\n" +
                    "Troubleshooting steps:\n" +
                    "• Run database/setup_database.bat to create the database\n" +
                    "• Check TROUBLESHOOTING.md for detailed solutions\n" +
                    "• Verify MySQL service is running\n\n" +
                    "The application will continue to load, but database operations will fail.";
                
                JOptionPane.showMessageDialog(null,
                    errorMessage,
                    "Database Connection Error",
                    JOptionPane.ERROR_MESSAGE);
            });
        } else {
            System.out.println("Database connection successful!");
            
            // Initialize database if needed
            if (!DatabaseConnection.initializeDatabase()) {
                System.out.println("Warning: Database initialization check failed.");
            }
        }
        
        // Launch the application on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            try {
                // Create and show the login frame
                LoginFrame loginFrame = new LoginFrame();
                loginFrame.setVisible(true);
                
                System.out.println("Payroll Management System started successfully!");
                System.out.println("Default login credentials:");
                System.out.println("Username: admin");
                System.out.println("Password: admin123");
                
            } catch (Exception e) {
                System.err.println("Error starting application: " + e.getMessage());
                e.printStackTrace();
                
                // Show error dialog
                JOptionPane.showMessageDialog(null,
                    "Error starting the application:\n" + e.getMessage(),
                    "Application Error",
                    JOptionPane.ERROR_MESSAGE);
                
                System.exit(1);
            }
        });
        
        // Add shutdown hook to properly close database connection
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down Payroll Management System...");
            DatabaseConnection.closeConnection();
            System.out.println("Application shutdown complete.");
        }));
    }
}