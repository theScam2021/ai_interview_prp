# Quick Start Guide

## Prerequisites Installation

### 1. Install Java 17+
```bash
# Ubuntu/Debian
sudo apt update
sudo apt install openjdk-17-jdk

# macOS
brew install openjdk@17

# Verify
java -version
```

### 2. Install Maven
```bash
# Ubuntu/Debian
sudo apt install maven

# macOS
brew install maven

# Verify
mvn -version
```

### 3. Install Node.js 18+
```bash
# Ubuntu/Debian
curl -fsSL https://deb.nodesource.com/setup_18.x | sudo -E bash -
sudo apt install nodejs

# macOS
brew install node@18

# Verify
node --version
npm --version
```

### 4. Install Ollama
```bash
# Linux/macOS
curl -fsSL https://ollama.com/install.sh | sh

# Windows
# Download from https://ollama.com/download

# Verify
ollama --version
```

### 5. Download AI Model
```bash
ollama pull llama3
# This will download ~4.7GB
```

## Quick Start (Automated)

```bash
# Make scripts executable
chmod +x start-all.sh stop-all.sh test-*.sh

# Start everything
./start-all.sh
```

The script will:
1. Check all prerequisites
2. Start Ollama server
3. Build and start backend
4. Install dependencies and start frontend
5. Open the application

## Manual Start

### Terminal 1: Start Ollama
```bash
ollama serve
```

### Terminal 2: Start Backend
```bash
cd backend
mvn spring-boot:run
```

### Terminal 3: Start Frontend
```bash
npm install
npm run dev
```

## Access the Application

- **Frontend**: http://localhost:3000
- **Backend API**: http://localhost:8080
- **H2 Database Console**: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:interviewdb`
  - Username: `sa`
  - Password: (leave empty)

## Testing the Application

### 1. Test Ollama
```bash
chmod +x test-ollama.sh
./test-ollama.sh
```

### 2. Test Backend
```bash
chmod +x test-backend.sh
./test-backend.sh
```

### 3. Test Full Flow

1. Open http://localhost:3000
2. Click "Start Your Interview Preparation"
3. Upload a PDF resume (create a sample if needed)
4. Select "Java Developer" role
5. Click "Start Interview"
6. Allow microphone access
7. Click "Start Recording" and answer questions
8. Complete the interview
9. View your performance report

## Creating a Sample Resume

If you don't have a PDF resume, create one with this content:

```
John Doe
Software Developer

Skills:
- Java, Spring Boot, Hibernate
- React, JavaScript, HTML/CSS
- MySQL, PostgreSQL
- Git, Maven, Docker

Experience:
Software Developer at Tech Corp (2021-2023)
- Developed REST APIs using Spring Boot
- Built responsive web applications with React
- Implemented microservices architecture

Projects:
1. E-commerce Platform
   - Full-stack web application
   - Technologies: Java, Spring Boot, React, MySQL

2. Task Management System
   - Real-time collaboration tool
   - Technologies: Node.js, React, MongoDB

Education:
Bachelor of Technology in Computer Science
University Name (2017-2021)
```

Save as PDF and upload to the application.

## Troubleshooting

### Ollama not responding
```bash
# Check if running
pgrep ollama

# Restart
pkill ollama
ollama serve
```

### Backend port already in use
```bash
# Find process on port 8080
lsof -i :8080

# Kill it
kill -9 <PID>
```

### Frontend port already in use
```bash
# Find process on port 3000
lsof -i :3000

# Kill it
kill -9 <PID>
```

### Microphone not working
- Grant browser microphone permissions
- Use Chrome or Edge (best compatibility)
- Check system microphone settings

### PDF upload fails
- Ensure file is valid PDF
- Check file size (max 5MB)
- Try a different PDF

## Next Steps

1. Complete a full interview
2. Review your performance report
3. Try different job roles
4. Upload different resumes
5. Check the database in H2 Console

## Stopping the Application

```bash
# If using start-all.sh
Press Ctrl+C

# Or use stop script
./stop-all.sh

# Manual stop
pkill -f "vite"
pkill -f "spring-boot"
```

## Getting Help

- Check logs: `tail -f backend.log` or `tail -f frontend.log`
- Review README.md for detailed documentation
- Check DEPLOYMENT.md for production setup
- Open GitHub issue for bugs

## Development Tips

- Backend auto-reloads on code changes (with Spring DevTools)
- Frontend hot-reloads automatically
- Check H2 console to inspect database
- Use browser DevTools for frontend debugging
- Check backend.log for API errors
