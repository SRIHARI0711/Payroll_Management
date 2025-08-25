package utils;

import config.DatabaseConfig;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * Database connection utility class with improved error handling
 */
public class DatabaseConnection {
    private static Connection connection = null;
    
    /**
     * Get database connection with enhanced configuration
     * @return Connection object
     * @throws SQLException if connection fails
     */
    public static Connection getConnection() throws SQLException {
        try {
            // Load MySQL JDBC driver
            Class.forName(DatabaseConfig.DB_DRIVER);
            
            // Set connection properties for better compatibility
            Properties props = new Properties();
            props.setProperty("user", DatabaseConfig.DB_USERNAME);
            props.setProperty("password", DatabaseConfig.DB_PASSWORD);
            props.setProperty("useSSL", "false");
            props.setProperty("allowPublicKeyRetrieval", "true");
            props.setProperty("serverTimezone", "UTC");
            props.setProperty("autoReconnect", "true");
            props.setProperty("useUnicode", "true");
            props.setProperty("characterEncoding", "UTF-8");
            props.setProperty("connectTimeout", "60000");
            props.setProperty("socketTimeout", "60000");
            
            // Create connection with properties
            Connection conn = DriverManager.getConnection(DatabaseConfig.DB_URL, props);
            
            // Set connection properties
            if (conn != null) {
                conn.setAutoCommit(true);
                // Test the connection
                try (Statement stmt = conn.createStatement()) {
                    stmt.executeQuery("SELECT 1").close();
                }
            }
            
            return conn;
            
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found. Please ensure mysql-connector-java.jar is in the classpath: " + e.getMessage());
        } catch (SQLException e) {
            String errorMsg = "Failed to connect to database: " + e.getMessage();
            
            // Provide specific error messages for common issues
            if (e.getMessage().contains("Access denied")) {
                errorMsg += "\n\nPossible solutions:\n" +
                           "1. Check username and password in DatabaseConfig.java\n" +
                           "2. Ensure MySQL user has proper privileges\n" +
                           "3. Try: GRANT ALL PRIVILEGES ON payroll_management.* TO 'root'@'localhost';";
            } else if (e.getMessage().contains("Unknown database")) {
                errorMsg += "\n\nPossible solutions:\n" +
                           "1. Create the database: CREATE DATABASE payroll_management;\n" +
                           "2. Run the schema.sql file to create tables\n" +
                           "3. Check database name in DatabaseConfig.java";
            } else if (e.getMessage().contains("Connection refused")) {
                errorMsg += "\n\nPossible solutions:\n" +
                           "1. Start MySQL server\n" +
                           "2. Check if MySQL is running on port 3306\n" +
                           "3. Verify MySQL service is started";
            } else if (e.getMessage().contains("Communications link failure")) {
                errorMsg += "\n\nPossible solutions:\n" +
                           "1. Check network connectivity\n" +
                           "2. Verify MySQL server is running\n" +
                           "3. Check firewall settings\n" +
                           "4. Increase connection timeout";
            }
            
            throw new SQLException(errorMsg);
        }
    }
    
    /**
     * Get connection with retry mechanism
     * @param maxRetries Maximum number of retry attempts
     * @return Connection object
     * @throws SQLException if all retry attempts fail
     */
    public static Connection getConnectionWithRetry(int maxRetries) throws SQLException {
        SQLException lastException = null;
        
        for (int i = 0; i < maxRetries; i++) {
            try {
                return getConnection();
            } catch (SQLException e) {
                lastException = e;
                if (i < maxRetries - 1) {
                    try {
                        Thread.sleep(1000); // Wait 1 second before retry
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new SQLException("Connection retry interrupted", ie);
                    }
                }
            }
        }
        
        throw lastException;
    }
    
    /**
     * Close database connection safely
     */
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed successfully.");
            }
        } catch (SQLException e) {
            System.err.println("Error closing database connection: " + e.getMessage());
        }
    }
    
    /**
     * Close connection safely
     * @param conn Connection to close
     */
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                if (!conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }
    
    /**
     * Test database connection with detailed diagnostics
     * @return true if connection is successful, false otherwise
     */
    public static boolean testConnection() {
        Connection testConn = null;
        try {
            System.out.println("Testing database connection...");
            System.out.println("Database URL: " + DatabaseConfig.DB_URL);
            System.out.println("Username: " + DatabaseConfig.DB_USERNAME);
            
            testConn = getConnection();
            
            if (testConn != null && !testConn.isClosed()) {
                // Test basic query
                try (Statement stmt = testConn.createStatement()) {
                    stmt.executeQuery("SELECT 1").close();
                    System.out.println("Database connection test successful!");
                    return true;
                }
            }
            
            return false;
            
        } catch (SQLException e) {
            System.err.println("Database connection test failed: " + e.getMessage());
            return false;
        } finally {
            closeConnection(testConn);
        }
    }
    
    /**
     * Test if database exists
     * @return true if database exists, false otherwise
     */
    public static boolean testDatabaseExists() {
        try {
            // Try to connect to MySQL server without specifying database
            String serverUrl = DatabaseConfig.DB_URL.substring(0, DatabaseConfig.DB_URL.lastIndexOf('/'));
            
            Properties props = new Properties();
            props.setProperty("user", DatabaseConfig.DB_USERNAME);
            props.setProperty("password", DatabaseConfig.DB_PASSWORD);
            props.setProperty("useSSL", "false");
            props.setProperty("allowPublicKeyRetrieval", "true");
            
            try (Connection conn = DriverManager.getConnection(serverUrl, props);
                 Statement stmt = conn.createStatement()) {
                
                // Check if database exists
                stmt.executeQuery("SELECT SCHEMA_NAME FROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME = 'payroll_management'");
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error checking database existence: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Initialize database with schema if it doesn't exist
     * @return true if successful, false otherwise
     */
    public static boolean initializeDatabase() {
        try {
            if (!testDatabaseExists()) {
                System.out.println("Database does not exist. Please run the schema.sql file to create it.");
                return false;
            }
            return testConnection();
        } catch (Exception e) {
            System.err.println("Error initializing database: " + e.getMessage());
            return false;
        }
    }
}