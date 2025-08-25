package dao;

import models.Payroll;
import utils.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Payroll operations
 */
public class PayrollDAO {
    
    /**
     * Create a new payroll record
     * @param payroll Payroll object to create
     * @return true if successful, false otherwise
     */
    public boolean createPayroll(Payroll payroll) {
        String sql = "INSERT INTO payroll (employee_id, pay_period_start, pay_period_end, base_salary, " +
                    "overtime_hours, overtime_rate, overtime_pay, bonus, allowances, gross_salary, " +
                    "tax_deduction, insurance_deduction, other_deductions, total_deductions, net_salary, " +
                    "payment_date, payment_status, created_by) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, payroll.getEmployeeId());
            stmt.setDate(2, payroll.getPayPeriodStart());
            stmt.setDate(3, payroll.getPayPeriodEnd());
            stmt.setBigDecimal(4, payroll.getBaseSalary());
            stmt.setBigDecimal(5, payroll.getOvertimeHours());
            stmt.setBigDecimal(6, payroll.getOvertimeRate());
            stmt.setBigDecimal(7, payroll.getOvertimePay());
            stmt.setBigDecimal(8, payroll.getBonus());
            stmt.setBigDecimal(9, payroll.getAllowances());
            stmt.setBigDecimal(10, payroll.getGrossSalary());
            stmt.setBigDecimal(11, payroll.getTaxDeduction());
            stmt.setBigDecimal(12, payroll.getInsuranceDeduction());
            stmt.setBigDecimal(13, payroll.getOtherDeductions());
            stmt.setBigDecimal(14, payroll.getTotalDeductions());
            stmt.setBigDecimal(15, payroll.getNetSalary());
            stmt.setDate(16, payroll.getPaymentDate());
            stmt.setString(17, payroll.getPaymentStatus().toString());
            stmt.setInt(18, payroll.getCreatedBy());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    payroll.setPayrollId(generatedKeys.getInt(1));
                }
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error creating payroll: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Update an existing payroll record
     * @param payroll Payroll object to update
     * @return true if successful, false otherwise
     */
    public boolean updatePayroll(Payroll payroll) {
        String sql = "UPDATE payroll SET employee_id = ?, pay_period_start = ?, pay_period_end = ?, " +
                    "base_salary = ?, overtime_hours = ?, overtime_rate = ?, overtime_pay = ?, " +
                    "bonus = ?, allowances = ?, gross_salary = ?, tax_deduction = ?, " +
                    "insurance_deduction = ?, other_deductions = ?, total_deductions = ?, " +
                    "net_salary = ?, payment_date = ?, payment_status = ? WHERE payroll_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, payroll.getEmployeeId());
            stmt.setDate(2, payroll.getPayPeriodStart());
            stmt.setDate(3, payroll.getPayPeriodEnd());
            stmt.setBigDecimal(4, payroll.getBaseSalary());
            stmt.setBigDecimal(5, payroll.getOvertimeHours());
            stmt.setBigDecimal(6, payroll.getOvertimeRate());
            stmt.setBigDecimal(7, payroll.getOvertimePay());
            stmt.setBigDecimal(8, payroll.getBonus());
            stmt.setBigDecimal(9, payroll.getAllowances());
            stmt.setBigDecimal(10, payroll.getGrossSalary());
            stmt.setBigDecimal(11, payroll.getTaxDeduction());
            stmt.setBigDecimal(12, payroll.getInsuranceDeduction());
            stmt.setBigDecimal(13, payroll.getOtherDeductions());
            stmt.setBigDecimal(14, payroll.getTotalDeductions());
            stmt.setBigDecimal(15, payroll.getNetSalary());
            stmt.setDate(16, payroll.getPaymentDate());
            stmt.setString(17, payroll.getPaymentStatus().toString());
            stmt.setInt(18, payroll.getPayrollId());
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating payroll: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Delete a payroll record
     * @param payrollId Payroll ID to delete
     * @return true if successful, false otherwise
     */
    public boolean deletePayroll(int payrollId) {
        String sql = "DELETE FROM payroll WHERE payroll_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, payrollId);
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting payroll: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Get payroll by ID
     * @param payrollId Payroll ID
     * @return Payroll object if found, null otherwise
     */
    public Payroll getPayrollById(int payrollId) {
        String sql = "SELECT p.*, e.employee_code, CONCAT(e.first_name, ' ', e.last_name) as employee_name " +
                    "FROM payroll p " +
                    "JOIN employees e ON p.employee_id = e.employee_id " +
                    "WHERE p.payroll_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, payrollId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToPayroll(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting payroll by ID: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Get all payroll records
     * @return List of all payroll records
     */
    public List<Payroll> getAllPayrolls() {
        List<Payroll> payrolls = new ArrayList<>();
        String sql = "SELECT p.*, e.employee_code, CONCAT(e.first_name, ' ', e.last_name) as employee_name " +
                    "FROM payroll p " +
                    "JOIN employees e ON p.employee_id = e.employee_id " +
                    "ORDER BY p.pay_period_end DESC, e.first_name";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                payrolls.add(mapResultSetToPayroll(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting all payrolls: " + e.getMessage());
        }
        
        return payrolls;
    }
    
    /**
     * Get payroll records by employee
     * @param employeeId Employee ID
     * @return List of payroll records for the employee
     */
    public List<Payroll> getPayrollsByEmployee(int employeeId) {
        List<Payroll> payrolls = new ArrayList<>();
        String sql = "SELECT p.*, e.employee_code, CONCAT(e.first_name, ' ', e.last_name) as employee_name " +
                    "FROM payroll p " +
                    "JOIN employees e ON p.employee_id = e.employee_id " +
                    "WHERE p.employee_id = ? ORDER BY p.pay_period_end DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, employeeId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                payrolls.add(mapResultSetToPayroll(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting payrolls by employee: " + e.getMessage());
        }
        
        return payrolls;
    }
    
    /**
     * Get payroll records by date range
     * @param startDate Start date
     * @param endDate End date
     * @return List of payroll records within the date range
     */
    public List<Payroll> getPayrollsByDateRange(Date startDate, Date endDate) {
        List<Payroll> payrolls = new ArrayList<>();
        String sql = "SELECT p.*, e.employee_code, CONCAT(e.first_name, ' ', e.last_name) as employee_name " +
                    "FROM payroll p " +
                    "JOIN employees e ON p.employee_id = e.employee_id " +
                    "WHERE p.pay_period_start >= ? AND p.pay_period_end <= ? " +
                    "ORDER BY p.pay_period_end DESC, e.first_name";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, startDate);
            stmt.setDate(2, endDate);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                payrolls.add(mapResultSetToPayroll(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting payrolls by date range: " + e.getMessage());
        }
        
        return payrolls;
    }
    
    /**
     * Get payroll records by payment status
     * @param status Payment status
     * @return List of payroll records with the specified status
     */
    public List<Payroll> getPayrollsByStatus(Payroll.PaymentStatus status) {
        List<Payroll> payrolls = new ArrayList<>();
        String sql = "SELECT p.*, e.employee_code, CONCAT(e.first_name, ' ', e.last_name) as employee_name " +
                    "FROM payroll p " +
                    "JOIN employees e ON p.employee_id = e.employee_id " +
                    "WHERE p.payment_status = ? ORDER BY p.pay_period_end DESC, e.first_name";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status.toString());
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                payrolls.add(mapResultSetToPayroll(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting payrolls by status: " + e.getMessage());
        }
        
        return payrolls;
    }
    
    /**
     * Update payment status
     * @param payrollId Payroll ID
     * @param status New payment status
     * @param paymentDate Payment date (can be null)
     * @return true if successful, false otherwise
     */
    public boolean updatePaymentStatus(int payrollId, Payroll.PaymentStatus status, Date paymentDate) {
        String sql = "UPDATE payroll SET payment_status = ?, payment_date = ? WHERE payroll_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status.toString());
            stmt.setDate(2, paymentDate);
            stmt.setInt(3, payrollId);
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating payment status: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Check if payroll exists for employee in the given period
     * @param employeeId Employee ID
     * @param startDate Period start date
     * @param endDate Period end date
     * @param excludePayrollId Payroll ID to exclude from check (for updates)
     * @return true if payroll exists, false otherwise
     */
    public boolean payrollExistsForPeriod(int employeeId, Date startDate, Date endDate, int excludePayrollId) {
        String sql = "SELECT COUNT(*) FROM payroll WHERE employee_id = ? AND " +
                    "((pay_period_start <= ? AND pay_period_end >= ?) OR " +
                    "(pay_period_start <= ? AND pay_period_end >= ?) OR " +
                    "(pay_period_start >= ? AND pay_period_end <= ?)) AND payroll_id != ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, employeeId);
            stmt.setDate(2, startDate);
            stmt.setDate(3, startDate);
            stmt.setDate(4, endDate);
            stmt.setDate(5, endDate);
            stmt.setDate(6, startDate);
            stmt.setDate(7, endDate);
            stmt.setInt(8, excludePayrollId);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("Error checking payroll period existence: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Map ResultSet to Payroll object
     * @param rs ResultSet
     * @return Payroll object
     * @throws SQLException if SQL error occurs
     */
    private Payroll mapResultSetToPayroll(ResultSet rs) throws SQLException {
        Payroll payroll = new Payroll();
        payroll.setPayrollId(rs.getInt("payroll_id"));
        payroll.setEmployeeId(rs.getInt("employee_id"));
        payroll.setEmployeeCode(rs.getString("employee_code"));
        payroll.setEmployeeName(rs.getString("employee_name"));
        payroll.setPayPeriodStart(rs.getDate("pay_period_start"));
        payroll.setPayPeriodEnd(rs.getDate("pay_period_end"));
        payroll.setBaseSalary(rs.getBigDecimal("base_salary"));
        payroll.setOvertimeHours(rs.getBigDecimal("overtime_hours"));
        payroll.setOvertimeRate(rs.getBigDecimal("overtime_rate"));
        payroll.setOvertimePay(rs.getBigDecimal("overtime_pay"));
        payroll.setBonus(rs.getBigDecimal("bonus"));
        payroll.setAllowances(rs.getBigDecimal("allowances"));
        payroll.setGrossSalary(rs.getBigDecimal("gross_salary"));
        payroll.setTaxDeduction(rs.getBigDecimal("tax_deduction"));
        payroll.setInsuranceDeduction(rs.getBigDecimal("insurance_deduction"));
        payroll.setOtherDeductions(rs.getBigDecimal("other_deductions"));
        payroll.setTotalDeductions(rs.getBigDecimal("total_deductions"));
        payroll.setNetSalary(rs.getBigDecimal("net_salary"));
        payroll.setPaymentDate(rs.getDate("payment_date"));
        payroll.setPaymentStatus(Payroll.PaymentStatus.valueOf(rs.getString("payment_status")));
        payroll.setCreatedBy(rs.getInt("created_by"));
        payroll.setCreatedAt(rs.getTimestamp("created_at"));
        return payroll;
    }
}