@echo off
REM Navigate to the root directory of the project
cd /d "%~dp0\.."

echo [1/3] Cleaning up old compiled files...
if exist bin rmdir /s /q bin
mkdir bin

echo [2/3] Compiling Domain Server source code...
REM Find all .java files and save their paths to a text file
dir /s /B src\*.java > sources.txt
javac -d bin @sources.txt

REM Clean up the temporary text file
del sources.txt

REM Check if compilation was successful
if %errorlevel% equ 0 (
    echo [3/3] Compilation successful! Starting Domain Server...
    echo ------------------------------------------------------
    java -cp bin app.DomainServer
) else (
    echo ------------------------------------------------------
    echo [ERROR] Compilation failed! Please check the Java errors above.
)
pause