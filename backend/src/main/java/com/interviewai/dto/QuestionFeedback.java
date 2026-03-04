package com.interviewai.dto;

import lombok.Data;

@Data
public class QuestionFeedback {
    private String question;
    private Integer score;
    private String feedback;
}
