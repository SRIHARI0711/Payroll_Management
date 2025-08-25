-- Fix Common SQL Errors Script
-- Run this script to resolve common database issues

USE payroll_management;

-- Set SQL mode to handle compatibility issues
SET sql_mode = 'STRICT_TRANS_TABLES,NO_ZERO_DATE,NO_ZERO_IN_DATE,ERROR_FOR_DIVISION_BY_ZERO';

-- Fix 1: Ensure all tables have proper character set
ALTER TABLE users CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE departments CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE employees CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE payroll CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Fix 2: Add missing indexes for better performance
CREATE INDEX IF NOT EXISTS idx_employees_dept ON employees(department_id);
CREATE INDEX IF NOT EXISTS idx_employees_status ON employees(employment_status);
CREATE INDEX IF NOT EXISTS idx_employees_email ON employees(email);
CREATE INDEX IF NOT EXISTS idx_payroll_employee ON payroll(employee_id);
CREATE INDEX IF NOT EXISTS idx_payroll_period ON payroll(pay_period_start, pay_period_end);
CREATE INDEX IF NOT EXISTS idx_payroll_status ON payroll(payment_status);

-- Fix 3: Ensure proper decimal precision for salary fields
ALTER TABLE employees MODIFY COLUMN base_salary DECIMAL(12,2) NOT NULL DEFAULT 0.00;
ALTER TABLE payroll MODIFY COLUMN base_salary DECIMAL(12,2) NOT NULL DEFAULT 0.00;
ALTER TABLE payroll MODIFY COLUMN overtime_pay DECIMAL(12,2) DEFAULT 0.00;
ALTER TABLE payroll MODIFY COLUMN bonus DECIMAL(12,2) DEFAULT 0.00;
ALTER TABLE payroll MODIFY COLUMN allowances DECIMAL(12,2) DEFAULT 0.00;
ALTER TABLE payroll MODIFY COLUMN gross_salary DECIMAL(12,2) NOT NULL DEFAULT 0.00;
ALTER TABLE payroll MODIFY COLUMN tax_deduction DECIMAL(12,2) DEFAULT 0.00;
ALTER TABLE payroll MODIFY COLUMN insurance_deduction DECIMAL(12,2) DEFAULT 0.00;
ALTER TABLE payroll MODIFY COLUMN other_deductions DECIMAL(12,2) DEFAULT 0.00;
ALTER TABLE payroll MODIFY COLUMN total_deductions DECIMAL(12,2) DEFAULT 0.00;
ALTER TABLE payroll MODIFY COLUMN net_salary DECIMAL(12,2) NOT NULL DEFAULT 0.00;

-- Fix 4: Add updated_at column if missing
ALTER TABLE users ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;
ALTER TABLE departments ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

-- Fix 5: Ensure foreign key constraints are properly set
-- Drop existing foreign keys if they exist
SET FOREIGN_KEY_CHECKS = 0;

-- Re-add foreign key constraints with proper options
ALTER TABLE employees 
DROP FOREIGN KEY IF EXISTS fk_emp_department,
ADD CONSTRAINT fk_emp_department 
    FOREIGN KEY (department_id) REFERENCES departments(department_id)
    ON DELETE SET NULL ON UPDATE CASCADE;

ALTER TABLE payroll 
DROP FOREIGN KEY IF EXISTS fk_payroll_employee,
ADD CONSTRAINT fk_payroll_employee 
    FOREIGN KEY (employee_id) REFERENCES employees(employee_id)
    ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE payroll 
DROP FOREIGN KEY IF EXISTS fk_payroll_created_by,
ADD CONSTRAINT fk_payroll_created_by 
    FOREIGN KEY (created_by) REFERENCES users(user_id)
    ON DELETE SET NULL ON UPDATE CASCADE;

SET FOREIGN_KEY_CHECKS = 1;

-- Fix 6: Clean up any orphaned records
-- Remove payroll records for non-existent employees
DELETE p FROM payroll p 
LEFT JOIN employees e ON p.employee_id = e.employee_id 
WHERE e.employee_id IS NULL;

-- Set department_id to NULL for employees with invalid department references
UPDATE employees e 
LEFT JOIN departments d ON e.department_id = d.department_id 
SET e.department_id = NULL 
WHERE e.department_id IS NOT NULL AND d.department_id IS NULL;

-- Fix 7: Ensure admin user exists
INSERT IGNORE INTO users (username, password, role, full_name, email) 
VALUES ('admin', 'admin123', 'ADMIN', 'System Administrator', 'admin@company.com');

-- Fix 8: Ensure sample departments exist
INSERT IGNORE INTO departments (department_name, department_code, manager_name, budget) VALUES
('Human Resources', 'HR', 'John Smith', 500000.00),
('Information Technology', 'IT', 'Jane Doe', 1000000.00),
('Finance', 'FIN', 'Mike Johnson', 750000.00),
('Marketing', 'MKT', 'Sarah Wilson', 600000.00);

-- Fix 9: Add check constraints for data validation
-- Note: MySQL 8.0+ supports check constraints
-- For older versions, these will be ignored

ALTER TABLE employees 
ADD CONSTRAINT IF NOT EXISTS chk_base_salary 
CHECK (base_salary >= 0);

ALTER TABLE payroll 
ADD CONSTRAINT IF NOT EXISTS chk_pay_period 
CHECK (pay_period_end >= pay_period_start);

ALTER TABLE payroll 
ADD CONSTRAINT IF NOT EXISTS chk_gross_salary 
CHECK (gross_salary >= 0);

ALTER TABLE payroll 
ADD CONSTRAINT IF NOT EXISTS chk_net_salary 
CHECK (net_salary >= 0);

-- Fix 10: Update any NULL values that should have defaults
UPDATE employees SET base_salary = 0.00 WHERE base_salary IS NULL;
UPDATE payroll SET overtime_hours = 0.00 WHERE overtime_hours IS NULL;
UPDATE payroll SET overtime_rate = 1.50 WHERE overtime_rate IS NULL;
UPDATE payroll SET overtime_pay = 0.00 WHERE overtime_pay IS NULL;
UPDATE payroll SET bonus = 0.00 WHERE bonus IS NULL;
UPDATE payroll SET allowances = 0.00 WHERE allowances IS NULL;
UPDATE payroll SET tax_deduction = 0.00 WHERE tax_deduction IS NULL;
UPDATE payroll SET insurance_deduction = 0.00 WHERE insurance_deduction IS NULL;
UPDATE payroll SET other_deductions = 0.00 WHERE other_deductions IS NULL;
UPDATE payroll SET total_deductions = 0.00 WHERE total_deductions IS NULL;

-- Fix 11: Recalculate payroll totals to ensure consistency
UPDATE payroll SET 
    overtime_pay = (base_salary / 160) * 1.5 * overtime_hours,
    gross_salary = base_salary + overtime_pay + bonus + allowances,
    total_deductions = tax_deduction + insurance_deduction + other_deductions,
    net_salary = gross_salary - total_deductions
WHERE payroll_id > 0;

-- Fix 12: Optimize tables
OPTIMIZE TABLE users;
OPTIMIZE TABLE departments;
OPTIMIZE TABLE employees;
OPTIMIZE TABLE payroll;

-- Display summary of fixes applied
SELECT 'Database fixes completed successfully!' AS Status;

-- Show table statistics
SELECT 
    'users' AS table_name, 
    COUNT(*) AS record_count 
FROM users
UNION ALL
SELECT 
    'departments' AS table_name, 
    COUNT(*) AS record_count 
FROM departments
UNION ALL
SELECT 
    'employees' AS table_name, 
    COUNT(*) AS record_count 
FROM employees
UNION ALL
SELECT 
    'payroll' AS table_name, 
    COUNT(*) AS record_count 
FROM payroll;

-- Check for any remaining issues
SELECT 'Checking for orphaned payroll records...' AS check_name;
SELECT COUNT(*) AS orphaned_payroll_count
FROM payroll p 
LEFT JOIN employees e ON p.employee_id = e.employee_id 
WHERE e.employee_id IS NULL;

SELECT 'Checking for employees with invalid departments...' AS check_name;
SELECT COUNT(*) AS invalid_dept_count
FROM employees e 
LEFT JOIN departments d ON e.department_id = d.department_id 
WHERE e.department_id IS NOT NULL AND d.department_id IS NULL;