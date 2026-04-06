
cd "$(dirname "$0")/.."

echo "[1/3] Cleaning up old compiled files..."
rm -rf bin
mkdir -p bin

echo "[2/3] Compiling Domain Server source code..."

find src -name "*.java" > sources.txt
javac -d bin @sources.txt


rm sources.txt


if [ $? -eq 0 ]; then
    echo "[3/3] Compilation successful! Starting Domain Server..."
    echo "------------------------------------------------------"
   
    java -cp bin app.DomainServer
else
    echo "------------------------------------------------------"
    echo "[ERROR] Compilation failed! Please check the Java errors above."
fi