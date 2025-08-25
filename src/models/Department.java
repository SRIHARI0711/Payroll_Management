package models;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Department model class
 */
public class Department {
    private int departmentId;
    private String departmentName;
    private String departmentCode;
    private String managerName;
    private BigDecimal budget;
    private Timestamp createdAt;
    private boolean isActive;
    
    // Constructors
    public Department() {}
    
    public Department(String departmentName, String departmentCode, String managerName, BigDecimal budget) {
        this.departmentName = departmentName;
        this.departmentCode = departmentCode;
        this.managerName = managerName;
        this.budget = budget;
        this.isActive = true;
    }
    
    public Department(int departmentId, String departmentName, String departmentCode, String managerName, BigDecimal budget) {
        this.departmentId = departmentId;
        this.departmentName = departmentName;
        this.departmentCode = departmentCode;
        this.managerName = managerName;
        this.budget = budget;
        this.isActive = true;
    }
    
    // Getters and Setters
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
    
    public String getDepartmentCode() {
        return departmentCode;
    }
    
    public void setDepartmentCode(String departmentCode) {
        this.departmentCode = departmentCode;
    }
    
    public String getManagerName() {
        return managerName;
    }
    
    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }
    
    public BigDecimal getBudget() {
        return budget;
    }
    
    public void setBudget(BigDecimal budget) {
        this.budget = budget;
    }
    
    public Timestamp getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public void setActive(boolean active) {
        isActive = active;
    }
    
    @Override
    public String toString() {
        return departmentName + " (" + departmentCode + ")";
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Department that = (Department) obj;
        return departmentId == that.departmentId;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(departmentId);
    }
}