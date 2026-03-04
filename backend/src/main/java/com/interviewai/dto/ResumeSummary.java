package com.interviewai.dto;

import lombok.Data;
import java.util.List;

@Data
public class ResumeSummary {
    private List<String> skills;
    private List<String> projects;
    private String experience;
    private List<String> strengths;
    private List<String> missingSkills;
}
