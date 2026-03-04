#!/bin/bash

echo "========================================="
echo "   InterviewAI - Complete Startup"
echo "========================================="
echo ""

# Colors
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

# Function to check if command exists
command_exists() {
    command -v "$1" >/dev/null 2>&1
}

# Check prerequisites
echo -e "${BLUE}Checking prerequisites...${NC}"

if ! command_exists java; then
    echo -e "${RED}✗ Java not found. Please install Java 17+${NC}"
    exit 1
fi
echo -e "${GREEN}✓ Java found: $(java -version 2>&1 | head -n 1)${NC}"

if ! command_exists mvn; then
    echo -e "${RED}✗ Maven not found. Please install Maven${NC}"
    exit 1
fi
echo -e "${GREEN}✓ Maven found${NC}"

if ! command_exists node; then
    echo -e "${RED}✗ Node.js not found. Please install Node.js 18+${NC}"
    exit 1
fi
echo -e "${GREEN}✓ Node.js found: $(node --version)${NC}"

if ! command_exists ollama; then
    echo -e "${YELLOW}⚠ Ollama not found. Install from https://ollama.com${NC}"
    echo "Continue anyway? (y/n)"
    read -r response
    if [[ ! "$response" =~ ^[Yy]$ ]]; then
        exit 1
    fi
else
    echo -e "${GREEN}✓ Ollama found${NC}"
fi

echo ""
echo -e "${BLUE}Step 1: Starting Ollama...${NC}"
if pgrep -x "ollama" > /dev/null; then
    echo -e "${GREEN}✓ Ollama already running${NC}"
else
    echo "Starting Ollama server..."
    ollama serve > /dev/null 2>&1 &
    sleep 3
    echo -e "${GREEN}✓ Ollama started${NC}"
fi

# Check if model exists
echo ""
echo -e "${BLUE}Step 2: Checking AI model...${NC}"
if ollama list | grep -q "llama3\|mistral"; then
    echo -e "${GREEN}✓ AI model found${NC}"
else
    echo -e "${YELLOW}⚠ No AI model found. Downloading llama3...${NC}"
    echo "This may take several minutes..."
    ollama pull llama3
fi

echo ""
echo -e "${BLUE}Step 3: Installing frontend dependencies...${NC}"
if [ ! -d "node_modules" ]; then
    npm install
else
    echo -e "${GREEN}✓ Dependencies already installed${NC}"
fi

echo ""
echo -e "${BLUE}Step 4: Starting backend...${NC}"
cd backend
if [ ! -d "target" ]; then
    echo "Building backend for the first time..."
    mvn clean install -DskipTests
fi

echo "Starting Spring Boot application..."
mvn spring-boot:run > ../backend.log 2>&1 &
BACKEND_PID=$!
cd ..

echo "Waiting for backend to start..."
for i in {1..30}; do
    if curl -s http://localhost:8080 > /dev/null; then
        echo -e "${GREEN}✓ Backend started successfully${NC}"
        break
    fi
    sleep 2
    if [ $i -eq 30 ]; then
        echo -e "${RED}✗ Backend failed to start. Check backend.log${NC}"
        exit 1
    fi
done

echo ""
echo -e "${BLUE}Step 5: Starting frontend...${NC}"
npm run dev > frontend.log 2>&1 &
FRONTEND_PID=$!

echo "Waiting for frontend to start..."
sleep 5
echo -e "${GREEN}✓ Frontend started${NC}"

echo ""
echo "========================================="
echo -e "${GREEN}   InterviewAI is now running!${NC}"
echo "========================================="
echo ""
echo "Access the application:"
echo -e "  ${BLUE}Frontend:${NC} http://localhost:3000"
echo -e "  ${BLUE}Backend:${NC}  http://localhost:8080"
echo -e "  ${BLUE}H2 Console:${NC} http://localhost:8080/h2-console"
echo ""
echo "Logs:"
echo "  Backend: tail -f backend.log"
echo "  Frontend: tail -f frontend.log"
echo ""
echo "To stop all services:"
echo "  kill $BACKEND_PID $FRONTEND_PID"
echo "  Or run: ./stop-all.sh"
echo ""
echo -e "${YELLOW}Press Ctrl+C to stop all services${NC}"

# Wait for user interrupt
trap "echo ''; echo 'Stopping services...'; kill $BACKEND_PID $FRONTEND_PID 2>/dev/null; exit" INT
wait
