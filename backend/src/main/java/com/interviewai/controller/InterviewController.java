package com.interviewai.controller;

import com.interviewai.dto.*;
import com.interviewai.service.InterviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/interview")
@CrossOrigin(origins = "http://localhost:3000")
public class InterviewController {
    
    private final InterviewService interviewService;
    
    public InterviewController(InterviewService interviewService) {
        this.interviewService = interviewService;
    }
    
    @PostMapping("/generate-questions")
    public ResponseEntity<QuestionGenerationResponse> generateQuestions(
            @RequestBody QuestionGenerationRequest request) {
        try {
            QuestionGenerationResponse response = interviewService.generateQuestions(
                request.getResumeSummary(),
                request.getJobRole()
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @PostMapping("/start")
    public ResponseEntity<StartInterviewResponse> startInterview(
            @RequestBody StartInterviewRequest request) {
        try {
            StartInterviewResponse response = interviewService.startInterview(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @PostMapping("/submit-answer")
    public ResponseEntity<Void> submitAnswer(@RequestBody SubmitAnswerRequest request) {
        try {
            interviewService.submitAnswer(request);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @PostMapping("/complete/{sessionId}")
    public ResponseEntity<InterviewReport> completeInterview(@PathVariable Long sessionId) {
        try {
            InterviewReport report = interviewService.completeInterview(sessionId);
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/report/{sessionId}")
    public ResponseEntity<InterviewReport> getReport(@PathVariable Long sessionId) {
        try {
            InterviewReport report = interviewService.getReport(sessionId);
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}
