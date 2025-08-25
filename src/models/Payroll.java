package models;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

/**
 * Payroll model class
 */
public class Payroll {
    private int payrollId;
    private int employeeId;
    private String employeeName; // For display purposes
    private String employeeCode; // For display purposes
    private Date payPeriodStart;
    private Date payPeriodEnd;
    private BigDecimal baseSalary;
    private BigDecimal overtimeHours;
    private BigDecimal overtimeRate;
    private BigDecimal overtimePay;
    private BigDecimal bonus;
    private BigDecimal allowances;
    private BigDecimal grossSalary;
    private BigDecimal taxDeduction;
    private BigDecimal insuranceDeduction;
    private BigDecimal otherDeductions;
    private BigDecimal totalDeductions;
    private BigDecimal netSalary;
    private Date paymentDate;
    private PaymentStatus paymentStatus;
    private int createdBy;
    private Timestamp createdAt;
    
    // Enum for payment status
    public enum PaymentStatus {
        PENDING, PAID, CANCELLED
    }
    
    // Constructors
    public Payroll() {}
    
    public Payroll(int employeeId, Date payPeriodStart, Date payPeriodEnd, BigDecimal baseSalary) {
        this.employeeId = employeeId;
        this.payPeriodStart = payPeriodStart;
        this.payPeriodEnd = payPeriodEnd;
        this.baseSalary = baseSalary;
        this.overtimeHours = BigDecimal.ZERO;
        this.overtimeRate = new BigDecimal("1.5");
        this.overtimePay = BigDecimal.ZERO;
        this.bonus = BigDecimal.ZERO;
        this.allowances = BigDecimal.ZERO;
        this.taxDeduction = BigDecimal.ZERO;
        this.insuranceDeduction = BigDecimal.ZERO;
        this.otherDeductions = BigDecimal.ZERO;
        this.paymentStatus = PaymentStatus.PENDING;
        calculateSalary();
    }
    
    // Calculate gross salary, total deductions, and net salary
    public void calculateSalary() {
        // Calculate overtime pay
        this.overtimePay = this.overtimeHours.multiply(this.baseSalary.divide(new BigDecimal("160")))
                                           .multiply(this.overtimeRate);
        
        // Calculate gross salary
        this.grossSalary = this.baseSalary.add(this.overtimePay).add(this.bonus).add(this.allowances);
        
        // Calculate total deductions
        this.totalDeductions = this.taxDeduction.add(this.insuranceDeduction).add(this.otherDeductions);
        
        // Calculate net salary
        this.netSalary = this.grossSalary.subtract(this.totalDeductions);
    }
    
    // Getters and Setters
    public int getPayrollId() {
        return payrollId;
    }
    
    public void setPayrollId(int payrollId) {
        this.payrollId = payrollId;
    }
    
    public int getEmployeeId() {
        return employeeId;
    }
    
    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }
    
    public String getEmployeeName() {
        return employeeName;
    }
    
    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }
    
    public String getEmployeeCode() {
        return employeeCode;
    }
    
    public void setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode;
    }
    
    public Date getPayPeriodStart() {
        return payPeriodStart;
    }
    
    public void setPayPeriodStart(Date payPeriodStart) {
        this.payPeriodStart = payPeriodStart;
    }
    
    public Date getPayPeriodEnd() {
        return payPeriodEnd;
    }
    
    public void setPayPeriodEnd(Date payPeriodEnd) {
        this.payPeriodEnd = payPeriodEnd;
    }
    
    public BigDecimal getBaseSalary() {
        return baseSalary;
    }
    
    public void setBaseSalary(BigDecimal baseSalary) {
        this.baseSalary = baseSalary;
    }
    
    public BigDecimal getOvertimeHours() {
        return overtimeHours;
    }
    
    public void setOvertimeHours(BigDecimal overtimeHours) {
        this.overtimeHours = overtimeHours;
    }
    
    public BigDecimal getOvertimeRate() {
        return overtimeRate;
    }
    
    public void setOvertimeRate(BigDecimal overtimeRate) {
        this.overtimeRate = overtimeRate;
    }
    
    public BigDecimal getOvertimePay() {
        return overtimePay;
    }
    
    public void setOvertimePay(BigDecimal overtimePay) {
        this.overtimePay = overtimePay;
    }
    
    public BigDecimal getBonus() {
        return bonus;
    }
    
    public void setBonus(BigDecimal bonus) {
        this.bonus = bonus;
    }
    
    public BigDecimal getAllowances() {
        return allowances;
    }
    
    public void setAllowances(BigDecimal allowances) {
        this.allowances = allowances;
    }
    
    public BigDecimal getGrossSalary() {
        return grossSalary;
    }
    
    public void setGrossSalary(BigDecimal grossSalary) {
        this.grossSalary = grossSalary;
    }
    
    public BigDecimal getTaxDeduction() {
        return taxDeduction;
    }
    
    public void setTaxDeduction(BigDecimal taxDeduction) {
        this.taxDeduction = taxDeduction;
    }
    
    public BigDecimal getInsuranceDeduction() {
        return insuranceDeduction;
    }
    
    public void setInsuranceDeduction(BigDecimal insuranceDeduction) {
        this.insuranceDeduction = insuranceDeduction;
    }
    
    public BigDecimal getOtherDeductions() {
        return otherDeductions;
    }
    
    public void setOtherDeductions(BigDecimal otherDeductions) {
        this.otherDeductions = otherDeductions;
    }
    
    public BigDecimal getTotalDeductions() {
        return totalDeductions;
    }
    
    public void setTotalDeductions(BigDecimal totalDeductions) {
        this.totalDeductions = totalDeductions;
    }
    
    public BigDecimal getNetSalary() {
        return netSalary;
    }
    
    public void setNetSalary(BigDecimal netSalary) {
        this.netSalary = netSalary;
    }
    
    public Date getPaymentDate() {
        return paymentDate;
    }
    
    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }
    
    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }
    
    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
    
    public int getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }
    
    public Timestamp getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
    
    @Override
    public String toString() {
        return "Payroll{" +
                "payrollId=" + payrollId +
                ", employeeCode='" + employeeCode + '\'' +
                ", employeeName='" + employeeName + '\'' +
                ", payPeriod=" + payPeriodStart + " to " + payPeriodEnd +
                ", netSalary=" + netSalary +
                ", status=" + paymentStatus +
                '}';
    }
}