@echo off
title FINOVA - Smart Money Management Platform
echo =====================================================================
echo                  FINOVA - Smart Money Management
echo         Real Database + Two-Factor Email OTP + Google Login
echo =====================================================================
echo.

where java >nul 2>nul
if %ERRORLEVEL% neq 0 (
    echo [ERROR] Java is not installed or not in your PATH.
    echo Please install Java 17 or higher to run FINOVA.
    pause
    exit /b 1
)

echo [1/2] Verifying JAR artifact...
if not exist "backend\target\finova-backend-1.0.0.jar" (
    echo [INFO] Building backend JAR with Maven...
    call "tools\apache-maven-3.9.9\bin\mvn.cmd" package -DskipTests -f backend\pom.xml
)

echo [2/2] Starting FINOVA on http://localhost:8080 ...
echo [INFO] Database: Persistent Local Storage (./data/finovadb)
echo [INFO] Press Ctrl+C at any time to stop the server.
echo.

start "" "http://localhost:8080"
java -jar "backend\target\finova-backend-1.0.0.jar"

pause
