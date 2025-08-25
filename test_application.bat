@echo off
echo Testing Payroll Management System
echo =================================
echo.

echo 1. Testing database connection...
java -Dfile.encoding=UTF-8 -cp "classes;lib/*" utils.DatabaseTest
echo.

echo 2. Testing application startup...
echo Note: The GUI application will start. Close it to continue.
echo Default login credentials:
echo Username: admin
echo Password: admin123
echo.
pause

echo Starting application...
java -Dfile.encoding=UTF-8 -cp "classes;lib/*" main.PayrollManagementSystem

echo.
echo Test completed!
pause