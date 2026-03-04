package com.interviewai.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interviewai.dto.ResumeSummary;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class ResumeService {
    
    private final OllamaService ollamaService;
    private final ObjectMapper objectMapper;
    
    public ResumeService(OllamaService ollamaService) {
        this.ollamaService = ollamaService;
        this.objectMapper = new ObjectMapper();
    }
    
    public ResumeSummary analyzeResume(MultipartFile file) throws IOException {
        String resumeText = extractTextFromPDF(file);
        String prompt = buildResumeAnalysisPrompt(resumeText);
        String aiResponse = ollamaService.generateResponse(prompt);
        
        return parseResumeSummary(aiResponse);
    }
    
    private String extractTextFromPDF(MultipartFile file) throws IOException {
        try (PDDocument document = PDDocument.load(file.getInputStream())) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }
    
    private String buildResumeAnalysisPrompt(String resumeText) {
        return String.format("""
            Analyze the following resume and extract structured information in JSON format.
            
            Resume:
            %s
            
            Provide the analysis in this exact JSON format:
            {
              "skills": ["skill1", "skill2", ...],
              "projects": ["project1", "project2", ...],
              "experience": "brief summary",
              "strengths": ["strength1", "strength2", ...],
              "missingSkills": ["skill1", "skill2", ...]
            }
            
            Only return the JSON, no additional text.
            """, resumeText);
    }
    
    private ResumeSummary parseResumeSummary(String aiResponse) {
        try {
            String jsonStr = extractJSON(aiResponse);
            return objectMapper.readValue(jsonStr, ResumeSummary.class);
        } catch (Exception e) {
            System.err.println("Failed to parse resume summary: " + e.getMessage());
            return createDefaultResumeSummary();
        }
    }
    
    private String extractJSON(String text) {
        int start = text.indexOf("{");
        int end = text.lastIndexOf("}");
        if (start != -1 && end != -1) {
            return text.substring(start, end + 1);
        }
        return text;
    }
    
    private ResumeSummary createDefaultResumeSummary() {
        ResumeSummary summary = new ResumeSummary();
        summary.setSkills(java.util.List.of("Java", "Spring Boot", "React"));
        summary.setProjects(java.util.List.of("Web Application"));
        summary.setExperience("Entry level");
        summary.setStrengths(java.util.List.of("Quick learner"));
        summary.setMissingSkills(java.util.List.of("Advanced topics"));
        return summary;
    }
}
