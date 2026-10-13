package com.campusfix.dto;

import com.campusfix.model.Priority;

public class AICategorizationResponse {

    private Long categoryId;
    private String categoryName;
    private Priority suggestedPriority;
    private String department;
    private Integer slaHours;
    private double confidenceScore;
    private String explanation;
    private String matchedKeywords;

    public AICategorizationResponse() {}

    public AICategorizationResponse(Long categoryId, String categoryName, Priority suggestedPriority, String department, Integer slaHours, double confidenceScore, String explanation, String matchedKeywords) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.suggestedPriority = suggestedPriority;
        this.department = department;
        this.slaHours = slaHours;
        this.confidenceScore = confidenceScore;
        this.explanation = explanation;
        this.matchedKeywords = matchedKeywords;
    }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public Priority getSuggestedPriority() { return suggestedPriority; }
    public void setSuggestedPriority(Priority suggestedPriority) { this.suggestedPriority = suggestedPriority; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public Integer getSlaHours() { return slaHours; }
    public void setSlaHours(Integer slaHours) { this.slaHours = slaHours; }

    public double getConfidenceScore() { return confidenceScore; }
    public void setConfidenceScore(double confidenceScore) { this.confidenceScore = confidenceScore; }

    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }

    public String getMatchedKeywords() { return matchedKeywords; }
    public void setMatchedKeywords(String matchedKeywords) { this.matchedKeywords = matchedKeywords; }
}
