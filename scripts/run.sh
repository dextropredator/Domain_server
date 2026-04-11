#!/bin/bash


echo "[BUILD] Cleaning old class files..."
rm -rf bin
mkdir bin

echo "[BUILD] Compiling Java source files..."
javac -d bin $(find src -name "*.java")

if [ $? -eq 0 ]; then
    echo "[BUILD] Compilation successful!"
    
    echo "[NETWORK] Ensuring ports 8080 and 8082 are open..."
    sudo ufw allow 8080/tcp
    sudo ufw allow 8000/tcp
    
   
    echo "[APP] Starting Domain Server..."
    java -cp bin app.MainServer
else
    echo "[ERROR] Compilation failed. Please check your code for syntax errors."
    exit 1
fi