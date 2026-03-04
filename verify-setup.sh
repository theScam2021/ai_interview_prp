#!/bin/bash

echo "========================================="
echo "   InterviewAI Setup Verification"
echo "========================================="
echo ""

# Colors
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m'

ERRORS=0

# Check Java
echo -n "Checking Java... "
if command -v java >/dev/null 2>&1; then
    VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
    if [ "$VERSION" -ge 17 ]; then
        echo -e "${GREEN}✓ Java $VERSION found${NC}"
    else
        echo -e "${RED}✗ Java 17+ required (found $VERSION)${NC}"
        ERRORS=$((ERRORS + 1))
    fi
else
    echo -e "${RED}✗ Java not found${NC}"
    ERRORS=$((ERRORS + 1))
fi

# Check Maven
echo -n "Checking Maven... "
if command -v mvn >/dev/null 2>&1; then
    echo -e "${GREEN}✓ Maven found${NC}"
else
    echo -e "${RED}✗ Maven not found${NC}"
    ERRORS=$((ERRORS + 1))
fi

# Check Node.js
echo -n "Checking Node.js... "
if command -v node >/dev/null 2>&1; then
    VERSION=$(node --version | cut -d'v' -f2 | cut -d'.' -f1)
    if [ "$VERSION" -ge 18 ]; then
        echo -e "${GREEN}✓ Node.js v$VERSION found${NC}"
    else
        echo -e "${YELLOW}⚠ Node.js 18+ recommended (found v$VERSION)${NC}"
    fi
else
    echo -e "${RED}✗ Node.js not found${NC}"
    ERRORS=$((ERRORS + 1))
fi

# Check npm
echo -n "Checking npm... "
if command -v npm >/dev/null 2>&1; then
    echo -e "${GREEN}✓ npm found${NC}"
else
    echo -e "${RED}✗ npm not found${NC}"
    ERRORS=$((ERRORS + 1))
fi

# Check Ollama
echo -n "Checking Ollama... "
if command -v ollama >/dev/null 2>&1; then
    echo -e "${GREEN}✓ Ollama found${NC}"
    
    # Check if Ollama is running
    echo -n "Checking Ollama service... "
    if curl -s http://localhost:11434/api/tags >/dev/null 2>&1; then
        echo -e "${GREEN}✓ Ollama is running${NC}"
        
        # Check for models
        echo -n "Checking AI models... "
        if ollama list | grep -q "llama3\|mistral"; then
            echo -e "${GREEN}✓ AI model found${NC}"
        else
            echo -e "${YELLOW}⚠ No AI model found. Run: ollama pull llama3${NC}"
        fi
    else
        echo -e "${YELLOW}⚠ Ollama not running. Start with: ollama serve${NC}"
    fi
else
    echo -e "${YELLOW}⚠ Ollama not found. Install from https://ollama.com${NC}"
fi

# Check project structure
echo ""
echo "Checking project structure..."

echo -n "Backend directory... "
if [ -d "backend" ]; then
    echo -e "${GREEN}✓${NC}"
else
    echo -e "${RED}✗ backend/ directory not found${NC}"
    ERRORS=$((ERRORS + 1))
fi

echo -n "Frontend source... "
if [ -d "src" ]; then
    echo -e "${GREEN}✓${NC}"
else
    echo -e "${RED}✗ src/ directory not found${NC}"
    ERRORS=$((ERRORS + 1))
fi

echo -n "package.json... "
if [ -f "package.json" ]; then
    echo -e "${GREEN}✓${NC}"
else
    echo -e "${RED}✗ package.json not found${NC}"
    ERRORS=$((ERRORS + 1))
fi

echo -n "pom.xml... "
if [ -f "backend/pom.xml" ]; then
    echo -e "${GREEN}✓${NC}"
else
    echo -e "${RED}✗ backend/pom.xml not found${NC}"
    ERRORS=$((ERRORS + 1))
fi

# Check ports
echo ""
echo "Checking ports..."

echo -n "Port 3000 (Frontend)... "
if lsof -Pi :3000 -sTCP:LISTEN -t >/dev/null 2>&1; then
    echo -e "${YELLOW}⚠ Port 3000 is in use${NC}"
else
    echo -e "${GREEN}✓ Available${NC}"
fi

echo -n "Port 8080 (Backend)... "
if lsof -Pi :8080 -sTCP:LISTEN -t >/dev/null 2>&1; then
    echo -e "${YELLOW}⚠ Port 8080 is in use${NC}"
else
    echo -e "${GREEN}✓ Available${NC}"
fi

echo -n "Port 11434 (Ollama)... "
if lsof -Pi :11434 -sTCP:LISTEN -t >/dev/null 2>&1; then
    echo -e "${GREEN}✓ Ollama is running${NC}"
else
    echo -e "${YELLOW}⚠ Ollama not running on port 11434${NC}"
fi

# Summary
echo ""
echo "========================================="
if [ $ERRORS -eq 0 ]; then
    echo -e "${GREEN}✓ Setup verification passed!${NC}"
    echo ""
    echo "You're ready to start the application:"
    echo "  ./start-all.sh"
else
    echo -e "${RED}✗ Found $ERRORS error(s)${NC}"
    echo ""
    echo "Please fix the errors above before starting."
    echo "See QUICKSTART.md for installation instructions."
fi
echo "========================================="
