package config;

/**
 * Database configuration class containing connection parameters
 */
public class DatabaseConfig {
    // Database connection parameters
    public static final String DB_URL = "jdbc:mysql://localhost:3306/payroll_management";
    public static final String DB_USERNAME = "root";
    public static final String DB_PASSWORD = "Hari@2005"; // Update with your MySQL password
    public static final String DB_DRIVER = "com.mysql.cj.jdbc.Driver";
    
    // Connection pool settings
    public static final int MAX_CONNECTIONS = 10;
    public static final int CONNECTION_TIMEOUT = 30000; // 30 seconds
    
    // Application settings
    public static final String APP_NAME = "Payroll Management System";
    public static final String APP_VERSION = "1.0.0";
    
    // Date formats
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    
    // Validation constants
    public static final int MIN_PASSWORD_LENGTH = 6;
    public static final int MAX_NAME_LENGTH = 50;
    public static final int MAX_EMAIL_LENGTH = 100;
}