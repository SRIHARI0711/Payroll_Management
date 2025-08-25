# Payroll Management System - Setup Guide

## Prerequisites

### 1. Java Development Kit (JDK)
- Install JDK 8 or higher
- Download from: https://www.oracle.com/java/technologies/downloads/
- Ensure `JAVA_HOME` environment variable is set
- Verify installation: `java -version` and `javac -version`

### 2. MySQL Database Server
- Install MySQL Server 5.7 or higher
- Download from: https://dev.mysql.com/downloads/mysql/
- Remember the root password you set during installation

### 3. MySQL Connector/J (JDBC Driver)
- Download MySQL Connector/J JAR file
- Download from: https://dev.mysql.com/downloads/connector/j/
- Choose "Platform Independent" and download the ZIP archive
- Extract and copy `mysql-connector-java-8.0.33.jar` (or similar) to the `lib/` directory

## Database Setup

### 1. Create Database
1. Open MySQL Command Line Client or MySQL Workbench
2. Log in with your root credentials
3. Run the SQL script from `database/schema.sql`:
   ```sql
   SOURCE /path/to/payroll_management/database/schema.sql;
   ```
   Or copy and paste the contents of the file

### 2. Configure Database Connection
1. Open `src/config/DatabaseConfig.java`
2. Update the database connection parameters:
   ```java
   public static final String DB_URL = "jdbc:mysql://localhost:3306/payroll_management";
   public static final String DB_USERNAME = "root";
   public static final String DB_PASSWORD = "your_mysql_password"; // Update this
   ```

## Application Setup

### Method 1: Using the Batch Script (Windows)
1. Double-click `compile_and_run.bat`
2. Follow the prompts
3. The script will compile and optionally run the application

### Method 2: Manual Compilation
1. Open Command Prompt/Terminal in the project directory
2. Create classes directory: `mkdir classes`
3. Compile the application:
   ```bash
   javac -d classes -cp "lib/*;." src/**/*.java
   ```
4. Run the application:
   ```bash
   java -cp "classes;lib/*" main.PayrollManagementSystem
   ```

### Method 3: Using an IDE
1. Import the project into your IDE (IntelliJ IDEA, Eclipse, VS Code)
2. Add the MySQL Connector/J JAR to the project classpath
3. Set the main class to `main.PayrollManagementSystem`
4. Run the application

## Default Login Credentials

- **Username:** admin
- **Password:** admin123

## Project Structure

```
payroll_management/
├── src/
│   ├── main/
│   │   └── PayrollManagementSystem.java    # Main application class
│   ├── config/
│   │   └── DatabaseConfig.java             # Database configuration
│   ├── models/
│   │   ├── Employee.java                   # Employee model
│   │   ├── Department.java                 # Department model
│   │   ├── Payroll.java                    # Payroll model
│   │   └── User.java                       # User model
│   ├── dao/
│   │   ├── EmployeeDAO.java                # Employee data access
│   │   ├── DepartmentDAO.java              # Department data access
│   │   ├── PayrollDAO.java                 # Payroll data access
│   │   └── UserDAO.java                    # User data access
│   ├── gui/
│   │   ├── LoginFrame.java                 # Login window
│   │   ├── MainFrame.java                  # Main application window
│   │   ├── EmployeeManagementPanel.java    # Employee management
│   │   ├── PayrollManagementPanel.java     # Payroll management
│   │   └── ReportsPanel.java               # Reports and analytics
│   └── utils/
│       ├── DatabaseConnection.java         # Database connection utility
│       └── ValidationUtils.java            # Input validation utilities
├── database/
│   └── schema.sql                          # Database schema and sample data
├── lib/
│   └── mysql-connector-java-8.0.33.jar    # MySQL JDBC driver (download required)
├── classes/                                # Compiled Java classes (auto-generated)
├── compile_and_run.bat                     # Windows compilation script
├── README.md                               # Project documentation
└── SETUP_GUIDE.md                          # This setup guide
```

## Features

### Employee Management
- Add, edit, delete employees
- Search and filter employees
- Employee status management (Active, Inactive, Terminated)
- Department assignment

### Payroll Management
- Create payroll records for employees
- Calculate overtime pay, bonuses, and deductions
- Automatic salary calculations
- Payment status tracking
- Mark payrolls as paid

### Reports and Analytics
- Employee reports with filtering
- Payroll reports by date range
- Department-wise reports
- Summary statistics
- Export reports to CSV

### User Authentication
- Secure login system
- Role-based access (Admin/HR)
- User session management

## Troubleshooting

### Database Connection Issues
1. Verify MySQL server is running
2. Check database credentials in `DatabaseConfig.java`
3. Ensure the database `payroll_management` exists
4. Verify MySQL Connector/J JAR is in the classpath

### Compilation Errors
1. Ensure JDK is properly installed
2. Check that all source files are present
3. Verify MySQL Connector/J JAR is in the `lib/` directory

### Runtime Errors
1. Check console output for detailed error messages
2. Verify database connection
3. Ensure all required tables exist in the database

## Sample Data

The database schema includes sample data:
- 1 admin user (admin/admin123)
- 4 departments (HR, IT, Finance, Marketing)
- 5 sample employees

## Security Notes

- Change the default admin password after first login
- Use strong passwords for database connections
- Consider implementing password hashing for production use
- Regularly backup your database

## Support

For issues or questions:
1. Check the console output for error messages
2. Verify all setup steps have been completed
3. Ensure all prerequisites are properly installed
4. Check database connectivity

## License

This project is for educational and demonstration purposes.