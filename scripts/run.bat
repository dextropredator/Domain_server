@echo off

:: 1. Clean up old build files
echo [BUILD] Cleaning old class files...
if exist bin rmdir /s /q bin
mkdir bin

:: 2. Compile all Java files
:: Windows doesn't have the "find" command like Linux, so we create a temporary 
:: text file with all the .java paths, feed it to javac, and then delete it.
echo [BUILD] Compiling Java source files...
dir /s /B src\*.java > sources.txt
javac -d bin @sources.txt
set BUILD_STATUS=%ERRORLEVEL%
del sources.txt

if %BUILD_STATUS% equ 0 (
    echo [BUILD] Compilation successful!
    
    :: 3. Open the Firewall (Requires running the .bat as Administrator)
    echo [NETWORK] Ensuring ports 8080 and 8000 are open...
    netsh advfirewall firewall add rule name="Java Domain Server 8080" dir=in action=allow protocol=TCP localport=8080 >nul 2>&1
    netsh advfirewall firewall add rule name="Java Proxy Server 8000" dir=in action=allow protocol=TCP localport=8000 >nul 2>&1
    
    :: 4. Run the Main Server
    echo [APP] Starting Domain Server...
    java -cp bin app.MainServer
) else (
    echo [ERROR] Compilation failed. Please check your code for syntax errors.
    exit /b 1
)