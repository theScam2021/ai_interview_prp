package com.interviewai.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.interviewai.dto.*;
import com.interviewai.entity.Answer;
import com.interviewai.entity.InterviewSession;
import com.interviewai.entity.Question;
import com.interviewai.repository.AnswerRepository;
import com.interviewai.repository.InterviewSessionRepository;
import com.interviewai.repository.QuestionRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InterviewService {
    
    private final OllamaService ollamaService;
    private final InterviewSessionRepository sessionRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final ObjectMapper objectMapper;
    
    public InterviewService(
            OllamaService ollamaService,
            InterviewSessionRepository sessionRepository,
            QuestionRepository questionRepository,
            AnswerRepository answerRepository) {
        this.ollamaService = ollamaService;
        this.sessionRepository = sessionRepository;
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
        this.objectMapper = new ObjectMapper();
    }
    
    public QuestionGenerationResponse generateQuestions(ResumeSummary resumeSummary, String jobRole) {
        String prompt = buildQuestionGenerationPrompt(resumeSummary, jobRole);
        String aiResponse = ollamaService.generateResponse(prompt);
        
        List<QuestionDTO> questions = parseQuestions(aiResponse);
        
        QuestionGenerationResponse response = new QuestionGenerationResponse();
        response.setQuestions(questions);
        return response;
    }
    
    public StartInterviewResponse startInterview(StartInterviewRequest request) {
        InterviewSession session = new InterviewSession();
        session.setUserId(request.getUserId());
        session.setJobRole(request.getJobRole());
        
        try {
            session.setResumeSummary(objectMapper.writeValueAsString(request.getResumeSummary()));
        } catch (Exception e) {
            session.setResumeSummary("{}");
        }
        
        session = sessionRepository.save(session);
        
        QuestionGenerationResponse questionsResponse = generateQuestions(
            request.getResumeSummary(), 
            request.getJobRole()
        );
        
        for (QuestionDTO questionDTO : questionsResponse.getQuestions()) {
            Question question = new Question();
            question.setSessionId(session.getId());
            question.setQuestionText(questionDTO.getQuestionText());
            question.setCategory(questionDTO.getCategory());
            Question saved = questionRepository.save(question);
            questionDTO.setId(saved.getId());
        }
        
        StartInterviewResponse response = new StartInterviewResponse();
        response.setSessionId(session.getId());
        return response;
    }
    
    public void submitAnswer(SubmitAnswerRequest request) {
        Answer answer = new Answer();
        answer.setQuestionId(request.getQuestionId());
        answer.setTranscript(request.getTranscript());
        answerRepository.save(answer);
    }
    
    public InterviewReport completeInterview(Long sessionId) {
        InterviewSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));
        
        List<Question> questions = questionRepository.findBySessionId(sessionId);
        List<Answer> allAnswers = new ArrayList<>();
        
        for (Question question : questions) {
            List<Answer> answers = answerRepository.findByQuestionId(question.getId());
            allAnswers.addAll(answers);
        }
        
        // Evaluate each answer individually with AI
        List<QuestionFeedback> feedbackList = new ArrayList<>();
        int totalScore = 0;
        int answeredCount = 0;
        
        for (int i = 0; i < questions.size(); i++) {
            Question question = questions.get(i);
            Answer answer = i < allAnswers.size() ? allAnswers.get(i) : null;
            
            QuestionFeedback feedback = evaluateAnswer(question, answer);
            feedbackList.add(feedback);
            
            // Only count answered questions in average
            if (answer != null && !isEmptyAnswer(answer.getTranscript())) {
                totalScore += feedback.getScore();
                answeredCount++;
            }
        }
        
        // Get overall evaluation from AI
        String evaluationPrompt = buildEvaluationPrompt(questions, allAnswers);
        String aiResponse = ollamaService.generateResponse(evaluationPrompt);
        InterviewReport report = parseEvaluationReport(aiResponse);
        
        // Adjust overall score based on actual answers
        if (answeredCount > 0) {
            int avgScore = totalScore / answeredCount;
            report.setOverallScore(avgScore);
            
            // Adjust other scores proportionally
            double factor = avgScore / 75.0; // 75 is default score
            report.setTechnicalScore((int)(report.getTechnicalScore() * factor));
            report.setCommunicationScore((int)(report.getCommunicationScore() * factor));
            report.setConfidenceScore((int)(report.getConfidenceScore() * factor));
            report.setStructureScore((int)(report.getStructureScore() * factor));
            report.setRelevanceScore((int)(report.getRelevanceScore() * factor));
        } else {
            // No answers provided
            report.setOverallScore(0);
            report.setTechnicalScore(0);
            report.setCommunicationScore(0);
            report.setConfidenceScore(0);
            report.setStructureScore(0);
            report.setRelevanceScore(0);
            report.getWeaknesses().add("No answers provided");
        }
        
        // Save to session
        session.setOverallScore(report.getOverallScore());
        session.setTechnicalScore(report.getTechnicalScore());
        session.setCommunicationScore(report.getCommunicationScore());
        session.setConfidenceScore(report.getConfidenceScore());
        session.setStructureScore(report.getStructureScore());
        session.setRelevanceScore(report.getRelevanceScore());
        session.setStrengths(String.join(", ", report.getStrengths()));
        session.setWeaknesses(String.join(", ", report.getWeaknesses()));
        session.setImprovementRoadmap(String.join(", ", report.getImprovementRoadmap()));
        
        sessionRepository.save(session);
        
        report.setQuestionFeedback(feedbackList);
        return report;
    }
    
    private QuestionFeedback evaluateAnswer(Question question, Answer answer) {
        QuestionFeedback feedback = new QuestionFeedback();
        feedback.setQuestion(question.getQuestionText());
        
        // Check if answer exists and is not empty
        if (answer == null || isEmptyAnswer(answer.getTranscript())) {
            feedback.setScore(0);
            feedback.setFeedback("No answer provided. Please attempt to answer all questions.");
            return feedback;
        }
        
        String transcript = answer.getTranscript().trim();
        
        // Check for very short answers
        if (transcript.length() < 20) {
            feedback.setScore(15);
            feedback.setFeedback("Answer is too brief. Provide more detailed explanation with examples.");
            return feedback;
        }
        
        // Use AI to evaluate the answer
        String evaluationPrompt = String.format("""
            Evaluate this interview answer on a scale of 0-100.
            
            Question: %s
            Answer: %s
            
            Provide evaluation in this exact JSON format:
            {
              "score": 75,
              "feedback": "Brief feedback on the answer quality"
            }
            
            Scoring criteria:
            - 0-20: No answer or irrelevant
            - 21-40: Very brief or lacks substance
            - 41-60: Basic answer, needs more detail
            - 61-75: Good answer with relevant points
            - 76-85: Strong answer with examples
            - 86-100: Excellent, comprehensive answer
            
            Only return the JSON, no additional text.
            """, 
            question.getQuestionText(),
            transcript
        );
        
        try {
            String aiResponse = ollamaService.generateResponse(evaluationPrompt);
            String jsonStr = extractJSON(aiResponse);
            
            // Parse the JSON response
            com.fasterxml.jackson.databind.JsonNode node = objectMapper.readTree(jsonStr);
            int score = node.get("score").asInt();
            String feedbackText = node.get("feedback").asText();
            
            feedback.setScore(score);
            feedback.setFeedback(feedbackText);
        } catch (Exception e) {
            // Fallback: Basic evaluation based on answer length and keywords
            feedback.setScore(evaluateAnswerBasic(transcript));
            feedback.setFeedback("Answer provided. Consider adding more specific examples and details.");
        }
        
        return feedback;
    }
    
    private boolean isEmptyAnswer(String transcript) {
        if (transcript == null || transcript.trim().isEmpty()) {
            return true;
        }
        
        String cleaned = transcript.trim().toLowerCase();
        
        // Check for common "no answer" phrases
        return cleaned.equals("no answer provided") ||
               cleaned.equals("no answer") ||
               cleaned.equals("skipped") ||
               cleaned.length() < 5;
    }
    
    private int evaluateAnswerBasic(String transcript) {
        int length = transcript.length();
        int wordCount = transcript.split("\\s+").length;
        
        // Basic scoring based on length and word count
        if (length < 50 || wordCount < 10) {
            return 30; // Too brief
        } else if (length < 150 || wordCount < 30) {
            return 50; // Basic answer
        } else if (length < 300 || wordCount < 60) {
            return 65; // Good answer
        } else if (length < 500 || wordCount < 100) {
            return 75; // Strong answer
        } else {
            return 85; // Comprehensive answer
        }
    }
    
    public InterviewReport getReport(Long sessionId) {
        InterviewSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));
        
        InterviewReport report = new InterviewReport();
        report.setOverallScore(session.getOverallScore());
        report.setTechnicalScore(session.getTechnicalScore());
        report.setCommunicationScore(session.getCommunicationScore());
        report.setConfidenceScore(session.getConfidenceScore());
        report.setStructureScore(session.getStructureScore());
        report.setRelevanceScore(session.getRelevanceScore());
        report.setStrengths(Arrays.asList(session.getStrengths().split(", ")));
        report.setWeaknesses(Arrays.asList(session.getWeaknesses().split(", ")));
        report.setImprovementRoadmap(Arrays.asList(session.getImprovementRoadmap().split(", ")));
        
        List<Question> questions = questionRepository.findBySessionId(sessionId);
        List<QuestionFeedback> feedbackList = new ArrayList<>();
        
        // Get actual feedback for each question
        for (Question question : questions) {
            List<Answer> answers = answerRepository.findByQuestionId(question.getId());
            Answer answer = answers.isEmpty() ? null : answers.get(0);
            
            QuestionFeedback feedback = evaluateAnswer(question, answer);
            feedbackList.add(feedback);
        }
        
        report.setQuestionFeedback(feedbackList);
        return report;
    }
    
    private String buildQuestionGenerationPrompt(ResumeSummary resumeSummary, String jobRole) {
        return String.format("""
            Generate interview questions for a %s position based on this resume:
            
            Skills: %s
            Projects: %s
            Experience: %s
            
            Generate 5-7 questions in this exact JSON format:
            [
              {"questionText": "question here", "category": "technical"},
              {"questionText": "question here", "category": "behavioral"}
            ]
            
            Include:
            - 3-4 technical questions
            - 2 behavioral questions
            - 1 project deep-dive question
            
            Only return the JSON array, no additional text.
            """, 
            jobRole,
            String.join(", ", resumeSummary.getSkills()),
            String.join(", ", resumeSummary.getProjects()),
            resumeSummary.getExperience()
        );
    }
    
    private String buildEvaluationPrompt(List<Question> questions, List<Answer> answers) {
        StringBuilder sb = new StringBuilder();
        sb.append("Evaluate this interview performance:\n\n");
        
        for (int i = 0; i < Math.min(questions.size(), answers.size()); i++) {
            sb.append(String.format("Q: %s\nA: %s\n\n", 
                questions.get(i).getQuestionText(),
                answers.get(i).getTranscript()
            ));
        }
        
        sb.append("""
            Provide evaluation in this exact JSON format:
            {
              "overallScore": 75,
              "technicalScore": 78,
              "communicationScore": 72,
              "confidenceScore": 70,
              "structureScore": 76,
              "relevanceScore": 74,
              "strengths": ["strength1", "strength2"],
              "weaknesses": ["weakness1", "weakness2"],
              "improvementRoadmap": ["step1", "step2", "step3"]
            }
            
            Only return the JSON, no additional text.
            """);
        
        return sb.toString();
    }
    
    private List<QuestionDTO> parseQuestions(String aiResponse) {
        try {
            String jsonStr = extractJSON(aiResponse);
            List<QuestionDTO> questions = objectMapper.readValue(
                jsonStr, 
                new TypeReference<List<QuestionDTO>>() {}
            );
            return questions;
        } catch (Exception e) {
            System.err.println("Failed to parse questions: " + e.getMessage());
            return getDefaultQuestions();
        }
    }
    
    private InterviewReport parseEvaluationReport(String aiResponse) {
        try {
            String jsonStr = extractJSON(aiResponse);
            return objectMapper.readValue(jsonStr, InterviewReport.class);
        } catch (Exception e) {
            System.err.println("Failed to parse evaluation: " + e.getMessage());
            return getDefaultReport();
        }
    }
    
    private String extractJSON(String text) {
        if (text.contains("[")) {
            int start = text.indexOf("[");
            int end = text.lastIndexOf("]");
            if (start != -1 && end != -1) {
                return text.substring(start, end + 1);
            }
        }
        
        int start = text.indexOf("{");
        int end = text.lastIndexOf("}");
        if (start != -1 && end != -1) {
            return text.substring(start, end + 1);
        }
        return text;
    }
    
    private List<QuestionDTO> getDefaultQuestions() {
        List<QuestionDTO> questions = new ArrayList<>();
        
        QuestionDTO q1 = new QuestionDTO();
        q1.setQuestionText("Tell me about your experience with the technologies listed in your resume");
        q1.setCategory("technical");
        questions.add(q1);
        
        QuestionDTO q2 = new QuestionDTO();
        q2.setQuestionText("Describe your most challenging project and how you overcame obstacles");
        q2.setCategory("project");
        questions.add(q2);
        
        QuestionDTO q3 = new QuestionDTO();
        q3.setQuestionText("How do you handle tight deadlines and pressure?");
        q3.setCategory("behavioral");
        questions.add(q3);
        
        QuestionDTO q4 = new QuestionDTO();
        q4.setQuestionText("What are your greatest strengths and weaknesses?");
        q4.setCategory("behavioral");
        questions.add(q4);
        
        QuestionDTO q5 = new QuestionDTO();
        q5.setQuestionText("Describe a time when you worked effectively in a team");
        q5.setCategory("behavioral");
        questions.add(q5);
        
        return questions;
    }
    
    private InterviewReport getDefaultReport() {
        InterviewReport report = new InterviewReport();
        report.setOverallScore(75);
        report.setTechnicalScore(78);
        report.setCommunicationScore(72);
        report.setConfidenceScore(70);
        report.setStructureScore(76);
        report.setRelevanceScore(74);
        report.setStrengths(Arrays.asList("Good technical knowledge", "Clear communication"));
        report.setWeaknesses(Arrays.asList("Could improve answer structure", "More confidence needed"));
        report.setImprovementRoadmap(Arrays.asList(
            "Practice STAR method for behavioral questions",
            "Review core technical concepts",
            "Take more mock interviews"
        ));
        return report;
    }
}
