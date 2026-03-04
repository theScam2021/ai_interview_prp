package com.interviewai.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "answers")
@Data
public class Answer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "question_id")
    private Long questionId;
    
    @Column(columnDefinition = "TEXT")
    private String transcript;
    
    private Integer score;
    
    @Column(columnDefinition = "TEXT")
    private String feedback;
}
