package com.interviewai.dto;

import lombok.Data;
import java.util.List;

@Data
public class QuestionGenerationResponse {
    private List<QuestionDTO> questions;
}
