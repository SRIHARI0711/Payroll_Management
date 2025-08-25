package utils;

import config.DatabaseConfig;
import java.sql.*;
import java.util.List;

/**
 * Simple database connection test utility
 * Run this class to test database connectivity and diagnose issues
 */
public class DatabaseTest {
    
    public static void main(String[] args) {
        System.out.println("=== Payroll Management System Database Test ===");
        System.out.println();
        
        // Test 1: Basic connection test
        System.out.println("1. Testing basic database connection...");
        testBasicConnection();
        
        // Test 2: Validate database structure
        System.out.println("\n2. Validating database structure...");
        validateDatabaseStructure();
        
        // Test 3: Test CRUD operations
        System.out.println("\n3. Testing basic CRUD operations...");
        testCrudOperations();
        
        // Test 4: Show database statistics
        System.out.println("\n4. Database statistics:");
        showDatabaseStats();
        
        System.out.println("\n=== Test Complete ===");
    }
    
    private static void testBasicConnection() {
        try {
            System.out.println("Database URL: " + DatabaseConfig.DB_URL);
            System.out.println("Username: " + DatabaseConfig.DB_USERNAME);
            System.out.println("Driver: " + DatabaseConfig.DB_DRIVER);
            
            Connection conn = DatabaseConnection.getConnection();
            if (conn != null && !conn.isClosed()) {
                System.out.println("✓ Connection successful!");
                
                // Test a simple query
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery("SELECT VERSION()")) {
                    if (rs.next()) {
                        System.out.println("✓ MySQL Version: " + rs.getString(1));
                    }
                }
                
                conn.close();
            } else {
                System.out.println("✗ Connection failed!");
            }
        } catch (SQLException e) {
            System.out.println("✗ Connection error: " + e.getMessage());
            System.out.println("Error Code: " + e.getErrorCode());
            System.out.println("SQL State: " + e.getSQLState());
            
            // Provide specific troubleshooting advice
            provideTroubleshootingAdvice(e);
        }
    }
    
    private static void validateDatabaseStructure() {
        List<String> issues = DatabaseValidator.validateDatabase();
        
        if (issues.isEmpty()) {
            System.out.println("✓ Database structure is valid!");
        } else {
            System.out.println("✗ Database validation issues found:");
            for (String issue : issues) {
                System.out.println("  - " + issue);
            }
            
            // Try to fix common issues
            System.out.println("\nAttempting to fix common issues...");
            List<String> fixes = DatabaseValidator.fixCommonIssues();
            for (String fix : fixes) {
                System.out.println("  ✓ " + fix);
            }
        }
    }
    
    private static void testCrudOperations() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            
            // Test reading from users table
            try (PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM users");
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("✓ Users table accessible, " + rs.getInt(1) + " records found");
                }
            }
            
            // Test reading from departments table
            try (PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM departments");
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("✓ Departments table accessible, " + rs.getInt(1) + " records found");
                }
            }
            
            // Test reading from employees table
            try (PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM employees");
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("✓ Employees table accessible, " + rs.getInt(1) + " records found");
                }
            }
            
            // Test reading from payroll table
            try (PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM payroll");
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("✓ Payroll table accessible, " + rs.getInt(1) + " records found");
                }
            }
            
        } catch (SQLException e) {
            System.out.println("✗ CRUD test failed: " + e.getMessage());
        }
    }
    
    private static void showDatabaseStats() {
        String stats = DatabaseValidator.getDatabaseStats();
        System.out.println(stats);
    }
    
    private static void provideTroubleshootingAdvice(SQLException e) {
        String message = e.getMessage().toLowerCase();
        
        System.out.println("\nTroubleshooting advice:");
        
        if (message.contains("access denied")) {
            System.out.println("• Check username and password in DatabaseConfig.java");
            System.out.println("• Ensure MySQL user has proper privileges");
            System.out.println("• Try: GRANT ALL PRIVILEGES ON payroll_management.* TO 'root'@'localhost';");
        } else if (message.contains("unknown database")) {
            System.out.println("• Create the database: CREATE DATABASE payroll_management;");
            System.out.println("• Run the schema_fixed.sql file");
            System.out.println("• Or run database/setup_database.bat");
        } else if (message.contains("connection refused")) {
            System.out.println("• Start MySQL server");
            System.out.println("• Check if MySQL is running on port 3306");
            System.out.println("• Verify MySQL service is started");
        } else if (message.contains("driver")) {
            System.out.println("• Add mysql-connector-java.jar to the lib folder");
            System.out.println("• Ensure the JAR is in the classpath");
            System.out.println("• Download from: https://dev.mysql.com/downloads/connector/j/");
        } else if (message.contains("communications link failure")) {
            System.out.println("• Check network connectivity");
            System.out.println("• Verify MySQL server is running");
            System.out.println("• Check firewall settings");
        }
        
        System.out.println("• See TROUBLESHOOTING.md for detailed solutions");
    }
}