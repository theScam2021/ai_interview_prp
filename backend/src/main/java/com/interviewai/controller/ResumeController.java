package com.interviewai.controller;

import com.interviewai.dto.ResumeSummary;
import com.interviewai.service.ResumeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/resume")
@CrossOrigin(origins = "http://localhost:3000")
public class ResumeController {
    
    private final ResumeService resumeService;
    
    public ResumeController(ResumeService resumeService) {
        this.resumeService = resumeService;
    }
    
    @PostMapping("/upload")
    public ResponseEntity<ResumeSummary> uploadResume(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            
            ResumeSummary summary = resumeService.analyzeResume(file);
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}
