package com.interviewai.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "interview_sessions")
@Data
public class InterviewSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id")
    private Long userId;
    
    @Column(name = "job_role")
    private String jobRole;
    
    @Column(columnDefinition = "TEXT")
    private String resumeSummary;
    
    @Column(name = "overall_score")
    private Integer overallScore;
    
    @Column(name = "technical_score")
    private Integer technicalScore;
    
    @Column(name = "communication_score")
    private Integer communicationScore;
    
    @Column(name = "confidence_score")
    private Integer confidenceScore;
    
    @Column(name = "structure_score")
    private Integer structureScore;
    
    @Column(name = "relevance_score")
    private Integer relevanceScore;
    
    @Column(columnDefinition = "TEXT")
    private String strengths;
    
    @Column(columnDefinition = "TEXT")
    private String weaknesses;
    
    @Column(columnDefinition = "TEXT")
    private String improvementRoadmap;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
