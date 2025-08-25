package models;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

/**
 * Employee model class
 */
public class Employee {
    private int employeeId;
    private String employeeCode;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String address;
    private Date dateOfBirth;
    private Date hireDate;
    private int departmentId;
    private String departmentName; // For display purposes
    private String position;
    private BigDecimal baseSalary;
    private EmploymentStatus employmentStatus;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    
    // Enum for employment status
    public enum EmploymentStatus {
        ACTIVE, INACTIVE, TERMINATED
    }
    
    // Constructors
    public Employee() {}
    
    public Employee(String employeeCode, String firstName, String lastName, String email, 
                   Date hireDate, int departmentId, String position, BigDecimal baseSalary) {
        this.employeeCode = employeeCode;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.hireDate = hireDate;
        this.departmentId = departmentId;
        this.position = position;
        this.baseSalary = baseSalary;
        this.employmentStatus = EmploymentStatus.ACTIVE;
    }
    
    // Getters and Setters
    public int getEmployeeId() {
        return employeeId;
    }
    
    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }
    
    public String getEmployeeCode() {
        return employeeCode;
    }
    
    public void setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public Date getDateOfBirth() {
        return dateOfBirth;
    }
    
    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
    
    public Date getHireDate() {
        return hireDate;
    }
    
    public void setHireDate(Date hireDate) {
        this.hireDate = hireDate;
    }
    
    public int getDepartmentId() {
        return departmentId;
    }
    
    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }
    
    public String getDepartmentName() {
        return departmentName;
    }
    
    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }
    
    public String getPosition() {
        return position;
    }
    
    public void setPosition(String position) {
        this.position = position;
    }
    
    public BigDecimal getBaseSalary() {
        return baseSalary;
    }
    
    public void setBaseSalary(BigDecimal baseSalary) {
        this.baseSalary = baseSalary;
    }
    
    public EmploymentStatus getEmploymentStatus() {
        return employmentStatus;
    }
    
    public void setEmploymentStatus(EmploymentStatus employmentStatus) {
        this.employmentStatus = employmentStatus;
    }
    
    public Timestamp getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
    
    public Timestamp getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    @Override
    public String toString() {
        return employeeCode + " - " + getFullName();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Employee employee = (Employee) obj;
        return employeeId == employee.employeeId;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(employeeId);
    }
}