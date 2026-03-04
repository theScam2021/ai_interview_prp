#!/bin/bash

echo "=== InterviewAI Backend Test Script ==="
echo ""

# Colors
GREEN='\033[0;32m'
RED='\033[0;31m'
NC='\033[0m'

# Test 1: Check if backend is running
echo "Test 1: Checking if backend is running..."
if curl -s http://localhost:8080 > /dev/null; then
    echo -e "${GREEN}✓ Backend is running${NC}"
else
    echo -e "${RED}✗ Backend is not running. Start it with: cd backend && mvn spring-boot:run${NC}"
    exit 1
fi

# Test 2: Test question generation
echo ""
echo "Test 2: Testing question generation API..."
RESPONSE=$(curl -s -X POST http://localhost:8080/api/interview/generate-questions \
  -H "Content-Type: application/json" \
  -d '{
    "resumeSummary": {
      "skills": ["Java", "Spring Boot", "React"],
      "projects": ["E-commerce Platform"],
      "experience": "2 years",
      "strengths": ["Problem solving"],
      "missingSkills": ["Docker"]
    },
    "jobRole": "Java Developer"
  }')

if echo "$RESPONSE" | grep -q "questions"; then
    echo -e "${GREEN}✓ Question generation working${NC}"
    echo "Sample questions generated:"
    echo "$RESPONSE" | head -n 5
else
    echo -e "${RED}✗ Question generation failed${NC}"
    echo "Response: $RESPONSE"
fi

# Test 3: Test interview start
echo ""
echo "Test 3: Testing interview start API..."
START_RESPONSE=$(curl -s -X POST http://localhost:8080/api/interview/start \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "jobRole": "Java Developer",
    "resumeSummary": {
      "skills": ["Java"],
      "projects": [],
      "experience": "2 years",
      "strengths": [],
      "missingSkills": []
    }
  }')

if echo "$START_RESPONSE" | grep -q "sessionId"; then
    echo -e "${GREEN}✓ Interview start working${NC}"
    SESSION_ID=$(echo "$START_RESPONSE" | grep -o '"sessionId":[0-9]*' | grep -o '[0-9]*')
    echo "Session ID: $SESSION_ID"
else
    echo -e "${RED}✗ Interview start failed${NC}"
    echo "Response: $START_RESPONSE"
fi

# Test 4: Check H2 Console
echo ""
echo "Test 4: Checking H2 Console..."
if curl -s http://localhost:8080/h2-console > /dev/null; then
    echo -e "${GREEN}✓ H2 Console accessible at http://localhost:8080/h2-console${NC}"
else
    echo -e "${RED}✗ H2 Console not accessible${NC}"
fi

echo ""
echo "=== Test Summary ==="
echo "Backend URL: http://localhost:8080"
echo "H2 Console: http://localhost:8080/h2-console"
echo "JDBC URL: jdbc:h2:mem:interviewdb"
echo "Username: sa"
echo "Password: (empty)"
