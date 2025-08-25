-- Payroll Management System Database Schema

-- Drop database if exists and create fresh
DROP DATABASE IF EXISTS payroll_management;
CREATE DATABASE payroll_management CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE payroll_management;

-- Set SQL mode to handle strict mode issues
SET sql_mode = 'STRICT_TRANS_TABLES,NO_ZERO_DATE,NO_ZERO_IN_DATE,ERROR_FOR_DIVISION_BY_ZERO';

-- Users table for authentication
CREATE TABLE users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role ENUM('ADMIN', 'HR') NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    INDEX idx_username (username),
    INDEX idx_active (is_active)
) ENGINE=InnoDB;

-- Departments table
CREATE TABLE departments (
    department_id INT PRIMARY KEY AUTO_INCREMENT,
    department_name VARCHAR(100) NOT NULL,
    department_code VARCHAR(10) UNIQUE NOT NULL,
    manager_name VARCHAR(100) NULL,
    budget DECIMAL(15,2) NULL DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    INDEX idx_dept_code (department_code),
    INDEX idx_dept_active (is_active)
) ENGINE=InnoDB;

-- Employees table
CREATE TABLE employees (
    employee_id INT PRIMARY KEY AUTO_INCREMENT,
    employee_code VARCHAR(20) UNIQUE NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(20) NULL,
    address TEXT NULL,
    date_of_birth DATE NULL,
    hire_date DATE NOT NULL,
    department_id INT NULL,
    position VARCHAR(100) NULL,
    base_salary DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    employment_status ENUM('ACTIVE', 'INACTIVE', 'TERMINATED') DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_emp_code (employee_code),
    INDEX idx_emp_email (email),
    INDEX idx_emp_status (employment_status),
    INDEX idx_emp_dept (department_id),
    INDEX idx_emp_name (first_name, last_name),
    CONSTRAINT fk_emp_department 
        FOREIGN KEY (department_id) REFERENCES departments(department_id)
        ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB;

-- Payroll table
CREATE TABLE payroll (
    payroll_id INT PRIMARY KEY AUTO_INCREMENT,
    employee_id INT NOT NULL,
    pay_period_start DATE NOT NULL,
    pay_period_end DATE NOT NULL,
    base_salary DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    overtime_hours DECIMAL(6,2) DEFAULT 0.00,
    overtime_rate DECIMAL(6,2) DEFAULT 1.50,
    overtime_pay DECIMAL(12,2) DEFAULT 0.00,
    bonus DECIMAL(12,2) DEFAULT 0.00,
    allowances DECIMAL(12,2) DEFAULT 0.00,
    gross_salary DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    tax_deduction DECIMAL(12,2) DEFAULT 0.00,
    insurance_deduction DECIMAL(12,2) DEFAULT 0.00,
    other_deductions DECIMAL(12,2) DEFAULT 0.00,
    total_deductions DECIMAL(12,2) DEFAULT 0.00,
    net_salary DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    payment_date DATE NULL,
    payment_status ENUM('PENDING', 'PAID', 'CANCELLED') DEFAULT 'PENDING',
    created_by INT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_payroll_employee (employee_id),
    INDEX idx_payroll_period (pay_period_start, pay_period_end),
    INDEX idx_payroll_status (payment_status),
    INDEX idx_payroll_created_by (created_by),
    CONSTRAINT fk_payroll_employee 
        FOREIGN KEY (employee_id) REFERENCES employees(employee_id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_payroll_created_by 
        FOREIGN KEY (created_by) REFERENCES users(user_id)
        ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT chk_pay_period 
        CHECK (pay_period_end >= pay_period_start),
    CONSTRAINT chk_gross_salary 
        CHECK (gross_salary >= 0),
    CONSTRAINT chk_net_salary 
        CHECK (net_salary >= 0)
) ENGINE=InnoDB;

-- Insert default admin user (password: admin123)
INSERT INTO users (username, password, role, full_name, email) 
VALUES ('admin', 'admin123', 'ADMIN', 'System Administrator', 'admin@company.com');

-- Insert sample departments
INSERT INTO departments (department_name, department_code, manager_name, budget) VALUES
('Human Resources', 'HR', 'John Smith', 500000.00),
('Information Technology', 'IT', 'Jane Doe', 1000000.00),
('Finance', 'FIN', 'Mike Johnson', 750000.00),
('Marketing', 'MKT', 'Sarah Wilson', 600000.00);

-- Insert sample employees
INSERT INTO employees (employee_code, first_name, last_name, email, phone, hire_date, department_id, position, base_salary) VALUES
('EMP001', 'Alice', 'Johnson', 'alice.johnson@company.com', '555-0101', '2023-01-15', 2, 'Software Developer', 75000.00),
('EMP002', 'Bob', 'Smith', 'bob.smith@company.com', '555-0102', '2023-02-01', 2, 'Senior Developer', 85000.00),
('EMP003', 'Carol', 'Davis', 'carol.davis@company.com', '555-0103', '2023-03-10', 1, 'HR Specialist', 55000.00),
('EMP004', 'David', 'Wilson', 'david.wilson@company.com', '555-0104', '2023-04-05', 3, 'Financial Analyst', 65000.00),
('EMP005', 'Emma', 'Brown', 'emma.brown@company.com', '555-0105', '2023-05-20', 4, 'Marketing Coordinator', 50000.00);

-- Display success message
SELECT 'Database schema created successfully!' AS message;