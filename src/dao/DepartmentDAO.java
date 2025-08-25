package dao;

import models.Department;
import utils.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Department operations
 */
public class DepartmentDAO {
    
    /**
     * Create a new department
     * @param department Department object to create
     * @return true if successful, false otherwise
     */
    public boolean createDepartment(Department department) {
        String sql = "INSERT INTO departments (department_name, department_code, manager_name, budget, is_active) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, department.getDepartmentName());
            stmt.setString(2, department.getDepartmentCode());
            stmt.setString(3, department.getManagerName());
            stmt.setBigDecimal(4, department.getBudget());
            stmt.setBoolean(5, department.isActive());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    department.setDepartmentId(generatedKeys.getInt(1));
                }
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error creating department: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Update an existing department
     * @param department Department object to update
     * @return true if successful, false otherwise
     */
    public boolean updateDepartment(Department department) {
        String sql = "UPDATE departments SET department_name = ?, department_code = ?, manager_name = ?, budget = ?, is_active = ? WHERE department_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, department.getDepartmentName());
            stmt.setString(2, department.getDepartmentCode());
            stmt.setString(3, department.getManagerName());
            stmt.setBigDecimal(4, department.getBudget());
            stmt.setBoolean(5, department.isActive());
            stmt.setInt(6, department.getDepartmentId());
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating department: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Delete a department (soft delete - set inactive)
     * @param departmentId Department ID to delete
     * @return true if successful, false otherwise
     */
    public boolean deleteDepartment(int departmentId) {
        String sql = "UPDATE departments SET is_active = FALSE WHERE department_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, departmentId);
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting department: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Get department by ID
     * @param departmentId Department ID
     * @return Department object if found, null otherwise
     */
    public Department getDepartmentById(int departmentId) {
        String sql = "SELECT * FROM departments WHERE department_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, departmentId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToDepartment(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting department by ID: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Get all active departments
     * @return List of active departments
     */
    public List<Department> getAllDepartments() {
        List<Department> departments = new ArrayList<>();
        String sql = "SELECT * FROM departments WHERE is_active = TRUE ORDER BY department_name";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                departments.add(mapResultSetToDepartment(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting all departments: " + e.getMessage());
        }
        
        return departments;
    }
    
    /**
     * Search departments by name or code
     * @param searchTerm Search term
     * @return List of matching departments
     */
    public List<Department> searchDepartments(String searchTerm) {
        List<Department> departments = new ArrayList<>();
        String sql = "SELECT * FROM departments WHERE is_active = TRUE AND " +
                    "(department_name LIKE ? OR department_code LIKE ?) ORDER BY department_name";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + searchTerm + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                departments.add(mapResultSetToDepartment(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error searching departments: " + e.getMessage());
        }
        
        return departments;
    }
    
    /**
     * Check if department code already exists
     * @param departmentCode Department code to check
     * @param excludeDepartmentId Department ID to exclude from check (for updates)
     * @return true if code exists, false otherwise
     */
    public boolean departmentCodeExists(String departmentCode, int excludeDepartmentId) {
        String sql = "SELECT COUNT(*) FROM departments WHERE department_code = ? AND department_id != ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, departmentCode);
            stmt.setInt(2, excludeDepartmentId);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("Error checking department code existence: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Get employee count for a department
     * @param departmentId Department ID
     * @return Number of employees in the department
     */
    public int getEmployeeCount(int departmentId) {
        String sql = "SELECT COUNT(*) FROM employees WHERE department_id = ? AND employment_status = 'ACTIVE'";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, departmentId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting employee count: " + e.getMessage());
        }
        
        return 0;
    }
    
    /**
     * Map ResultSet to Department object
     * @param rs ResultSet
     * @return Department object
     * @throws SQLException if SQL error occurs
     */
    private Department mapResultSetToDepartment(ResultSet rs) throws SQLException {
        Department department = new Department();
        department.setDepartmentId(rs.getInt("department_id"));
        department.setDepartmentName(rs.getString("department_name"));
        department.setDepartmentCode(rs.getString("department_code"));
        department.setManagerName(rs.getString("manager_name"));
        department.setBudget(rs.getBigDecimal("budget"));
        department.setCreatedAt(rs.getTimestamp("created_at"));
        department.setActive(rs.getBoolean("is_active"));
        return department;
    }
}