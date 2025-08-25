@echo off
setlocal EnableDelayedExpansion
echo Payroll Management System - Compile and Run Script
echo ================================================

REM Check if Java is installed
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo Error: Java is not installed or not in PATH
    echo Please install Java JDK 8 or higher
    pause
    exit /b 1
)

REM Check if javac is available
javac -version >nul 2>&1
if %errorlevel% neq 0 (
    echo Error: Java compiler (javac) is not available
    echo Please ensure JDK is installed and JAVA_HOME is set
    pause
    exit /b 1
)

REM Create lib directory if it doesn't exist
if not exist "lib" mkdir lib

REM Check for MySQL Connector/J (both old and new artifact names)
set "mysql_jar_found=false"
for %%f in ("lib\mysql-connector-java-*.jar") do (
    if exist "%%f" set "mysql_jar_found=true"
)
for %%f in ("lib\mysql-connector-j-*.jar") do (
    if exist "%%f" set "mysql_jar_found=true"
)
if "%mysql_jar_found%"=="false" (
    echo Warning: MySQL Connector/J JAR file not found in lib directory
    echo Please download mysql-connector-j-8.x.x.jar (or mysql-connector-java-8.x.x.jar)
    echo and place it in the lib directory
    echo.
    echo Download from: https://dev.mysql.com/downloads/connector/j/
    echo.
    set /p continue="Continue anyway? (y/n): "
    if /i not "!continue!"=="y" (
        pause
        exit /b 1
    )
)

REM Create classes directory
if not exist "classes" mkdir classes

echo.
echo Compiling Java source files...
echo.

REM Set classpath for MySQL connector
set CLASSPATH=lib\*;.

REM Compile all Java files
javac -d classes -cp "%CLASSPATH%" src\config\*.java src\models\*.java src\utils\*.java src\dao\*.java src\gui\*.java src\main\*.java

if %errorlevel% neq 0 (
    echo.
    echo Compilation failed! Please check the error messages above.
    pause
    exit /b 1
)

echo.
echo Compilation successful!
echo.

REM Ask user if they want to run the application
set /p run="Do you want to run the application now? (y/n): "
if /i "!run!"=="y" (
    echo.
    echo Starting Payroll Management System...
    echo.
    echo Default login credentials:
    echo Username: admin
    echo Password: admin123
    echo.
    
    REM Run the application with UTF-8 encoding to avoid garbled characters
    java -Dfile.encoding=UTF-8 -cp "classes;lib\*" main.PayrollManagementSystem
)

echo.
echo Script completed.
pause