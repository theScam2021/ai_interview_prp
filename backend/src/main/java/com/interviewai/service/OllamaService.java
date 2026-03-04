package com.interviewai.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
public class OllamaService {
    
    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    
    @Value("${ollama.model}")
    private String model;
    
    public OllamaService(@Value("${ollama.base.url}") String baseUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .build();
        this.objectMapper = new ObjectMapper();
    }
    
    public String generateResponse(String prompt) {
        try {
            Map<String, Object> request = Map.of(
                "model", model,
                "prompt", prompt,
                "stream", false
            );
            
            String response = webClient.post()
                    .uri("/api/generate")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            
            JsonNode jsonNode = objectMapper.readTree(response);
            return jsonNode.get("response").asText();
        } catch (Exception e) {
            System.err.println("Ollama API error: " + e.getMessage());
            return getFallbackResponse(prompt);
        }
    }
    
    private String getFallbackResponse(String prompt) {
        if (prompt.contains("resume")) {
            return """
                {
                  "skills": ["Java", "Spring Boot", "React", "SQL"],
                  "projects": ["E-commerce Platform", "Task Management System"],
                  "experience": "2 years",
                  "strengths": ["Problem solving", "Team collaboration"],
                  "missingSkills": ["Microservices", "Docker"]
                }
                """;
        } else if (prompt.contains("questions")) {
            return """
                [
                  {"questionText": "Tell me about your experience with Java and Spring Boot", "category": "technical"},
                  {"questionText": "Explain your most challenging project", "category": "project"},
                  {"questionText": "How do you handle tight deadlines?", "category": "behavioral"},
                  {"questionText": "What are your strengths and weaknesses?", "category": "behavioral"},
                  {"questionText": "Describe a time you worked in a team", "category": "behavioral"}
                ]
                """;
        } else {
            return """
                {
                  "overallScore": 75,
                  "technicalScore": 78,
                  "communicationScore": 72,
                  "confidenceScore": 70,
                  "structureScore": 76,
                  "relevanceScore": 74,
                  "strengths": ["Good technical knowledge", "Clear communication"],
                  "weaknesses": ["Could improve answer structure", "More confidence needed"],
                  "improvementRoadmap": ["Practice STAR method", "Review core concepts", "Mock interviews"]
                }
                """;
        }
    }
}
