#!/bin/bash

echo "Stopping InterviewAI services..."

# Stop frontend (Vite)
pkill -f "vite"

# Stop backend (Spring Boot)
pkill -f "spring-boot"

# Stop Ollama (optional - uncomment if you want to stop it)
# pkill -f "ollama"

echo "All services stopped."
