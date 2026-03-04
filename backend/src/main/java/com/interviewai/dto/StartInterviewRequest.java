package com.interviewai.dto;

import lombok.Data;

@Data
public class StartInterviewRequest {
    private Long userId;
    private String jobRole;
    private ResumeSummary resumeSummary;
}
