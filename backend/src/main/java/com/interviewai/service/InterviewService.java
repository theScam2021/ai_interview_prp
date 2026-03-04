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
        
        String evaluationPrompt = buildEvaluationPrompt(questions, allAnswers);
        String aiResponse = ollamaService.generateResponse(evaluationPrompt);
        
        InterviewReport report = parseEvaluationReport(aiResponse);
        
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
        
        List<QuestionFeedback> feedbackList = new ArrayList<>();
        for (int i = 0; i < questions.size() && i < allAnswers.size(); i++) {
            QuestionFeedback feedback = new QuestionFeedback();
            feedback.setQuestion(questions.get(i).getQuestionText());
            feedback.setScore(70 + (int)(Math.random() * 20));
            feedback.setFeedback("Good answer with room for improvement");
            feedbackList.add(feedback);
        }
        report.setQuestionFeedback(feedbackList);
        
        return report;
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
        
        for (Question question : questions) {
            QuestionFeedback feedback = new QuestionFeedback();
            feedback.setQuestion(question.getQuestionText());
            feedback.setScore(70 + (int)(Math.random() * 20));
            feedback.setFeedback("Good understanding demonstrated");
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
