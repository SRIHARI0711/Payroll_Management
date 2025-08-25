# Payroll Management System

A desktop application built with Java Swing and MySQL for managing employee payroll efficiently.

## Features

- Employee Management (Add, Update, Delete, View)
- Payroll Calculation and Management
- Department Management
- User Authentication (Admin/HR)
- Salary Reports
- Search and Filter Functionality

## Tech Stack

- **Frontend**: Java Swing
- **Backend**: MySQL Database
- **Database Connector**: MySQL Connector/J (JDBC)
- **IDE**: Visual Studio Code

## Prerequisites

- Java JDK 8 or higher
- MySQL Server
- MySQL Connector/J JAR file

## Database Setup

1. Create a MySQL database named `payroll_management`
2. Run the SQL scripts in the `database/` folder to create tables
3. Update database connection details in `src/config/DatabaseConfig.java`

## How to Run

1. Compile the Java files
2. Ensure MySQL Connector/J is in the classpath
3. Run the main class: `PayrollManagementSystem`

## Project Structure

```
payroll_management/
├── src/
│   ├── main/
│   │   └── PayrollManagementSystem.java
│   ├── config/
│   │   └── DatabaseConfig.java
│   ├── models/
│   │   ├── Employee.java
│   │   ├── Department.java
│   │   ├── Payroll.java
│   │   └── User.java
│   ├── dao/
│   │   ├── EmployeeDAO.java
│   │   ├── DepartmentDAO.java
│   │   ├── PayrollDAO.java
│   │   └── UserDAO.java
│   ├── gui/
│   │   ├── LoginFrame.java
│   │   ├── MainFrame.java
│   │   ├── EmployeeManagementPanel.java
│   │   ├── PayrollManagementPanel.java
│   │   └── ReportsPanel.java
│   └── utils/
│       ├── DatabaseConnection.java
│       └── ValidationUtils.java
├── database/
│   └── schema.sql
├── lib/
│   └── mysql-connector-java-8.0.33.jar
└── README.md
```