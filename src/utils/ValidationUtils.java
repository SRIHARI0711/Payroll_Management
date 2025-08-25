package utils;

import config.DatabaseConfig;
import java.util.regex.Pattern;

/**
 * Utility class for input validation
 */
public class ValidationUtils {
    
    // Email validation pattern
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$");
    
    // Phone validation pattern (supports various formats)
    private static final Pattern PHONE_PATTERN = 
        Pattern.compile("^[\\+]?[1-9]?[0-9]{7,15}$");
    
    /**
     * Validate email format
     * @param email Email to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email.trim()).matches() && 
               email.length() <= DatabaseConfig.MAX_EMAIL_LENGTH;
    }
    
    /**
     * Validate phone number format
     * @param phone Phone number to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return true; // Phone is optional
        }
        // Remove spaces, dashes, and parentheses for validation
        String cleanPhone = phone.replaceAll("[\\s\\-\\(\\)]", "");
        return PHONE_PATTERN.matcher(cleanPhone).matches();
    }
    
    /**
     * Validate name (first name, last name)
     * @param name Name to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        String trimmedName = name.trim();
        return trimmedName.length() >= 2 && 
               trimmedName.length() <= DatabaseConfig.MAX_NAME_LENGTH &&
               trimmedName.matches("^[a-zA-Z\\s]+$");
    }
    
    /**
     * Validate password strength
     * @param password Password to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidPassword(String password) {
        if (password == null || password.length() < DatabaseConfig.MIN_PASSWORD_LENGTH) {
            return false;
        }
        return true; // Basic validation - can be enhanced with complexity requirements
    }
    
    /**
     * Validate salary amount
     * @param salary Salary to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidSalary(double salary) {
        return salary >= 0 && salary <= 999999.99;
    }
    
    /**
     * Validate employee code format
     * @param code Employee code to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidEmployeeCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            return false;
        }
        String trimmedCode = code.trim();
        return trimmedCode.length() >= 3 && 
               trimmedCode.length() <= 20 &&
               trimmedCode.matches("^[A-Z0-9]+$");
    }
    
    /**
     * Validate department code format
     * @param code Department code to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidDepartmentCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            return false;
        }
        String trimmedCode = code.trim();
        return trimmedCode.length() >= 2 && 
               trimmedCode.length() <= 10 &&
               trimmedCode.matches("^[A-Z0-9]+$");
    }
    
    /**
     * Check if string is null or empty
     * @param str String to check
     * @return true if null or empty, false otherwise
     */
    public static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
    
    /**
     * Sanitize string input (remove leading/trailing spaces)
     * @param input Input string
     * @return Sanitized string
     */
    public static String sanitizeInput(String input) {
        return input == null ? "" : input.trim();
    }
}