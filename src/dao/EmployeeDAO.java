package dao;

import models.Employee;
import utils.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Employee operations
 */
public class EmployeeDAO {
    
    /**
     * Create a new employee
     * @param employee Employee object to create
     * @return true if successful, false otherwise
     */
    public boolean createEmployee(Employee employee) {
        String sql = "INSERT INTO employees (employee_code, first_name, last_name, email, phone, address, " +
                    "date_of_birth, hire_date, department_id, position, base_salary, employment_status) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, employee.getEmployeeCode());
            stmt.setString(2, employee.getFirstName());
            stmt.setString(3, employee.getLastName());
            stmt.setString(4, employee.getEmail());
            stmt.setString(5, employee.getPhone());
            stmt.setString(6, employee.getAddress());
            stmt.setDate(7, employee.getDateOfBirth());
            stmt.setDate(8, employee.getHireDate());
            stmt.setInt(9, employee.getDepartmentId());
            stmt.setString(10, employee.getPosition());
            stmt.setBigDecimal(11, employee.getBaseSalary());
            stmt.setString(12, employee.getEmploymentStatus().toString());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    employee.setEmployeeId(generatedKeys.getInt(1));
                }
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error creating employee: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to create employee: " + e.getMessage(), e);
        }
        
        return false;
    }
    
    /**
     * Update an existing employee
     * @param employee Employee object to update
     * @return true if successful, false otherwise
     */
    public boolean updateEmployee(Employee employee) {
        String sql = "UPDATE employees SET employee_code = ?, first_name = ?, last_name = ?, email = ?, " +
                    "phone = ?, address = ?, date_of_birth = ?, hire_date = ?, department_id = ?, " +
                    "position = ?, base_salary = ?, employment_status = ? WHERE employee_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, employee.getEmployeeCode());
            stmt.setString(2, employee.getFirstName());
            stmt.setString(3, employee.getLastName());
            stmt.setString(4, employee.getEmail());
            stmt.setString(5, employee.getPhone());
            stmt.setString(6, employee.getAddress());
            stmt.setDate(7, employee.getDateOfBirth());
            stmt.setDate(8, employee.getHireDate());
            stmt.setInt(9, employee.getDepartmentId());
            stmt.setString(10, employee.getPosition());
            stmt.setBigDecimal(11, employee.getBaseSalary());
            stmt.setString(12, employee.getEmploymentStatus().toString());
            stmt.setInt(13, employee.getEmployeeId());
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating employee: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to update employee: " + e.getMessage(), e);
        }
    }
    
    /**
     * Delete an employee (soft delete - set status to TERMINATED)
     * @param employeeId Employee ID to delete
     * @return true if successful, false otherwise
     */
    public boolean deleteEmployee(int employeeId) {
        String sql = "UPDATE employees SET employment_status = 'TERMINATED' WHERE employee_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, employeeId);
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting employee: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Get employee by ID
     * @param employeeId Employee ID
     * @return Employee object if found, null otherwise
     */
    public Employee getEmployeeById(int employeeId) {
        String sql = "SELECT e.*, d.department_name FROM employees e " +
                    "LEFT JOIN departments d ON e.department_id = d.department_id " +
                    "WHERE e.employee_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, employeeId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToEmployee(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting employee by ID: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Get all active employees
     * @return List of active employees
     */
    public List<Employee> getAllEmployees() {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT e.*, d.department_name FROM employees e " +
                    "LEFT JOIN departments d ON e.department_id = d.department_id " +
                    "WHERE e.employment_status = 'ACTIVE' ORDER BY e.first_name, e.last_name";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                employees.add(mapResultSetToEmployee(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting all employees: " + e.getMessage());
        }
        
        return employees;
    }
    
    /**
     * Search employees by various criteria
     * @param searchTerm Search term
     * @return List of matching employees
     */
    public List<Employee> searchEmployees(String searchTerm) {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT e.*, d.department_name FROM employees e " +
                    "LEFT JOIN departments d ON e.department_id = d.department_id " +
                    "WHERE e.employment_status = 'ACTIVE' AND " +
                    "(e.employee_code LIKE ? OR e.first_name LIKE ? OR e.last_name LIKE ? OR " +
                    "e.email LIKE ? OR e.position LIKE ? OR d.department_name LIKE ?) " +
                    "ORDER BY e.first_name, e.last_name";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + searchTerm + "%";
            for (int i = 1; i <= 6; i++) {
                stmt.setString(i, searchPattern);
            }
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                employees.add(mapResultSetToEmployee(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error searching employees: " + e.getMessage());
        }
        
        return employees;
    }
    
    /**
     * Get employees by department
     * @param departmentId Department ID
     * @return List of employees in the department
     */
    public List<Employee> getEmployeesByDepartment(int departmentId) {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT e.*, d.department_name FROM employees e " +
                    "LEFT JOIN departments d ON e.department_id = d.department_id " +
                    "WHERE e.department_id = ? AND e.employment_status = 'ACTIVE' " +
                    "ORDER BY e.first_name, e.last_name";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, departmentId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                employees.add(mapResultSetToEmployee(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting employees by department: " + e.getMessage());
        }
        
        return employees;
    }
    
    /**
     * Check if employee code already exists
     * @param employeeCode Employee code to check
     * @param excludeEmployeeId Employee ID to exclude from check (for updates)
     * @return true if code exists, false otherwise
     */
    public boolean employeeCodeExists(String employeeCode, int excludeEmployeeId) {
        String sql = "SELECT COUNT(*) FROM employees WHERE employee_code = ? AND employee_id != ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, employeeCode);
            stmt.setInt(2, excludeEmployeeId);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("Error checking employee code existence: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Check if email already exists
     * @param email Email to check
     * @param excludeEmployeeId Employee ID to exclude from check (for updates)
     * @return true if email exists, false otherwise
     */
    public boolean emailExists(String email, int excludeEmployeeId) {
        String sql = "SELECT COUNT(*) FROM employees WHERE email = ? AND employee_id != ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            stmt.setInt(2, excludeEmployeeId);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("Error checking email existence: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Get employee statistics
     * @return Array containing [total employees, active employees, inactive employees, terminated employees]
     */
    public int[] getEmployeeStatistics() {
        int[] stats = new int[4];
        String sql = "SELECT COUNT(*) as total, " +
                    "SUM(CASE WHEN employment_status = 'ACTIVE' THEN 1 ELSE 0 END) as active, " +
                    "SUM(CASE WHEN employment_status = 'INACTIVE' THEN 1 ELSE 0 END) as inactive, " +
                    "SUM(CASE WHEN employment_status = 'TERMINATED' THEN 1 ELSE 0 END) as `terminated` " +
                    "FROM employees";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                stats[0] = rs.getInt("total");
                stats[1] = rs.getInt("active");
                stats[2] = rs.getInt("inactive");
                stats[3] = rs.getInt("terminated");
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting employee statistics: " + e.getMessage());
        }
        
        return stats;
    }
    
    /**
     * Map ResultSet to Employee object
     * @param rs ResultSet
     * @return Employee object
     * @throws SQLException if SQL error occurs
     */
    private Employee mapResultSetToEmployee(ResultSet rs) throws SQLException {
        Employee employee = new Employee();
        employee.setEmployeeId(rs.getInt("employee_id"));
        employee.setEmployeeCode(rs.getString("employee_code"));
        employee.setFirstName(rs.getString("first_name"));
        employee.setLastName(rs.getString("last_name"));
        employee.setEmail(rs.getString("email"));
        employee.setPhone(rs.getString("phone"));
        employee.setAddress(rs.getString("address"));
        employee.setDateOfBirth(rs.getDate("date_of_birth"));
        employee.setHireDate(rs.getDate("hire_date"));
        employee.setDepartmentId(rs.getInt("department_id"));
        employee.setDepartmentName(rs.getString("department_name"));
        employee.setPosition(rs.getString("position"));
        employee.setBaseSalary(rs.getBigDecimal("base_salary"));
        employee.setEmploymentStatus(Employee.EmploymentStatus.valueOf(rs.getString("employment_status")));
        employee.setCreatedAt(rs.getTimestamp("created_at"));
        employee.setUpdatedAt(rs.getTimestamp("updated_at"));
        return employee;
    }
}