#!/bin/bash

# 1. Clean up old build files
echo "[BUILD] Cleaning old class files..."
rm -rf bin
mkdir bin

# 2. Compile all Java files
# This finds every .java file in the src folder and compiles it into the bin folder
echo "[BUILD] Compiling Java source files..."
javac -d bin $(find src -name "*.java")

if [ $? -eq 0 ]; then
    echo "[BUILD] Compilation successful!"
    
    # 3. Open the Firewall (Requires sudo)
    echo "[NETWORK] Ensuring ports 8080 and 8082 are open..."
    sudo ufw allow 8080/tcp
    sudo ufw allow 8000/tcp
    
    # 4. Run the Main Server
    # Note: We use -cp bin to tell Java where the compiled classes are
    # And we call the full package name: app.MainServer
    echo "[APP] Starting Domain Server..."
    java -cp bin app.MainServer
else
    echo "[ERROR] Compilation failed. Please check your code for syntax errors."
    exit 1
fi