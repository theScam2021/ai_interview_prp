package com.interviewai.dto;

import lombok.Data;

@Data
public class SubmitAnswerRequest {
    private Long sessionId;
    private Long questionId;
    private String transcript;
}
