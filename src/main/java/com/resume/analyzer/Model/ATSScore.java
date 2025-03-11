package com.resume.analyzer.Model;


import lombok.Builder;
import lombok.Data;


import java.util.List;
import java.util.Map;

@Data
@Builder
public class ATSScore {
    private int score;
    private String recommendation;
    private List<String> strengths;
    private List<String> weaknesses;
    private Map<String, Integer> categoryScores;
}