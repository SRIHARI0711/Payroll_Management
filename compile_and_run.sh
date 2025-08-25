#!/bin/bash

echo "Payroll Management System - Compile and Run Script"
echo "================================================"

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "Error: Java is not installed or not in PATH"
    echo "Please install Java JDK 8 or higher"
    exit 1
fi

# Check if javac is available
if ! command -v javac &> /dev/null; then
    echo "Error: Java compiler (javac) is not available"
    echo "Please ensure JDK is installed and JAVA_HOME is set"
    exit 1
fi

# Create lib directory if it doesn't exist
mkdir -p lib

# Check for MySQL Connector/J
if ! ls lib/mysql-connector-java-*.jar 1> /dev/null 2>&1; then
    echo "Warning: MySQL Connector/J JAR file not found in lib directory"
    echo "Please download mysql-connector-java-8.0.33.jar or later"
    echo "and place it in the lib directory"
    echo ""
    echo "Download from: https://dev.mysql.com/downloads/connector/j/"
    echo ""
    read -p "Continue anyway? (y/n): " continue
    if [[ ! $continue =~ ^[Yy]$ ]]; then
        exit 1
    fi
fi

# Create classes directory
mkdir -p classes

echo ""
echo "Compiling Java source files..."
echo ""

# Set classpath for MySQL connector
export CLASSPATH="lib/*:."

# Compile all Java files
find src -name "*.java" -exec javac -d classes -cp "$CLASSPATH" {} +

if [ $? -ne 0 ]; then
    echo ""
    echo "Compilation failed! Please check the error messages above."
    exit 1
fi

echo ""
echo "Compilation successful!"
echo ""

# Ask user if they want to run the application
read -p "Do you want to run the application now? (y/n): " run
if [[ $run =~ ^[Yy]$ ]]; then
    echo ""
    echo "Starting Payroll Management System..."
    echo ""
    echo "Default login credentials:"
    echo "Username: admin"
    echo "Password: admin123"
    echo ""
    
    # Run the application
    java -cp "classes:lib/*" main.PayrollManagementSystem
fi

echo ""
echo "Script completed."