package com.interviewai.dto;

import lombok.Data;

@Data
public class QuestionGenerationRequest {
    private ResumeSummary resumeSummary;
    private String jobRole;
}
