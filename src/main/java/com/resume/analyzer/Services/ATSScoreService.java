package com.resume.analyzer.Services;


import com.resume.analyzer.Model.ATSScore;
import com.resume.analyzer.Model.Settings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ATSScoreService {

    @Autowired
    private RepoService repoService;

    private static final String SYSTEM_PROMPT = """
        You are an expert ATS (Applicant Tracking System) analyzer. Your task is to:
        1. Evaluate the provided resume against standard ATS criteria or a specific job description if provided.
        2. Score the resume on a scale of 1-100 based on ATS compatibility.
        3. Provide specific feedback in the following categories:
           - Format and structure (20 points)
           - Keywords and relevance (30 points)
           - Quantifiable achievements (20 points)
           - Skills and qualifications (30 points)
        4. List specific strengths and weaknesses.
        5. Give actionable recommendations to improve the ATS score.
        
        Return your analysis in a valid JSON format with the following structure:
        {
          "score": [overall score 1-100],
          "recommendation": "[brief overall recommendation]",
          "strengths": ["strength1", "strength2", ...],
          "weaknesses": ["weakness1", "weakness2", ...],
          "categoryScores": {
            "format": [score 1-20],
            "keywords": [score 1-30],
            "achievements": [score 1-20],
            "skills": [score 1-30]
          }
        }
        
        Make your evaluation comprehensive but concise.
        """;
    
    private static final String USER_PROMPT_WITHOUT_JD = """
        Please analyze this resume for ATS compatibility:
        
        {resumeText}
        """;
    
    private static final String USER_PROMPT_WITH_JD = """
        Please analyze this resume for ATS compatibility with the following job description:
        
        JOB DESCRIPTION:
        {jobDescription}
        
        RESUME:
        {resumeText}
        """;
    
    /**
     * Calculates ATS score for a resume
     *
     * @param resumeText The text extracted from the resume
     * @return ATSScore object containing the score and feedback
     */
    public ATSScore calculateScore(String resumeText) {
        return calculateScore(resumeText, null);
    }
    
    /**
     * Calculates ATS score for a resume against a specific job description
     *
     * @param resumeText     The text extracted from the resume
     * @param jobDescription Optional job description to match against
     * @return ATSScore object containing the score and feedback
     */
    public ATSScore calculateScore(String resumeText, String jobDescription) {
        try {
            Settings settings=repoService.getSettings();
            if(AIChatClient.getChatClient()==null&&settings!=null){
               AIChatClient.getInstance(settings);
            }
            ChatClient chatClient = AIChatClient.getChatClient();
            if (chatClient == null) {
                return createErrorScore("AI service is not configured. Please set up your API key in settings.");
            }
            String userPromptTemplate;
            if (jobDescription != null && !jobDescription.isEmpty()) {
                userPromptTemplate = USER_PROMPT_WITH_JD;
                userPromptTemplate=userPromptTemplate.replace("{jobDescription}",jobDescription);
            } else {
                userPromptTemplate = USER_PROMPT_WITHOUT_JD;
            }
            userPromptTemplate=userPromptTemplate.replace("{resumeText}",resumeText);
            log.info("Sending resume to AI for ATS analysis");

            String finalUserPromptTemplate = userPromptTemplate;
            String content = chatClient.prompt()
                    .user(u->u.text(finalUserPromptTemplate))
                    .system(SYSTEM_PROMPT)
                    .stream()
                    .content()
                    .collectList()
                    .map(list->String.join("",list))
                    .block();

            log.debug("AI response: {}", content);
            
            return parseResponse(content);
        } catch (Exception e) {
            log.error("Error calculating ATS score", e);
            return createErrorScore("Failed to analyze resume. Please try again later.");
        }
    }
    
    private ATSScore parseResponse(String jsonResponse) {
        try {
            // For simplicity, we're using string manipulation here
            // In a production app, use a proper JSON parser like Jackson
            
            // Sample implementation - in real code, use Jackson or Gson
            int score = extractIntValue(jsonResponse, "score");
            String recommendation = extractStringValue(jsonResponse, "recommendation");
            
            List<String> strengths = extractStringList(jsonResponse, "strengths");
            List<String> weaknesses = extractStringList(jsonResponse, "weaknesses");
            
            Map<String, Integer> categoryScores = new HashMap<>();
            categoryScores.put("format", extractNestedIntValue(jsonResponse, "categoryScores", "format"));
            categoryScores.put("keywords", extractNestedIntValue(jsonResponse, "categoryScores", "keywords"));
            categoryScores.put("achievements", extractNestedIntValue(jsonResponse, "categoryScores", "achievements"));
            categoryScores.put("skills", extractNestedIntValue(jsonResponse, "categoryScores", "skills"));
            
            return ATSScore.builder()
                    .score(score)
                    .recommendation(recommendation)
                    .strengths(strengths)
                    .weaknesses(weaknesses)
                    .categoryScores(categoryScores)
                    .build();
                    
        } catch (Exception e) {
            log.error("Error parsing AI response", e);
            return createErrorScore("Unable to analyze resume properly. Please try again.");
        }
    }
    
    // These methods are simplistic - in production code, use a proper JSON library
    private int extractIntValue(String json, String key) {
        // Simple implementation for demo purposes
        String pattern = "\"" + key + "\"\\s*:\\s*(\\d+)";
        java.util.regex.Pattern r = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = r.matcher(json);
        if (m.find()) {
            return Integer.parseInt(m.group(1));
        }
        return 0;
    }
    
    private String extractStringValue(String json, String key) {
        String pattern = "\"" + key + "\"\\s*:\\s*\"([^\"]*)\"";
        java.util.regex.Pattern r = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = r.matcher(json);
        if (m.find()) {
            return m.group(1);
        }
        return "";
    }
    
    private List<String> extractStringList(String json, String key) {
        List<String> results = new ArrayList<>();
        String listPattern = "\"" + key + "\"\\s*:\\s*\\[(.*?)\\]";
        java.util.regex.Pattern r = java.util.regex.Pattern.compile(listPattern, java.util.regex.Pattern.DOTALL);
        java.util.regex.Matcher m = r.matcher(json);
        
        if (m.find()) {
            String listContent = m.group(1);
            String itemPattern = "\"([^\"]*)\"";
            java.util.regex.Pattern itemR = java.util.regex.Pattern.compile(itemPattern);
            java.util.regex.Matcher itemM = itemR.matcher(listContent);
            
            while (itemM.find()) {
                results.add(itemM.group(1));
            }
        }
        return results;
    }
    
    private int extractNestedIntValue(String json, String parentKey, String childKey) {
        String nestedPattern = "\"" + parentKey + "\"\\s*:\\s*\\{[^}]*\"" + childKey + "\"\\s*:\\s*(\\d+)";
        java.util.regex.Pattern r = java.util.regex.Pattern.compile(nestedPattern);
        java.util.regex.Matcher m = r.matcher(json);
        if (m.find()) {
            return Integer.parseInt(m.group(1));
        }
        return 0;
    }
    
    public static ATSScore createErrorScore(String errorMessage) {
        Map<String, Integer> defaultScores = new HashMap<>();
        defaultScores.put("format", 0);
        defaultScores.put("keywords", 0);
        defaultScores.put("achievements", 0);
        defaultScores.put("skills", 0);
        
        return ATSScore.builder()
                .score(0)
                .recommendation(errorMessage)
                .strengths(Collections.emptyList())
                .weaknesses(Collections.singletonList("Analysis error occurred"))
                .categoryScores(defaultScores)
                .build();
    }
}