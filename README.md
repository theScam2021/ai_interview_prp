# InterviewAI - AI-Powered Mock Interview Platform

A complete full-stack application that provides realistic AI-powered mock interviews with resume analysis, voice-based interviews, and detailed performance reports.

## Features

- **Resume Upload & Analysis**: Upload PDF resumes and get AI-powered analysis
- **Job Role Selection**: Choose from predefined roles or enter custom positions
- **AI Interview Simulation**: Voice-based interviews with AI-generated questions
- **Real-time Recording**: Speech-to-text transcription during interviews
- **Performance Evaluation**: Detailed scoring and feedback on all aspects
- **Improvement Roadmap**: Personalized study plan based on performance

## Tech Stack

### Frontend
- React 18
- Vite
- Tailwind CSS
- React Router
- Axios
- Recharts
- Web Speech API

### Backend
- Spring Boot 3.2
- Java 17
- H2 Database
- Apache PDFBox
- Spring Data JPA
- WebFlux

### AI
- Ollama (Local LLM)
- Llama3 or Mistral model

## Prerequisites

1. **Node.js** (v18 or higher)
2. **Java** (JDK 17 or higher)
3. **Maven** (3.8 or higher)
4. **Ollama** (for AI features)

## Installation & Setup

### 1. Install Ollama

```bash
# Linux/Mac
curl -fsSL https://ollama.com/install.sh | sh

# Windows - Download from https://ollama.com/download
```

### 2. Pull AI Model

```bash
ollama pull llama3
# or
ollama pull mistral
```

### 3. Start Ollama Server

```bash
ollama serve
```

### 4. Backend Setup

```bash
cd backend

# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

Backend will start on `http://localhost:8080`

### 5. Frontend Setup

```bash
# Install dependencies
npm install

# Start development server
npm run dev
```

Frontend will start on `http://localhost:3000`

## Testing the Application

### Step 1: Test Backend API

```bash
# Check if backend is running
curl http://localhost:8080/api/interview/generate-questions \
  -H "Content-Type: application/json" \
  -d '{"resumeSummary":{"skills":["Java"],"projects":[],"experience":"2 years","strengths":[],"missingSkills":[]},"jobRole":"Java Developer"}'
```

### Step 2: Test Frontend

1. Open browser to `http://localhost:3000`
2. Click "Start Your Interview Preparation"
3. Upload a PDF resume
4. Select a job role
5. Complete the interview
6. View your performance report

### Step 3: Test Individual Features

#### Resume Upload
- Upload a valid PDF resume
- Verify AI analysis appears

#### Job Selection
- Select different job roles
- Try custom role input

#### Interview Flow
- Test microphone permissions
- Verify speech-to-text works
- Check timer functionality
- Test question navigation

#### Report Generation
- Complete full interview
- Verify all scores display
- Check radar chart renders
- Review feedback sections

## Project Structure

```
interviewai/
├── backend/
│   ├── src/main/java/com/interviewai/
│   │   ├── config/          # Configuration classes
│   │   ├── controller/      # REST controllers
│   │   ├── dto/             # Data transfer objects
│   │   ├── entity/          # JPA entities
│   │   ├── repository/      # Data repositories
│   │   └── service/         # Business logic
│   └── pom.xml
├── src/
│   ├── pages/               # React page components
│   ├── services/            # API service layer
│   ├── App.jsx              # Main app component
│   └── main.jsx             # Entry point
├── package.json
└── README.md
```

## API Endpoints

### Resume
- `POST /api/resume/upload` - Upload and analyze resume

### Interview
- `POST /api/interview/generate-questions` - Generate interview questions
- `POST /api/interview/start` - Start new interview session
- `POST /api/interview/submit-answer` - Submit answer for a question
- `POST /api/interview/complete/{sessionId}` - Complete interview and get evaluation
- `GET /api/interview/report/{sessionId}` - Get interview report

## Database

The application uses H2 in-memory database. Access the console at:
- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:interviewdb`
- Username: `sa`
- Password: (empty)

## Building for Production

### Frontend

```bash
npm run build
```

Build output will be in `dist/` directory.

### Backend

```bash
cd backend
mvn clean package
java -jar target/interview-ai-backend-1.0.0.jar
```

## Deployment

### Frontend Deployment (Vercel/Netlify)

1. Build the project: `npm run build`
2. Deploy `dist/` folder
3. Update API base URL in `src/services/api.js`

### Backend Deployment (Heroku/Railway)

1. Add PostgreSQL database
2. Update `application.properties` with production database
3. Deploy JAR file

### Ollama Deployment

For production, consider:
- Running Ollama on a dedicated server
- Using cloud GPU instances
- Implementing API rate limiting

## Troubleshooting

### Ollama Connection Issues
- Ensure Ollama is running: `ollama serve`
- Check model is downloaded: `ollama list`
- Verify port 11434 is accessible

### Microphone Not Working
- Grant browser microphone permissions
- Use HTTPS in production (required for Web Speech API)
- Check browser compatibility (Chrome/Edge recommended)

### CORS Errors
- Verify backend CORS configuration
- Check frontend proxy settings in `vite.config.js`

### PDF Upload Fails
- Ensure file is valid PDF
- Check file size (max 5MB)
- Verify PDFBox dependency is included

## Browser Compatibility

- Chrome/Edge: Full support
- Firefox: Limited speech recognition support
- Safari: Partial support

## Future Enhancements

- [ ] Animated AI avatar
- [ ] Emotion detection via webcam
- [ ] Multi-language support
- [ ] Video recording
- [ ] Recruiter dashboard
- [ ] Interview history tracking
- [ ] Social authentication
- [ ] Email reports

## License

MIT License

## Contributing

Pull requests are welcome. For major changes, please open an issue first.

## Support

For issues and questions, please open a GitHub issue.
