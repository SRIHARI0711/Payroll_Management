package utils;

import javax.swing.*;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.SQLSyntaxErrorException;
import java.sql.SQLTimeoutException;

/**
 * Centralized error handling utility class
 */
public class ErrorHandler {
    
    /**
     * Handle SQL exceptions with user-friendly messages
     * @param e SQLException to handle
     * @param operation Description of the operation that failed
     * @param parent Parent component for dialog
     */
    public static void handleSQLException(SQLException e, String operation, JComponent parent) {
        String userMessage = getUserFriendlyMessage(e, operation);
        String technicalMessage = "Technical details: " + e.getMessage();
        
        // Log the technical error
        System.err.println("SQL Error during " + operation + ": " + e.getMessage());
        e.printStackTrace();
        
        // Show user-friendly message
        JOptionPane.showMessageDialog(parent, 
            userMessage + "\n\n" + technicalMessage,
            "Database Error", 
            JOptionPane.ERROR_MESSAGE);
    }
    
    /**
     * Handle general exceptions
     * @param e Exception to handle
     * @param operation Description of the operation that failed
     * @param parent Parent component for dialog
     */
    public static void handleException(Exception e, String operation, JComponent parent) {
        String userMessage = "An error occurred while " + operation + ".";
        String technicalMessage = "Technical details: " + e.getMessage();
        
        // Log the error
        System.err.println("Error during " + operation + ": " + e.getMessage());
        e.printStackTrace();
        
        // Show user-friendly message
        JOptionPane.showMessageDialog(parent, 
            userMessage + "\n\n" + technicalMessage,
            "Application Error", 
            JOptionPane.ERROR_MESSAGE);
    }
    
    /**
     * Convert SQL exception to user-friendly message
     * @param e SQLException
     * @param operation Operation description
     * @return User-friendly error message
     */
    private static String getUserFriendlyMessage(SQLException e, String operation) {
        String message = e.getMessage().toLowerCase();
        
        if (e instanceof SQLIntegrityConstraintViolationException) {
            if (message.contains("duplicate entry")) {
                if (message.contains("employee_code")) {
                    return "Employee code already exists. Please use a different employee code.";
                } else if (message.contains("email")) {
                    return "Email address already exists. Please use a different email address.";
                } else if (message.contains("username")) {
                    return "Username already exists. Please choose a different username.";
                } else {
                    return "A record with this information already exists. Please check for duplicates.";
                }
            } else if (message.contains("foreign key constraint")) {
                if (message.contains("department")) {
                    return "Cannot complete operation: Invalid department selected.";
                } else if (message.contains("employee")) {
                    return "Cannot complete operation: Employee reference is invalid.";
                } else {
                    return "Cannot complete operation: Referenced record does not exist.";
                }
            } else if (message.contains("cannot delete")) {
                return "Cannot delete this record because it is referenced by other records.";
            }
        } else if (e instanceof SQLSyntaxErrorException) {
            return "Database query error. Please contact system administrator.";
        } else if (e instanceof SQLTimeoutException) {
            return "Database operation timed out. Please try again or contact system administrator.";
        } else if (message.contains("connection")) {
            return "Database connection error. Please check your network connection and try again.";
        } else if (message.contains("access denied")) {
            return "Database access denied. Please contact system administrator.";
        } else if (message.contains("unknown database")) {
            return "Database not found. Please contact system administrator to set up the database.";
        } else if (message.contains("table") && message.contains("doesn't exist")) {
            return "Database table not found. Please contact system administrator to set up the database.";
        } else if (message.contains("column") && message.contains("unknown")) {
            return "Database structure error. Please contact system administrator.";
        }
        
        // Default message for unknown SQL errors
        return "Database error occurred while " + operation + ". Please try again or contact system administrator.";
    }
    
    /**
     * Show validation error message
     * @param message Error message
     * @param parent Parent component
     */
    public static void showValidationError(String message, JComponent parent) {
        JOptionPane.showMessageDialog(parent, 
            message,
            "Validation Error", 
            JOptionPane.WARNING_MESSAGE);
    }
    
    /**
     * Show information message
     * @param message Information message
     * @param parent Parent component
     */
    public static void showInfo(String message, JComponent parent) {
        JOptionPane.showMessageDialog(parent, 
            message,
            "Information", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Show success message
     * @param message Success message
     * @param parent Parent component
     */
    public static void showSuccess(String message, JComponent parent) {
        JOptionPane.showMessageDialog(parent, 
            message,
            "Success", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Show confirmation dialog
     * @param message Confirmation message
     * @param title Dialog title
     * @param parent Parent component
     * @return true if user confirmed, false otherwise
     */
    public static boolean showConfirmation(String message, String title, JComponent parent) {
        int result = JOptionPane.showConfirmDialog(parent, 
            message,
            title, 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        return result == JOptionPane.YES_OPTION;
    }
    
    /**
     * Log error to console with timestamp
     * @param operation Operation description
     * @param error Error message
     */
    public static void logError(String operation, String error) {
        System.err.println("[" + java.time.LocalDateTime.now() + "] ERROR in " + operation + ": " + error);
    }
    
    /**
     * Log info to console with timestamp
     * @param operation Operation description
     * @param message Info message
     */
    public static void logInfo(String operation, String message) {
        System.out.println("[" + java.time.LocalDateTime.now() + "] INFO in " + operation + ": " + message);
    }
}