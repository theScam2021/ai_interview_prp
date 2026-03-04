package com.interviewai.dto;

import lombok.Data;
import java.util.List;

@Data
public class InterviewReport {
    private Integer overallScore;
    private Integer technicalScore;
    private Integer communicationScore;
    private Integer confidenceScore;
    private Integer structureScore;
    private Integer relevanceScore;
    private List<String> strengths;
    private List<String> weaknesses;
    private List<QuestionFeedback> questionFeedback;
    private List<String> improvementRoadmap;
}
