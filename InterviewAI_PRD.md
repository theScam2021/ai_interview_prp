# InterviewAI -- Product Requirements Document (PRD)

## 1. Product Overview

### Product Name

InterviewAI -- Realistic AI Mock Interview Simulator

### Vision

To build a realistic AI-powered mock interview system that: - Analyzes
resumes - Generates personalized job-specific questions - Conducts
voice-based interviews using an AI avatar - Records responses -
Evaluates performance - Provides detailed improvement reports

### Target Users

-   College students
-   Job seekers
-   Internship aspirants
-   Final-year engineering students

------------------------------------------------------------------------

## 2. Problem Statement

Students lack access to realistic interview simulations and structured
feedback. Most mock interview platforms are paid, generic, or not
resume-personalized.

InterviewAI solves this by providing: - Resume-driven personalized
interviews - Voice-based realistic simulation - AI-powered evaluation
with improvement roadmap

------------------------------------------------------------------------

## 3. System Architecture

React (Port 3000) ↓ Spring Boot (Port 8080) ↓ Ollama (Local AI)

------------------------------------------------------------------------

## 4. Core Features (MVP)

### 4.1 Resume Upload & Analysis

-   Upload PDF resume
-   Extract text using Apache PDFBox
-   Send to Ollama for analysis
-   Extract:
    -   Skills
    -   Projects
    -   Experience
    -   Strengths
    -   Missing skills

Output: Structured JSON resume summary

------------------------------------------------------------------------

### 4.2 Job Role Selection

User selects role: - Java Developer - Frontend Developer - Data
Analyst - Custom role

System tailors interview based on selected role.

------------------------------------------------------------------------

### 4.3 AI Question Generation

-   5--10 technical questions
-   2 behavioral questions
-   1 project deep-dive question
-   Follow-up questions

All resume-personalized.

------------------------------------------------------------------------

### 4.4 AI Interview Flow

-   Static professional avatar
-   AI voice using SpeechSynthesis API
-   Timer (60--90 seconds per question)
-   Microphone recording
-   One question at a time
-   No skipping

Goal: Simulate real interview pressure

------------------------------------------------------------------------

### 4.5 Recording System

Record: - Audio - Transcript (Speech-to-Text) - Session data

Store interview session locally or in database.

------------------------------------------------------------------------

### 4.6 AI Evaluation Engine

After interview completion, AI evaluates: - Technical depth -
Communication clarity - Answer structure - Confidence (language-based) -
Relevance

Returns: - Overall Score (out of 100) - Per-question feedback -
Strengths - Weaknesses - Suggested topics - Personalized improvement
roadmap

------------------------------------------------------------------------

### 4.7 Performance Report Dashboard

Displays: - Overall Score - Technical Score - Communication Score -
Improvement Areas - Recommended Study Plan - Optional Radar Chart

------------------------------------------------------------------------

## 5. Non-Functional Requirements

  Requirement   Description
  ------------- -------------------------------
  Performance   Response under 3 seconds
  Security      Resume not permanently stored
  Scalability   Modular AI prompts
  Usability     Professional UI
  Reliability   Local AI fallback

------------------------------------------------------------------------

## 6. Tech Stack

### Frontend

-   React
-   Tailwind CSS
-   Axios
-   Web Speech API
-   SpeechSynthesis API
-   MediaRecorder API
-   Recharts

### Backend

-   Spring Boot
-   Spring Web
-   Spring Validation
-   Lombok
-   Apache PDFBox
-   H2 / PostgreSQL
-   WebClient / RestTemplate

### AI Layer

-   Ollama (Local)
-   Llama3 or Mistral model

All tools are free and locally deployable.

------------------------------------------------------------------------

## 7. Database Schema

### User

-   id
-   name
-   email (optional)

### InterviewSession

-   id
-   userId
-   jobRole
-   resumeSummary
-   overallScore
-   createdAt

### Question

-   id
-   sessionId
-   questionText

### Answer

-   id
-   questionId
-   transcript
-   score
-   feedback

------------------------------------------------------------------------

## 8. AI Prompt Strategy

### Resume Analysis Prompt

Extract structured JSON summary.

### Question Generation Prompt

Generate personalized questions based on resume + job role.

### Evaluation Prompt

Provide scores, feedback, and improvement roadmap.

------------------------------------------------------------------------

## 9. Development Phases

### Phase 1

-   Resume upload
-   AI question generation

### Phase 2

-   Interview UI
-   Voice system
-   Recording

### Phase 3

-   AI evaluation
-   Report dashboard

### Phase 4

-   UI polishing
-   Error handling
-   Optimization

------------------------------------------------------------------------

## 10. Future Enhancements

-   Animated AI avatar
-   Emotion detection
-   Eye tracking
-   Multi-language support
-   Cloud deployment
-   Recruiter analytics mode

------------------------------------------------------------------------

## 11. Resume Description (For Internship)

Designed and developed a full-stack AI-powered mock interview platform
using React, Spring Boot, and Ollama LLM that analyzes resumes, conducts
voice-based interviews, records responses, and generates performance
analytics reports.
