# Testing Guide

## Automated Tests

### 1. Verify Setup
```bash
./verify-setup.sh
```

This checks:
- Java 17+ installation
- Maven installation
- Node.js 18+ installation
- npm installation
- Ollama installation and status
- AI model availability
- Project structure
- Port availability

### 2. Test Ollama
```bash
./test-ollama.sh
```

This tests:
- Ollama server connectivity
- Available AI models
- AI text generation

### 3. Test Backend
```bash
./test-backend.sh
```

This tests:
- Backend server status
- Question generation API
- Interview start API
- H2 database console

## Manual Testing Checklist

### Frontend Testing

#### Home Page
- [ ] Page loads correctly
- [ ] All feature cards display
- [ ] "Start Your Interview Preparation" button works
- [ ] Navigation to upload page works

#### Resume Upload Page
- [ ] Drag and drop area displays
- [ ] File selection dialog opens
- [ ] PDF validation works (rejects non-PDF files)
- [ ] File size validation (max 5MB)
- [ ] Upload progress indicator shows
- [ ] Success message displays
- [ ] Navigation to job selection works
- [ ] Back button returns to home

#### Job Selection Page
- [ ] Resume summary displays correctly
- [ ] All job role cards display
- [ ] Role selection highlights card
- [ ] Custom role input appears when selected
- [ ] Custom role text input works
- [ ] "Start Interview" button enables/disables correctly
- [ ] Navigation to interview page works
- [ ] Back button returns to upload page

#### Interview Page
- [ ] Questions load correctly
- [ ] AI voice speaks questions (check audio)
- [ ] Progress bar updates
- [ ] Timer counts down correctly
- [ ] Timer turns red at 10 seconds
- [ ] Microphone permission request appears
- [ ] "Start Recording" button works
- [ ] Recording indicator shows (pulsing red)
- [ ] Speech-to-text transcription works
- [ ] Transcript appears in textarea
- [ ] Manual text editing works
- [ ] "Stop Recording" button works
- [ ] "Next Question" button works
- [ ] Question navigation works
- [ ] "Complete Interview" button appears on last question
- [ ] Navigation to report page works

#### Report Page
- [ ] Overall score displays
- [ ] Technical score displays
- [ ] Communication score displays
- [ ] Radar chart renders correctly
- [ ] Strengths list displays
- [ ] Weaknesses list displays
- [ ] Question-wise feedback displays
- [ ] Improvement roadmap displays
- [ ] "Back to Home" button works
- [ ] "Take Another Interview" button works

### Backend Testing

#### Resume Upload API
```bash
# Create a test PDF first
echo "Test Resume Content" > test.txt
# Convert to PDF (requires tools like pandoc or wkhtmltopdf)

# Test upload
curl -X POST http://localhost:8080/api/resume/upload \
  -F "file=@test.pdf"
```

Expected: JSON with skills, projects, experience, strengths, missingSkills

#### Question Generation API
```bash
curl -X POST http://localhost:8080/api/interview/generate-questions \
  -H "Content-Type: application/json" \
  -d '{
    "resumeSummary": {
      "skills": ["Java", "Spring Boot"],
      "projects": ["E-commerce"],
      "experience": "2 years",
      "strengths": ["Problem solving"],
      "missingSkills": ["Docker"]
    },
    "jobRole": "Java Developer"
  }'
```

Expected: JSON with array of questions

#### Start Interview API
```bash
curl -X POST http://localhost:8080/api/interview/start \
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
  }'
```

Expected: JSON with sessionId

#### Submit Answer API
```bash
curl -X POST http://localhost:8080/api/interview/submit-answer \
  -H "Content-Type: application/json" \
  -d '{
    "sessionId": 1,
    "questionId": 1,
    "transcript": "I have 2 years of experience with Java and Spring Boot..."
  }'
```

Expected: 200 OK

#### Complete Interview API
```bash
curl -X POST http://localhost:8080/api/interview/complete/1
```

Expected: JSON with evaluation scores and feedback

#### Get Report API
```bash
curl http://localhost:8080/api/interview/report/1
```

Expected: JSON with complete interview report

### Database Testing

1. Access H2 Console: http://localhost:8080/h2-console
2. Login with:
   - JDBC URL: `jdbc:h2:mem:interviewdb`
   - Username: `sa`
   - Password: (empty)

3. Run queries:
```sql
-- View all sessions
SELECT * FROM INTERVIEW_SESSIONS;

-- View all questions
SELECT * FROM QUESTIONS;

-- View all answers
SELECT * FROM ANSWERS;

-- View session with questions
SELECT s.id, s.job_role, s.overall_score, q.question_text
FROM INTERVIEW_SESSIONS s
LEFT JOIN QUESTIONS q ON s.id = q.session_id;
```

### Integration Testing

#### Full Flow Test
1. Start all services
2. Open http://localhost:3000
3. Upload resume
4. Select job role
5. Complete interview (answer all questions)
6. View report
7. Check database for saved data
8. Verify all scores are calculated

#### Error Handling Test
- [ ] Upload invalid file (not PDF)
- [ ] Upload oversized file (>5MB)
- [ ] Try to start interview without resume
- [ ] Try to navigate without completing steps
- [ ] Test with microphone denied
- [ ] Test with no internet (Ollama offline)
- [ ] Test with backend offline
- [ ] Test with invalid session ID

### Performance Testing

#### Load Test
```bash
# Install Apache Bench
sudo apt install apache2-utils

# Test question generation endpoint
ab -n 100 -c 10 -p request.json -T application/json \
  http://localhost:8080/api/interview/generate-questions
```

#### Response Time Test
- Resume upload: < 3 seconds
- Question generation: < 5 seconds
- Interview start: < 2 seconds
- Answer submission: < 1 second
- Report generation: < 5 seconds

### Browser Compatibility

Test on:
- [ ] Chrome (latest)
- [ ] Firefox (latest)
- [ ] Edge (latest)
- [ ] Safari (latest)

Features to verify:
- Speech Recognition (Chrome/Edge best)
- Speech Synthesis (all browsers)
- File upload
- Responsive design
- Audio playback

### Mobile Testing

Test on mobile devices:
- [ ] Responsive layout
- [ ] Touch interactions
- [ ] File upload from mobile
- [ ] Microphone access
- [ ] Audio playback

### Security Testing

- [ ] CORS properly configured
- [ ] File upload validation
- [ ] SQL injection prevention (JPA handles this)
- [ ] XSS prevention (React handles this)
- [ ] File size limits enforced
- [ ] No sensitive data in logs

## Test Data

### Sample Resume Content
```
John Doe
john.doe@email.com | (555) 123-4567

SKILLS
- Programming: Java, Python, JavaScript, TypeScript
- Frameworks: Spring Boot, React, Node.js, Express
- Databases: MySQL, PostgreSQL, MongoDB
- Tools: Git, Docker, Maven, Jenkins

EXPERIENCE
Senior Software Developer | Tech Corp | 2021-2023
- Developed microservices using Spring Boot
- Built responsive web applications with React
- Implemented CI/CD pipelines
- Mentored junior developers

Software Developer | StartupXYZ | 2019-2021
- Created REST APIs
- Worked on full-stack features
- Participated in agile ceremonies

PROJECTS
E-commerce Platform
- Full-stack application with 10,000+ users
- Technologies: Java, Spring Boot, React, MySQL
- Implemented payment gateway integration

Task Management System
- Real-time collaboration tool
- Technologies: Node.js, React, WebSocket, MongoDB
- Deployed on AWS

EDUCATION
Bachelor of Technology in Computer Science
University Name | 2015-2019
GPA: 3.8/4.0
```

### Sample API Requests

See `test-backend.sh` for complete examples.

## Continuous Testing

### During Development
```bash
# Watch backend logs
tail -f backend.log

# Watch frontend logs
tail -f frontend.log

# Monitor Ollama
watch -n 1 'curl -s http://localhost:11434/api/tags'
```

### Before Deployment
1. Run all automated tests
2. Complete full manual flow
3. Check all error scenarios
4. Verify database persistence
5. Test with different resumes
6. Test with different job roles
7. Verify report accuracy

## Troubleshooting Tests

If tests fail:
1. Check service status (./verify-setup.sh)
2. Review logs (backend.log, frontend.log)
3. Verify Ollama is running
4. Check port availability
5. Ensure database is accessible
6. Clear browser cache
7. Restart services

## Test Results Documentation

Create a test report with:
- Date and time of testing
- Environment details
- Test cases executed
- Pass/fail status
- Issues found
- Screenshots of errors
- Performance metrics
