package utils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Database validation utility to check database structure and data integrity
 */
public class DatabaseValidator {
    
    /**
     * Validate database structure and return list of issues
     * @return List of validation issues (empty if all good)
     */
    public static List<String> validateDatabase() {
        List<String> issues = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            
            // Check if required tables exist
            String[] requiredTables = {"users", "departments", "employees", "payroll"};
            for (String table : requiredTables) {
                if (!tableExists(conn, table)) {
                    issues.add("Required table '" + table + "' does not exist");
                }
            }
            
            // Check if admin user exists
            if (!adminUserExists(conn)) {
                issues.add("Default admin user does not exist");
            }
            
            // Check foreign key constraints
            if (!checkForeignKeys(conn)) {
                issues.add("Foreign key constraints are not properly set up");
            }
            
            // Check for sample data
            if (!hasSampleData(conn)) {
                issues.add("No sample data found - database may be empty");
            }
            
        } catch (SQLException e) {
            issues.add("Database connection error: " + e.getMessage());
        }
        
        return issues;
    }
    
    /**
     * Check if a table exists
     */
    private static boolean tableExists(Connection conn, String tableName) {
        try {
            DatabaseMetaData meta = conn.getMetaData();
            ResultSet rs = meta.getTables(null, null, tableName, new String[]{"TABLE"});
            return rs.next();
        } catch (SQLException e) {
            return false;
        }
    }
    
    /**
     * Check if admin user exists
     */
    private static boolean adminUserExists(Connection conn) {
        try (PreparedStatement stmt = conn.prepareStatement(
                "SELECT COUNT(*) FROM users WHERE username = 'admin'")) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            // Table might not exist
        }
        return false;
    }
    
    /**
     * Check foreign key constraints
     */
    private static boolean checkForeignKeys(Connection conn) {
        try {
            // Check if employees can reference departments
            try (PreparedStatement stmt = conn.prepareStatement(
                    "SELECT COUNT(*) FROM employees e LEFT JOIN departments d ON e.department_id = d.department_id WHERE e.department_id IS NOT NULL AND d.department_id IS NULL")) {
                ResultSet rs = stmt.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    return false; // Found orphaned records
                }
            }
            
            // Check if payroll can reference employees
            try (PreparedStatement stmt = conn.prepareStatement(
                    "SELECT COUNT(*) FROM payroll p LEFT JOIN employees e ON p.employee_id = e.employee_id WHERE e.employee_id IS NULL")) {
                ResultSet rs = stmt.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    return false; // Found orphaned records
                }
            }
            
            return true;
        } catch (SQLException e) {
            return false;
        }
    }
    
    /**
     * Check if sample data exists
     */
    private static boolean hasSampleData(Connection conn) {
        try {
            // Check if there are any departments
            try (PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM departments")) {
                ResultSet rs = stmt.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    return true;
                }
            }
        } catch (SQLException e) {
            // Table might not exist
        }
        return false;
    }
    
    /**
     * Fix common database issues
     * @return List of fixes applied
     */
    public static List<String> fixCommonIssues() {
        List<String> fixes = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            
            // Create admin user if it doesn't exist
            if (!adminUserExists(conn)) {
                try (PreparedStatement stmt = conn.prepareStatement(
                        "INSERT INTO users (username, password, role, full_name, email) VALUES (?, ?, ?, ?, ?)")) {
                    stmt.setString(1, "admin");
                    stmt.setString(2, "admin123");
                    stmt.setString(3, "ADMIN");
                    stmt.setString(4, "System Administrator");
                    stmt.setString(5, "admin@company.com");
                    stmt.executeUpdate();
                    fixes.add("Created default admin user");
                }
            }
            
            // Add sample departments if none exist
            if (!hasSampleData(conn)) {
                try (PreparedStatement stmt = conn.prepareStatement(
                        "INSERT INTO departments (department_name, department_code, manager_name, budget) VALUES (?, ?, ?, ?)")) {
                    
                    String[][] departments = {
                        {"Human Resources", "HR", "John Smith", "500000.00"},
                        {"Information Technology", "IT", "Jane Doe", "1000000.00"},
                        {"Finance", "FIN", "Mike Johnson", "750000.00"},
                        {"Marketing", "MKT", "Sarah Wilson", "600000.00"}
                    };
                    
                    for (String[] dept : departments) {
                        stmt.setString(1, dept[0]);
                        stmt.setString(2, dept[1]);
                        stmt.setString(3, dept[2]);
                        stmt.setBigDecimal(4, new java.math.BigDecimal(dept[3]));
                        stmt.addBatch();
                    }
                    
                    stmt.executeBatch();
                    fixes.add("Added sample departments");
                }
            }
            
        } catch (SQLException e) {
            fixes.add("Error applying fixes: " + e.getMessage());
        }
        
        return fixes;
    }
    
    /**
     * Get database statistics
     * @return Database statistics as formatted string
     */
    public static String getDatabaseStats() {
        StringBuilder stats = new StringBuilder();
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            
            // Count records in each table
            String[] tables = {"users", "departments", "employees", "payroll"};
            for (String table : tables) {
                try (PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM " + table)) {
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        stats.append(table).append(": ").append(rs.getInt(1)).append(" records\n");
                    }
                } catch (SQLException e) {
                    stats.append(table).append(": Table not found\n");
                }
            }
            
            // Database version
            try (Statement stmt = conn.createStatement()) {
                ResultSet rs = stmt.executeQuery("SELECT VERSION()");
                if (rs.next()) {
                    stats.append("MySQL Version: ").append(rs.getString(1)).append("\n");
                }
            }
            
        } catch (SQLException e) {
            stats.append("Error getting database stats: ").append(e.getMessage());
        }
        
        return stats.toString();
    }
}