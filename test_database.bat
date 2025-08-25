@echo off
echo ========================================
echo Database Connection Test
echo ========================================
echo.

echo This will test the database connection and diagnose any issues.
echo.

echo Compiling test class...
javac -cp "lib/*;src" src/utils/DatabaseTest.java -d classes

if %errorlevel% neq 0 (
    echo.
    echo ERROR: Compilation failed!
    echo Please ensure:
    echo 1. Java JDK is installed and in PATH
    echo 2. MySQL Connector JAR is in the lib folder
    echo 3. All source files are present
    pause
    exit /b 1
)

echo.
echo Running database test...
echo.

java -cp "lib/*;classes" utils.DatabaseTest

echo.
echo Test completed. Check the output above for any issues.
echo.

echo If you see connection errors:
echo 1. Run database/setup_database.bat to create the database
echo 2. Check TROUBLESHOOTING.md for detailed solutions
echo 3. Verify MySQL server is running
echo 4. Update credentials in src/config/DatabaseConfig.java
echo.

pause