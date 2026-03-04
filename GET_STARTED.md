# 🚀 Get Started with InterviewAI

## What You Have

A **complete, working AI-powered mock interview platform** with:
- ✅ Full-stack application (React + Spring Boot)
- ✅ AI integration (Ollama)
- ✅ Voice-based interviews
- ✅ Performance analytics
- ✅ All pages working
- ✅ Database setup
- ✅ Automated scripts
- ✅ Complete documentation

## 3-Step Quick Start

### Step 1: Install Prerequisites (5-10 minutes)

```bash
# Check what you have
./verify-setup.sh
```

If anything is missing, install:

**Java 17+**
```bash
# Ubuntu/Debian
sudo apt install openjdk-17-jdk

# macOS
brew install openjdk@17
```

**Maven**
```bash
# Ubuntu/Debian
sudo apt install maven

# macOS
brew install maven
```

**Node.js 18+**
```bash
# Ubuntu/Debian
curl -fsSL https://deb.nodesource.com/setup_18.x | sudo -E bash -
sudo apt install nodejs

# macOS
brew install node@18
```

**Ollama**
```bash
# Linux/macOS
curl -fsSL https://ollama.com/install.sh | sh

# Windows: Download from https://ollama.com/download
```

**AI Model**
```bash
ollama pull llama3
# Wait for ~4.7GB download
```

### Step 2: Start the Application (1 minute)

```bash
# Make scripts executable (first time only)
chmod +x *.sh

# Start everything
./start-all.sh
```

This will:
1. ✅ Start Ollama server
2. ✅ Build and start backend (port 8080)
3. ✅ Install dependencies and start frontend (port 3000)
4. ✅ Open your browser automatically

### Step 3: Test the Application (5 minutes)

1. **Open Browser**: http://localhost:3000

2. **Create Sample Resume**:
   ```bash
   ./create-sample-resume.sh
   # Open sample-resume.html in browser
   # Print to PDF (Ctrl+P or Cmd+P)
   # Save as sample-resume.pdf
   ```

3. **Complete Interview Flow**:
   - Click "Start Your Interview Preparation"
   - Upload sample-resume.pdf
   - Select "Java Developer"
   - Click "Start Interview"
   - Allow microphone access
   - Answer questions (or type answers)
   - Complete all questions
   - View your performance report

## What Each Page Does

### 1. Home Page (/)
- Landing page with features
- "Start Your Interview Preparation" button

### 2. Resume Upload (/upload)
- Drag-and-drop PDF upload
- AI analyzes your resume
- Extracts skills, projects, experience

### 3. Job Selection (/job-selection)
- Choose target role
- See resume summary
- Start interview

### 4. Interview Page (/interview)
- AI asks questions via voice
- Record your answers
- 90-second timer per question
- Speech-to-text transcription

### 5. Report Page (/report/:id)
- Overall score
- Technical/Communication scores
- Radar chart
- Question-wise feedback
- Improvement roadmap

## Testing Individual Components

### Test Ollama
```bash
./test-ollama.sh
```

### Test Backend
```bash
./test-backend.sh
```

### Test Frontend
Open http://localhost:3000 in browser

## Accessing Services

| Service | URL | Credentials |
|---------|-----|-------------|
| Frontend | http://localhost:3000 | - |
| Backend API | http://localhost:8080 | - |
| H2 Console | http://localhost:8080/h2-console | JDBC: `jdbc:h2:mem:interviewdb`<br>User: `sa`<br>Pass: (empty) |
| Ollama | http://localhost:11434 | - |

## Common Issues & Solutions

### "Ollama not found"
```bash
# Install Ollama
curl -fsSL https://ollama.com/install.sh | sh

# Pull model
ollama pull llama3
```

### "Port already in use"
```bash
# Find and kill process
lsof -i :3000  # or :8080
kill -9 <PID>
```

### "Microphone not working"
- Grant browser microphone permissions
- Use Chrome or Edge (best support)
- Check system microphone settings

### "Backend won't start"
```bash
# Check Java version
java -version  # Should be 17+

# Check logs
tail -f backend.log
```

### "Frontend won't start"
```bash
# Reinstall dependencies
rm -rf node_modules
npm install

# Check logs
tail -f frontend.log
```

## Stopping the Application

```bash
# Press Ctrl+C in the terminal where start-all.sh is running

# Or use stop script
./stop-all.sh
```

## Project Structure

```
interviewai/
├── src/                    # Frontend React code
│   ├── pages/             # 5 complete pages
│   └── services/          # API integration
├── backend/               # Spring Boot backend
│   └── src/main/java/    # Java code
├── *.sh                   # Automation scripts
└── *.md                   # Documentation
```

## Next Steps

### For Development
1. Modify frontend: Edit files in `src/`
2. Modify backend: Edit files in `backend/src/main/java/`
3. Changes auto-reload in development mode

### For Testing
1. Read TESTING.md for comprehensive test guide
2. Run automated tests
3. Test all user flows

### For Deployment
1. Read DEPLOYMENT.md for production setup
2. Configure PostgreSQL (replace H2)
3. Deploy to cloud platform

## Documentation Guide

| Document | Purpose |
|----------|---------|
| **GET_STARTED.md** | This file - Quick start |
| **README.md** | Complete project overview |
| **QUICKSTART.md** | Detailed setup instructions |
| **TESTING.md** | Testing guide |
| **DEPLOYMENT.md** | Production deployment |
| **PROJECT_SUMMARY.md** | What's been built |
| **InterviewAI_PRD.md** | Original requirements |

## Features Checklist

- ✅ Resume upload and AI analysis
- ✅ Job role selection (4 roles + custom)
- ✅ AI question generation (5-7 questions)
- ✅ Voice-based interview
- ✅ Speech-to-text recording
- ✅ 90-second timer per question
- ✅ Progress tracking
- ✅ AI evaluation (6 score dimensions)
- ✅ Performance report with charts
- ✅ Question-wise feedback
- ✅ Improvement roadmap
- ✅ Database persistence
- ✅ Error handling
- ✅ Responsive design

## Support

### Getting Help
1. Check documentation files
2. Review logs (backend.log, frontend.log)
3. Run verify-setup.sh
4. Check troubleshooting sections

### Reporting Issues
Include:
- Error message
- Steps to reproduce
- Log files
- System information

## Success Criteria

You'll know it's working when:
1. ✅ All services start without errors
2. ✅ Frontend loads at http://localhost:3000
3. ✅ You can upload a resume
4. ✅ Questions are generated
5. ✅ Interview completes
6. ✅ Report displays with scores

## Tips for Best Experience

1. **Use Chrome or Edge** for best speech recognition
2. **Speak clearly** during interview
3. **Allow microphone access** when prompted
4. **Wait for AI voice** to finish before recording
5. **Check Ollama is running** before starting

## What Makes This Special

- 🎯 **Complete**: Every feature from PRD implemented
- 🚀 **Ready**: One command to start everything
- 📚 **Documented**: Comprehensive guides included
- 🧪 **Tested**: Automated test scripts provided
- 🎨 **Professional**: Modern UI with Tailwind CSS
- 🤖 **AI-Powered**: Real LLM integration
- 🎤 **Voice-Based**: Realistic interview simulation
- 📊 **Analytics**: Detailed performance reports

## Time to First Interview

- Prerequisites installed: 10-15 minutes
- Application started: 1 minute
- First interview completed: 5 minutes
- **Total: ~20 minutes from zero to working interview!**

---

## Ready? Let's Go! 🚀

```bash
# 1. Verify everything is ready
./verify-setup.sh

# 2. Start the application
./start-all.sh

# 3. Open browser to http://localhost:3000

# 4. Start interviewing!
```

**Welcome to InterviewAI - Your AI Interview Coach!** 🎉
