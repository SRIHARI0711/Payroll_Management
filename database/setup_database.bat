@echo off
echo ========================================
echo Payroll Management System Database Setup
echo ========================================
echo.

echo This script will help you set up the database for the Payroll Management System.
echo.

echo Prerequisites:
echo 1. MySQL Server must be installed and running
echo 2. You need MySQL root password or appropriate user credentials
echo 3. MySQL command line client (mysql.exe) must be in PATH
echo.

pause

echo.
echo Testing MySQL connection...
mysql --version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: MySQL command line client not found!
    echo Please ensure MySQL is installed and mysql.exe is in your PATH.
    echo.
    echo You can also run the SQL scripts manually:
    echo 1. Open MySQL Workbench or command line
    echo 2. Run schema_fixed.sql to create the database
    echo.
    pause
    exit /b 1
)

echo MySQL client found!
echo.

echo Choose an option:
echo 1. Create new database (will drop existing database if it exists)
echo 2. Update existing database structure only
echo 3. Reset database with sample data
echo 4. Exit
echo.

set /p choice="Enter your choice (1-4): "

if "%choice%"=="1" goto create_new
if "%choice%"=="2" goto update_existing
if "%choice%"=="3" goto reset_database
if "%choice%"=="4" goto exit
goto invalid_choice

:create_new
echo.
echo Creating new database...
set /p username="Enter MySQL username (default: root): "
if "%username%"=="" set username=root

echo Running schema_fixed.sql...
mysql -u %username% -p < schema_fixed.sql
if %errorlevel% equ 0 (
    echo.
    echo SUCCESS: Database created successfully!
    echo.
    echo Default login credentials:
    echo Username: admin
    echo Password: admin123
    echo.
) else (
    echo.
    echo ERROR: Failed to create database. Please check:
    echo 1. MySQL server is running
    echo 2. Username and password are correct
    echo 3. User has CREATE DATABASE privileges
    echo.
)
goto end

:update_existing
echo.
echo Updating existing database structure...
set /p username="Enter MySQL username (default: root): "
if "%username%"=="" set username=root

echo This will update the database structure without losing data.
echo WARNING: Please backup your data before proceeding!
pause

echo Running update script...
mysql -u %username% -p payroll_management < update_schema.sql
if %errorlevel% equ 0 (
    echo.
    echo SUCCESS: Database updated successfully!
) else (
    echo.
    echo ERROR: Failed to update database.
)
goto end

:reset_database
echo.
echo Resetting database with sample data...
echo WARNING: This will delete all existing data!
set /p confirm="Are you sure? (y/N): "
if /i not "%confirm%"=="y" goto end

set /p username="Enter MySQL username (default: root): "
if "%username%"=="" set username=root

mysql -u %username% -p < schema_fixed.sql
if %errorlevel% equ 0 (
    echo.
    echo SUCCESS: Database reset successfully!
) else (
    echo.
    echo ERROR: Failed to reset database.
)
goto end

:invalid_choice
echo Invalid choice. Please try again.
goto end

:exit
echo Exiting...
goto end

:end
echo.
echo Database setup complete.
echo.
echo If you encounter any issues:
echo 1. Check that MySQL server is running
echo 2. Verify credentials in src/config/DatabaseConfig.java
echo 3. Ensure user has proper database privileges
echo 4. Check MySQL error logs for detailed error messages
echo.
pause