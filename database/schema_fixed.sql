-- Payroll Management System Database Schema - Fixed Version
-- This script resolves common SQL errors and improves database structure

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

-- Create audit table for tracking changes
CREATE TABLE audit_log (
    audit_id INT PRIMARY KEY AUTO_INCREMENT,
    table_name VARCHAR(50) NOT NULL,
    operation ENUM('INSERT', 'UPDATE', 'DELETE') NOT NULL,
    record_id INT NOT NULL,
    old_values JSON NULL,
    new_values JSON NULL,
    user_id INT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_audit_table (table_name),
    INDEX idx_audit_operation (operation),
    INDEX idx_audit_timestamp (timestamp)
) ENGINE=InnoDB;

-- Insert default admin user (password should be hashed in production)
INSERT INTO users (username, password, role, full_name, email) 
VALUES ('admin', 'admin123', 'ADMIN', 'System Administrator', 'admin@company.com');

-- Insert sample departments
INSERT INTO departments (department_name, department_code, manager_name, budget) VALUES
('Human Resources', 'HR', 'John Smith', 500000.00),
('Information Technology', 'IT', 'Jane Doe', 1000000.00),
('Finance', 'FIN', 'Mike Johnson', 750000.00),
('Marketing', 'MKT', 'Sarah Wilson', 600000.00),
('Operations', 'OPS', 'Robert Brown', 800000.00);

-- Insert sample employees
INSERT INTO employees (employee_code, first_name, last_name, email, phone, hire_date, department_id, position, base_salary) VALUES
('EMP001', 'Alice', 'Johnson', 'alice.johnson@company.com', '555-0101', '2023-01-15', 2, 'Software Developer', 75000.00),
('EMP002', 'Bob', 'Smith', 'bob.smith@company.com', '555-0102', '2023-02-01', 2, 'Senior Developer', 85000.00),
('EMP003', 'Carol', 'Davis', 'carol.davis@company.com', '555-0103', '2023-03-10', 1, 'HR Specialist', 55000.00),
('EMP004', 'David', 'Wilson', 'david.wilson@company.com', '555-0104', '2023-04-05', 3, 'Financial Analyst', 65000.00),
('EMP005', 'Emma', 'Brown', 'emma.brown@company.com', '555-0105', '2023-05-20', 4, 'Marketing Coordinator', 50000.00),
('EMP006', 'Frank', 'Miller', 'frank.miller@company.com', '555-0106', '2023-06-01', 2, 'DevOps Engineer', 80000.00),
('EMP007', 'Grace', 'Taylor', 'grace.taylor@company.com', '555-0107', '2023-07-15', 1, 'HR Manager', 70000.00),
('EMP008', 'Henry', 'Anderson', 'henry.anderson@company.com', '555-0108', '2023-08-01', 5, 'Operations Manager', 75000.00);

-- Create views for common queries
CREATE VIEW employee_details AS
SELECT 
    e.employee_id,
    e.employee_code,
    CONCAT(e.first_name, ' ', e.last_name) AS full_name,
    e.first_name,
    e.last_name,
    e.email,
    e.phone,
    e.position,
    e.base_salary,
    e.employment_status,
    e.hire_date,
    d.department_name,
    d.department_code,
    DATEDIFF(CURDATE(), e.hire_date) AS days_employed
FROM employees e
LEFT JOIN departments d ON e.department_id = d.department_id
WHERE e.employment_status = 'ACTIVE';

CREATE VIEW payroll_summary AS
SELECT 
    p.payroll_id,
    p.employee_id,
    CONCAT(e.first_name, ' ', e.last_name) AS employee_name,
    e.employee_code,
    d.department_name,
    p.pay_period_start,
    p.pay_period_end,
    p.gross_salary,
    p.total_deductions,
    p.net_salary,
    p.payment_status,
    p.payment_date
FROM payroll p
JOIN employees e ON p.employee_id = e.employee_id
LEFT JOIN departments d ON e.department_id = d.department_id
ORDER BY p.pay_period_end DESC;

-- Create stored procedures for common operations
DELIMITER //

CREATE PROCEDURE GetEmployeePayrollHistory(IN emp_id INT)
BEGIN
    SELECT 
        p.*,
        CONCAT(e.first_name, ' ', e.last_name) AS employee_name,
        e.employee_code
    FROM payroll p
    JOIN employees e ON p.employee_id = e.employee_id
    WHERE p.employee_id = emp_id
    ORDER BY p.pay_period_end DESC;
END //

CREATE PROCEDURE CalculateMonthlyPayroll(IN emp_id INT, IN period_start DATE, IN period_end DATE, IN overtime_hrs DECIMAL(6,2), IN bonus_amt DECIMAL(12,2))
BEGIN
    DECLARE base_sal DECIMAL(12,2);
    DECLARE overtime_pay_calc DECIMAL(12,2);
    DECLARE gross_sal DECIMAL(12,2);
    DECLARE tax_ded DECIMAL(12,2);
    DECLARE insurance_ded DECIMAL(12,2);
    DECLARE total_ded DECIMAL(12,2);
    DECLARE net_sal DECIMAL(12,2);
    
    -- Get base salary
    SELECT base_salary INTO base_sal FROM employees WHERE employee_id = emp_id;
    
    -- Calculate overtime pay (1.5x hourly rate)
    SET overtime_pay_calc = (base_sal / 160) * 1.5 * overtime_hrs; -- Assuming 160 hours per month
    
    -- Calculate gross salary
    SET gross_sal = base_sal + overtime_pay_calc + bonus_amt;
    
    -- Calculate deductions (simplified calculation)
    SET tax_ded = gross_sal * 0.15; -- 15% tax
    SET insurance_ded = gross_sal * 0.05; -- 5% insurance
    SET total_ded = tax_ded + insurance_ded;
    
    -- Calculate net salary
    SET net_sal = gross_sal - total_ded;
    
    -- Return calculated values
    SELECT 
        base_sal AS base_salary,
        overtime_hrs AS overtime_hours,
        overtime_pay_calc AS overtime_pay,
        bonus_amt AS bonus,
        gross_sal AS gross_salary,
        tax_ded AS tax_deduction,
        insurance_ded AS insurance_deduction,
        total_ded AS total_deductions,
        net_sal AS net_salary;
END //

DELIMITER ;

-- Create triggers for audit logging
DELIMITER //

CREATE TRIGGER employee_audit_insert
AFTER INSERT ON employees
FOR EACH ROW
BEGIN
    INSERT INTO audit_log (table_name, operation, record_id, new_values)
    VALUES ('employees', 'INSERT', NEW.employee_id, JSON_OBJECT(
        'employee_code', NEW.employee_code,
        'first_name', NEW.first_name,
        'last_name', NEW.last_name,
        'email', NEW.email,
        'base_salary', NEW.base_salary
    ));
END //

CREATE TRIGGER employee_audit_update
AFTER UPDATE ON employees
FOR EACH ROW
BEGIN
    INSERT INTO audit_log (table_name, operation, record_id, old_values, new_values)
    VALUES ('employees', 'UPDATE', NEW.employee_id, 
        JSON_OBJECT(
            'employee_code', OLD.employee_code,
            'first_name', OLD.first_name,
            'last_name', OLD.last_name,
            'email', OLD.email,
            'base_salary', OLD.base_salary
        ),
        JSON_OBJECT(
            'employee_code', NEW.employee_code,
            'first_name', NEW.first_name,
            'last_name', NEW.last_name,
            'email', NEW.email,
            'base_salary', NEW.base_salary
        )
    );
END //

DELIMITER ;

-- Grant necessary privileges (adjust as needed for your MySQL user)
-- GRANT ALL PRIVILEGES ON payroll_management.* TO 'root'@'localhost';
-- FLUSH PRIVILEGES;

-- Display success message
SELECT 'Database schema created successfully!' AS message;