package com.hiresmart.dto;

import java.math.BigDecimal;
import java.util.List;

public class AiDtos {

    public static class ResumeAnalysisDto {
        private String candidateName;
        private String contactInfo;
        private String summary;
        private List<String> technicalSkills;
        private List<String> softSkills;
        private String educationSummary;
        private String experienceSummary;
        private List<String> strengths;
        private List<String> gapsMissingInfo;
        private List<String> suggestedImprovements;
        private List<String> potentialJobRoles;

        public String getCandidateName() { return candidateName; }
        public void setCandidateName(String candidateName) { this.candidateName = candidateName; }
        public String getContactInfo() { return contactInfo; }
        public void setContactInfo(String contactInfo) { this.contactInfo = contactInfo; }
        public String getSummary() { return summary; }
        public void setSummary(String summary) { this.summary = summary; }
        public List<String> getTechnicalSkills() { return technicalSkills; }
        public void setTechnicalSkills(List<String> technicalSkills) { this.technicalSkills = technicalSkills; }
        public List<String> getSoftSkills() { return softSkills; }
        public void setSoftSkills(List<String> softSkills) { this.softSkills = softSkills; }
        public String getEducationSummary() { return educationSummary; }
        public void setEducationSummary(String educationSummary) { this.educationSummary = educationSummary; }
        public String getExperienceSummary() { return experienceSummary; }
        public void setExperienceSummary(String experienceSummary) { this.experienceSummary = experienceSummary; }
        public List<String> getStrengths() { return strengths; }
        public void setStrengths(List<String> strengths) { this.strengths = strengths; }
        public List<String> getGapsMissingInfo() { return gapsMissingInfo; }
        public void setGapsMissingInfo(List<String> gapsMissingInfo) { this.gapsMissingInfo = gapsMissingInfo; }
        public List<String> getSuggestedImprovements() { return suggestedImprovements; }
        public void setSuggestedImprovements(List<String> suggestedImprovements) { this.suggestedImprovements = suggestedImprovements; }
        public List<String> getPotentialJobRoles() { return potentialJobRoles; }
        public void setPotentialJobRoles(List<String> potentialJobRoles) { this.potentialJobRoles = potentialJobRoles; }
    }

    public static class JobMatchDto {
        private BigDecimal overallScore;
        private BigDecimal skillsScore;
        private BigDecimal experienceScore;
        private BigDecimal projectsScore;
        private BigDecimal educationScore;
        private List<String> matchedSkills;
        private List<String> missingSkills;
        private String explanation;
        private String recommendation;

        public BigDecimal getOverallScore() { return overallScore; }
        public void setOverallScore(BigDecimal overallScore) { this.overallScore = overallScore; }
        public BigDecimal getSkillsScore() { return skillsScore; }
        public void setSkillsScore(BigDecimal skillsScore) { this.skillsScore = skillsScore; }
        public BigDecimal getExperienceScore() { return experienceScore; }
        public void setExperienceScore(BigDecimal experienceScore) { this.experienceScore = experienceScore; }
        public BigDecimal getProjectsScore() { return projectsScore; }
        public void setProjectsScore(BigDecimal projectsScore) { this.projectsScore = projectsScore; }
        public BigDecimal getEducationScore() { return educationScore; }
        public void setEducationScore(BigDecimal educationScore) { this.educationScore = educationScore; }
        public List<String> getMatchedSkills() { return matchedSkills; }
        public void setMatchedSkills(List<String> matchedSkills) { this.matchedSkills = matchedSkills; }
        public List<String> getMissingSkills() { return missingSkills; }
        public void setMissingSkills(List<String> missingSkills) { this.missingSkills = missingSkills; }
        public String getExplanation() { return explanation; }
        public void setExplanation(String explanation) { this.explanation = explanation; }
        public String getRecommendation() { return recommendation; }
        public void setRecommendation(String recommendation) { this.recommendation = recommendation; }
    }

    public static class QuestionItem {
        private String question;
        private String category;
        private String difficulty;
        private String sampleAnswer;

        public QuestionItem() {}
        public QuestionItem(String question, String category, String difficulty, String sampleAnswer) {
            this.question = question;
            this.category = category;
            this.difficulty = difficulty;
            this.sampleAnswer = sampleAnswer;
        }

        public String getQuestion() { return question; }
        public void setQuestion(String question) { this.question = question; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public String getDifficulty() { return difficulty; }
        public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
        public String getSampleAnswer() { return sampleAnswer; }
        public void setSampleAnswer(String sampleAnswer) { this.sampleAnswer = sampleAnswer; }
    }

    public static class InterviewEvaluationDto {
        private BigDecimal technicalScore;
        private BigDecimal clarityScore;
        private BigDecimal relevanceScore;
        private BigDecimal overallScore;
        private List<String> strengths;
        private List<String> areasForImprovement;
        private String summary;

        public BigDecimal getTechnicalScore() { return technicalScore; }
        public void setTechnicalScore(BigDecimal technicalScore) { this.technicalScore = technicalScore; }
        public BigDecimal getClarityScore() { return clarityScore; }
        public void setClarityScore(BigDecimal clarityScore) { this.clarityScore = clarityScore; }
        public BigDecimal getRelevanceScore() { return relevanceScore; }
        public void setRelevanceScore(BigDecimal relevanceScore) { this.relevanceScore = relevanceScore; }
        public BigDecimal getOverallScore() { return overallScore; }
        public void setOverallScore(BigDecimal overallScore) { this.overallScore = overallScore; }
        public List<String> getStrengths() { return strengths; }
        public void setStrengths(List<String> strengths) { this.strengths = strengths; }
        public List<String> getAreasForImprovement() { return areasForImprovement; }
        public void setAreasForImprovement(List<String> areasForImprovement) { this.areasForImprovement = areasForImprovement; }
        public String getSummary() { return summary; }
        public void setSummary(String summary) { this.summary = summary; }
    }
}
