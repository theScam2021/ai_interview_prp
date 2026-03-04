# Deployment Guide

## Quick Start Checklist

- [ ] Ollama installed and running
- [ ] AI model downloaded (llama3 or mistral)
- [ ] Java 17+ installed
- [ ] Node.js 18+ installed
- [ ] Maven installed

## Local Development

### 1. Start Ollama
```bash
ollama serve
```

### 2. Start Backend
```bash
cd backend
mvn spring-boot:run
```

### 3. Start Frontend
```bash
npm install
npm run dev
```

### 4. Access Application
- Frontend: http://localhost:3000
- Backend: http://localhost:8080
- H2 Console: http://localhost:8080/h2-console

## Production Deployment

### Option 1: Docker Deployment

Create `docker-compose.yml`:

```yaml
version: '3.8'
services:
  backend:
    build: ./backend
    ports:
      - "8080:8080"
    environment:
      - OLLAMA_BASE_URL=http://ollama:11434
      - SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/interviewai
    depends_on:
      - db
      - ollama
  
  frontend:
    build: .
    ports:
      - "3000:80"
    depends_on:
      - backend
  
  db:
    image: postgres:15
    environment:
      - POSTGRES_DB=interviewai
      - POSTGRES_PASSWORD=password
    volumes:
      - postgres_data:/var/lib/postgresql/data
  
  ollama:
    image: ollama/ollama
    ports:
      - "11434:11434"
    volumes:
      - ollama_data:/root/.ollama

volumes:
  postgres_data:
  ollama_data:
```

### Option 2: Cloud Deployment

#### Backend (Railway/Heroku)
1. Add PostgreSQL addon
2. Update application.properties
3. Deploy JAR file

#### Frontend (Vercel/Netlify)
1. Build: `npm run build`
2. Deploy dist/ folder
3. Set environment variables

#### Ollama (Separate Server)
1. Deploy on GPU-enabled instance
2. Expose API endpoint
3. Update backend configuration

## Environment Variables

### Backend (.env or application.properties)
```properties
OLLAMA_BASE_URL=http://localhost:11434
OLLAMA_MODEL=llama3
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/interviewai
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=password
```

### Frontend (.env)
```
VITE_API_BASE_URL=http://localhost:8080/api
```

## Performance Optimization

1. **Enable caching** for AI responses
2. **Use CDN** for frontend assets
3. **Implement rate limiting** on API
4. **Optimize PDF processing** with streaming
5. **Add Redis** for session management

## Security Checklist

- [ ] Enable HTTPS
- [ ] Add authentication
- [ ] Implement rate limiting
- [ ] Sanitize file uploads
- [ ] Secure database credentials
- [ ] Add CORS whitelist
- [ ] Enable security headers

## Monitoring

- Add logging (Logback/Winston)
- Set up error tracking (Sentry)
- Monitor API performance
- Track Ollama response times
- Database query optimization

## Backup Strategy

- Database: Daily automated backups
- User data: Encrypted storage
- Logs: Rotate and archive

## Scaling Considerations

- Horizontal scaling for backend
- Load balancer for multiple instances
- Separate Ollama service
- CDN for static assets
- Database read replicas
