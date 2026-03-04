#!/bin/bash

echo "=== Ollama Test Script ==="
echo ""

# Colors
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m'

# Test 1: Check if Ollama is running
echo "Test 1: Checking if Ollama is running..."
if curl -s http://localhost:11434/api/tags > /dev/null; then
    echo -e "${GREEN}✓ Ollama is running${NC}"
else
    echo -e "${RED}✗ Ollama is not running${NC}"
    echo -e "${YELLOW}Start Ollama with: ollama serve${NC}"
    exit 1
fi

# Test 2: List available models
echo ""
echo "Test 2: Listing available models..."
MODELS=$(curl -s http://localhost:11434/api/tags)
echo "$MODELS"

if echo "$MODELS" | grep -q "llama3\|mistral"; then
    echo -e "${GREEN}✓ AI model found${NC}"
else
    echo -e "${YELLOW}⚠ No llama3 or mistral model found${NC}"
    echo "Install with: ollama pull llama3"
    echo "or: ollama pull mistral"
fi

# Test 3: Test AI generation
echo ""
echo "Test 3: Testing AI generation..."
RESPONSE=$(curl -s -X POST http://localhost:11434/api/generate \
  -H "Content-Type: application/json" \
  -d '{
    "model": "llama3",
    "prompt": "Say hello in one word",
    "stream": false
  }')

if echo "$RESPONSE" | grep -q "response"; then
    echo -e "${GREEN}✓ AI generation working${NC}"
    echo "AI Response: $(echo "$RESPONSE" | grep -o '"response":"[^"]*"' | cut -d'"' -f4)"
else
    echo -e "${RED}✗ AI generation failed${NC}"
    echo "Response: $RESPONSE"
    echo ""
    echo "Trying with mistral model..."
    RESPONSE=$(curl -s -X POST http://localhost:11434/api/generate \
      -H "Content-Type: application/json" \
      -d '{
        "model": "mistral",
        "prompt": "Say hello in one word",
        "stream": false
      }')
    
    if echo "$RESPONSE" | grep -q "response"; then
        echo -e "${GREEN}✓ AI generation working with mistral${NC}"
    else
        echo -e "${RED}✗ Both models failed. Please install a model:${NC}"
        echo "ollama pull llama3"
    fi
fi

echo ""
echo "=== Ollama Status ==="
echo "Ollama URL: http://localhost:11434"
echo "Available models: Run 'ollama list' to see all models"
