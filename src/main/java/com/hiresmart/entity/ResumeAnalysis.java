package com.hiresmart.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "resume_analysis")
public class ResumeAnalysis {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "resume_id", nullable = false, unique = true)
    private Resume resume;

    @Column(columnDefinition = "TEXT")
    private String summary;

    @Column(name = "technical_skills", columnDefinition = "TEXT")
    private String technicalSkills;

    @Column(name = "soft_skills", columnDefinition = "TEXT")
    private String softSkills;

    @Column(name = "education_summary", columnDefinition = "TEXT")
    private String educationSummary;

    @Column(name = "experience_summary", columnDefinition = "TEXT")
    private String experienceSummary;

    @Column(columnDefinition = "TEXT")
    private String strengths;

    @Column(name = "gaps_missing_info", columnDefinition = "TEXT")
    private String gapsMissingInfo;

    @Column(name = "suggested_improvements", columnDefinition = "TEXT")
    private String suggestedImprovements;

    @Column(name = "potential_job_roles", columnDefinition = "TEXT")
    private String potentialJobRoles;

    @Column(name = "raw_ai_response", columnDefinition = "TEXT")
    private String rawAiResponse;

    @Column(name = "analyzed_at", updatable = false)
    private LocalDateTime analyzedAt = LocalDateTime.now();

    public ResumeAnalysis() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Resume getResume() { return resume; }
    public void setResume(Resume resume) { this.resume = resume; }
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    public String getTechnicalSkills() { return technicalSkills; }
    public void setTechnicalSkills(String technicalSkills) { this.technicalSkills = technicalSkills; }
    public String getSoftSkills() { return softSkills; }
    public void setSoftSkills(String softSkills) { this.softSkills = softSkills; }
    public String getEducationSummary() { return educationSummary; }
    public void setEducationSummary(String educationSummary) { this.educationSummary = educationSummary; }
    public String getExperienceSummary() { return experienceSummary; }
    public void setExperienceSummary(String experienceSummary) { this.experienceSummary = experienceSummary; }
    public String getStrengths() { return strengths; }
    public void setStrengths(String strengths) { this.strengths = strengths; }
    public String getGapsMissingInfo() { return gapsMissingInfo; }
    public void setGapsMissingInfo(String gapsMissingInfo) { this.gapsMissingInfo = gapsMissingInfo; }
    public String getSuggestedImprovements() { return suggestedImprovements; }
    public void setSuggestedImprovements(String suggestedImprovements) { this.suggestedImprovements = suggestedImprovements; }
    public String getPotentialJobRoles() { return potentialJobRoles; }
    public void setPotentialJobRoles(String potentialJobRoles) { this.potentialJobRoles = potentialJobRoles; }
    public String getRawAiResponse() { return rawAiResponse; }
    public void setRawAiResponse(String rawAiResponse) { this.rawAiResponse = rawAiResponse; }
    public LocalDateTime getAnalyzedAt() { return analyzedAt; }
    public void setAnalyzedAt(LocalDateTime analyzedAt) { this.analyzedAt = analyzedAt; }
}
