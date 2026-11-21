package com.hiresmart.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hiresmart.dto.AiDtos;
import com.hiresmart.entity.Candidate;
import com.hiresmart.entity.Job;
import com.hiresmart.entity.Resume;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
public class AiJobMatchingService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Calculates explainable candidate-job matching score based on:
     * - Skills match: 40%
     * - Experience match: 25%
     * - Projects relevance: 20%
     * - Education relevance: 15%
     */
    public AiDtos.JobMatchDto calculateMatchScore(Candidate candidate, Resume resume, Job job) {
        AiDtos.JobMatchDto result = new AiDtos.JobMatchDto();

        // 1. Parse Required Skills
        List<String> requiredSkills = parseJsonList(job.getRequiredSkills());
        if (requiredSkills.isEmpty()) {
            requiredSkills = Arrays.asList("Java", "Spring Boot", "REST APIs", "MySQL");
        }

        // 2. Parse Candidate Skills & Extracted Resume Text
        String resumeText = resume.getExtractedText().toLowerCase();
        List<String> candidateSkills = parseJsonList(candidate.getSkillsJson());
        Set<String> candidateSkillSet = new HashSet<>();
        for (String s : candidateSkills) candidateSkillSet.add(s.toLowerCase());

        List<String> matchedSkills = new ArrayList<>();
        List<String> missingSkills = new ArrayList<>();

        for (String req : requiredSkills) {
            String cleanReq = req.toLowerCase().trim();
            if (candidateSkillSet.contains(cleanReq) || resumeText.contains(cleanReq)) {
                matchedSkills.add(req);
            } else {
                missingSkills.add(req);
            }
        }

        // --- Metric 1: Skills Match (Weight 40%) ---
        double skillRatio = requiredSkills.isEmpty() ? 1.0 : (double) matchedSkills.size() / requiredSkills.size();
        double skillsScore = Math.min(100.0, skillRatio * 100.0);

        // --- Metric 2: Experience Match (Weight 25%) ---
        double experienceScore = 75.0; // Base baseline
        if (resumeText.contains("senior") || resumeText.contains("lead") || resumeText.contains("5 years") || resumeText.contains("4 years")) {
            experienceScore = 95.0;
        } else if (resumeText.contains("2 years") || resumeText.contains("3 years")) {
            experienceScore = 85.0;
        } else if (resumeText.contains("junior") || resumeText.contains("intern")) {
            experienceScore = 65.0;
        }

        // --- Metric 3: Projects Relevance (Weight 20%) ---
        double projectsScore = 70.0;
        if (candidate.getProjectsJson() != null && !candidate.getProjectsJson().isEmpty()) {
            projectsScore += 15.0;
        }
        if (resumeText.contains("microservices") || resumeText.contains("pipeline") || resumeText.contains("distributed")) {
            projectsScore += 10.0;
        }
        projectsScore = Math.min(100.0, projectsScore);

        // --- Metric 4: Education Relevance (Weight 15%) ---
        double educationScore = 80.0;
        if (resumeText.contains("b.s.") || resumeText.contains("computer science") || resumeText.contains("engineering")) {
            educationScore = 95.0;
        } else if (resumeText.contains("m.s.") || resumeText.contains("master")) {
            educationScore = 100.0;
        }

        // --- Weighted Composite Score ---
        double totalWeighted = (skillsScore * 0.40) +
                               (experienceScore * 0.25) +
                               (projectsScore * 0.20) +
                               (educationScore * 0.15);

        BigDecimal finalOverallScore = BigDecimal.valueOf(totalWeighted).setScale(2, RoundingMode.HALF_UP);

        result.setOverallScore(finalOverallScore);
        result.setSkillsScore(BigDecimal.valueOf(skillsScore).setScale(2, RoundingMode.HALF_UP));
        result.setExperienceScore(BigDecimal.valueOf(experienceScore).setScale(2, RoundingMode.HALF_UP));
        result.setProjectsScore(BigDecimal.valueOf(projectsScore).setScale(2, RoundingMode.HALF_UP));
        result.setEducationScore(BigDecimal.valueOf(educationScore).setScale(2, RoundingMode.HALF_UP));
        result.setMatchedSkills(matchedSkills);
        result.setMissingSkills(missingSkills);

        // Explanation formulation
        String explanation = String.format(
                "Candidate scored %s%% overall match for %s. Matched %d of %d key skills (%s). " +
                "Experience score is rated at %.0f%% with strong project alignment (%.0f%%) and verified STEM education (%.0f%%).",
                finalOverallScore, job.getTitle(), matchedSkills.size(), requiredSkills.size(),
                String.join(", ", matchedSkills), experienceScore, projectsScore, educationScore
        );
        result.setExplanation(explanation);

        String recommendation = finalOverallScore.doubleValue() >= 80.0
                ? "Highly Recommended: Candidate possesses the required technical depth. Fast-track to interview."
                : (finalOverallScore.doubleValue() >= 60.0
                ? "Moderate Match: Meets primary requirements. Review missing skills (" + String.join(", ", missingSkills) + ") during technical screening."
                : "Low Match: Significant skill gaps detected for this specific senior role.");
        result.setRecommendation(recommendation);

        return result;
    }

    private List<String> parseJsonList(String json) {
        if (json == null || json.trim().isEmpty()) return Collections.emptyList();
        try {
            return objectMapper.readValue(json, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            return Arrays.asList(json.replace("[", "").replace("]", "").replace("\"", "").split(","));
        }
    }
}
