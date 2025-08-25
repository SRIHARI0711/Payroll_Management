# Payroll Management System - Troubleshooting Guide

## Common SQL Errors and Solutions

### 1. Connection Errors

#### Error: "Access denied for user 'root'@'localhost'"
**Solution:**
```sql
-- Reset MySQL root password
ALTER USER 'root'@'localhost' IDENTIFIED BY 'your_new_password';
FLUSH PRIVILEGES;
```
- Update the password in `src/config/DatabaseConfig.java`
- Ensure MySQL service is running

#### Error: "Unknown database 'payroll_management'"
**Solution:**
```sql
-- Create the database
CREATE DATABASE payroll_management;
-- Then run the schema_fixed.sql file
```

#### Error: "Communications link failure"
**Solutions:**
1. Check if MySQL server is running:
   ```bash
   # Windows
   net start mysql
   
   # Or check services
   services.msc
   ```
2. Verify MySQL is listening on port 3306
3. Check firewall settings
4. Increase connection timeout in DatabaseConfig.java

### 2. Table/Schema Errors

#### Error: "Table 'payroll_management.employees' doesn't exist"
**Solution:**
1. Run the database setup script: `database/setup_database.bat`
2. Or manually execute: `database/schema_fixed.sql`

#### Error: "Unknown column 'column_name' in 'field list'"
**Solution:**
1. Update database schema using `schema_fixed.sql`
2. Check if all required columns exist in tables

### 3. Data Integrity Errors

#### Error: "Duplicate entry for key 'employee_code'"
**Solution:**
- Use a unique employee code
- Check existing employee codes before creating new ones

#### Error: "Cannot add or update a child row: a foreign key constraint fails"
**Solution:**
- Ensure referenced department exists before assigning to employee
- Check that all foreign key references are valid

#### Error: "Data too long for column"
**Solution:**
- Check data length limits in schema
- Validate input data before saving

### 4. JDBC Driver Errors

#### Error: "No suitable driver found for jdbc:mysql"
**Solution:**
1. Ensure `mysql-connector-java.jar` is in the `lib` folder
2. Check classpath includes the MySQL connector
3. Verify JDBC URL format in DatabaseConfig.java

#### Error: "ClassNotFoundException: com.mysql.cj.jdbc.Driver"
**Solution:**
1. Download MySQL Connector/J from: https://dev.mysql.com/downloads/connector/j/
2. Place the JAR file in the `lib` directory
3. Update classpath to include the JAR file

### 5. Performance Issues

#### Slow Query Performance
**Solutions:**
1. Add indexes to frequently queried columns
2. Optimize WHERE clauses
3. Use LIMIT for large result sets
4. Consider database connection pooling

### 6. Character Encoding Issues

#### Error: "Incorrect string value" or garbled characters
**Solution:**
1. Ensure database uses UTF-8 encoding:
   ```sql
   ALTER DATABASE payroll_management CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```
2. Update connection URL to include encoding:
   ```java
   "jdbc:mysql://localhost:3306/payroll_management?useUnicode=true&characterEncoding=UTF-8"
   ```

## Database Setup Steps

### Step 1: Install MySQL
1. Download MySQL Community Server
2. Install with default settings
3. Remember the root password

### Step 2: Create Database
1. Open MySQL Command Line or Workbench
2. Run: `CREATE DATABASE payroll_management;`
3. Execute the `schema_fixed.sql` file

### Step 3: Configure Application
1. Update `src/config/DatabaseConfig.java` with correct credentials
2. Ensure MySQL Connector JAR is in classpath
3. Test connection using the application

### Step 4: Verify Setup
1. Run the application
2. Try logging in with: admin/admin123
3. Test basic operations (view employees, etc.)

## Configuration Files to Check

### DatabaseConfig.java
```java
public static final String DB_URL = "jdbc:mysql://localhost:3306/payroll_management";
public static final String DB_USERNAME = "root";
public static final String DB_PASSWORD = "your_password_here";
```

### MySQL Configuration (my.cnf or my.ini)
```ini
[mysqld]
port=3306
default-storage-engine=INNODB
sql-mode="STRICT_TRANS_TABLES,NO_ZERO_DATE,NO_ZERO_IN_DATE,ERROR_FOR_DIVISION_BY_ZERO"
```

## Debugging Tips

### Enable SQL Logging
Add to DatabaseConfig.java:
```java
props.setProperty("logger", "com.mysql.cj.log.StandardLogger");
props.setProperty("profileSQL", "true");
```

### Check MySQL Error Log
- Windows: Usually in MySQL installation directory
- Look for files like `error.log` or `hostname.err`

### Test Database Connection
Use the built-in connection test in the application:
- Tools → Test Database Connection

### Manual SQL Testing
```sql
-- Test basic connectivity
SELECT 1;

-- Check database exists
SHOW DATABASES LIKE 'payroll_management';

-- Check tables exist
USE payroll_management;
SHOW TABLES;

-- Test sample query
SELECT COUNT(*) FROM employees;
```

## Common Solutions Summary

1. **Database doesn't exist**: Run `schema_fixed.sql`
2. **Access denied**: Check username/password in DatabaseConfig.java
3. **Driver not found**: Add mysql-connector-java.jar to classpath
4. **Connection timeout**: Increase timeout values
5. **Foreign key errors**: Ensure referenced records exist
6. **Duplicate key errors**: Use unique values for unique fields
7. **Table doesn't exist**: Run database setup script

## Getting Help

If you continue to experience issues:

1. Check the console output for detailed error messages
2. Review MySQL error logs
3. Verify all prerequisites are met
4. Test with a simple MySQL client first
5. Check MySQL server status and configuration

## Useful MySQL Commands

```sql
-- Check MySQL version
SELECT VERSION();

-- Show current user and host
SELECT USER(), @@hostname;

-- Check privileges
SHOW GRANTS FOR CURRENT_USER();

-- Check database size
SELECT 
    table_schema AS 'Database',
    ROUND(SUM(data_length + index_length) / 1024 / 1024, 2) AS 'Size (MB)'
FROM information_schema.tables 
WHERE table_schema = 'payroll_management';

-- Check table status
SHOW TABLE STATUS FROM payroll_management;
```