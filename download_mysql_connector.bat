@echo off
echo Downloading MySQL Connector/J...
echo ================================

REM Create lib directory if it doesn't exist
if not exist "lib" mkdir lib

REM Download MySQL Connector/J 8.0.33
echo Downloading MySQL Connector/J 8.0.33...
powershell -Command "& {Invoke-WebRequest -Uri 'https://repo1.maven.org/maven2/mysql/mysql-connector-java/8.0.33/mysql-connector-java-8.0.33.jar' -OutFile 'lib\mysql-connector-java-8.0.33.jar'}"

if exist "lib\mysql-connector-java-8.0.33.jar" (
    echo.
    echo MySQL Connector/J downloaded successfully!
    echo File location: lib\mysql-connector-java-8.0.33.jar
    echo.
    echo You can now run the application using:
    echo   compile_and_run.bat
    echo.
) else (
    echo.
    echo Download failed. Please download manually from:
    echo https://dev.mysql.com/downloads/connector/j/
    echo.
    echo Download the Platform Independent ZIP file and extract
    echo mysql-connector-java-8.0.33.jar to the lib directory.
    echo.
)

pause