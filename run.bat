@echo off
echo Starting Payroll Management System...
echo.
echo Default login credentials:
echo Username: admin
echo Password: admin123
echo.

REM Run the application with UTF-8 encoding to avoid garbled characters
java -Dfile.encoding=UTF-8 -cp "classes;lib/*" main.PayrollManagementSystem

echo.
echo Application closed.
pause