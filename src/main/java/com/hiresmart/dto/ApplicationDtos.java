package com.hiresmart.dto;

import java.math.BigDecimal;
import java.util.List;

public class ApplicationDtos {

    public static class ApplyRequest {
        private Long jobId;
        private Long resumeId;

        public Long getJobId() { return jobId; }
        public void setJobId(Long jobId) { this.jobId = jobId; }
        public Long getResumeId() { return resumeId; }
        public void setResumeId(Long resumeId) { this.resumeId = resumeId; }
    }

    public static class StatusUpdateRequest {
        private String status; // SHORTLISTED, REJECTED, INTERVIEW_SCHEDULED, SELECTED
        private String rejectionReason;
        private String notes;

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getRejectionReason() { return rejectionReason; }
        public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }
        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }
    }

    public static class ApplicationResponse {
        private Long id;
        private Long jobId;
        private String jobTitle;
        private String companyName;
        private Long candidateId;
        private String candidateName;
        private String candidateEmail;
        private Long resumeId;
        private String resumeStorageUrl;
        private String status;
        private BigDecimal matchScore;
        private BigDecimal skillsMatchScore;
        private BigDecimal experienceMatchScore;
        private BigDecimal educationMatchScore;
        private String matchExplanation;
        private List<String> matchedSkills;
        private List<String> missingSkills;
        private String appliedAt;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public Long getJobId() { return jobId; }
        public void setJobId(Long jobId) { this.jobId = jobId; }
        public String getJobTitle() { return jobTitle; }
        public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }
        public String getCompanyName() { return companyName; }
        public void setCompanyName(String companyName) { this.companyName = companyName; }
        public Long getCandidateId() { return candidateId; }
        public void setCandidateId(Long candidateId) { this.candidateId = candidateId; }
        public String getCandidateName() { return candidateName; }
        public void setCandidateName(String candidateName) { this.candidateName = candidateName; }
        public String getCandidateEmail() { return candidateEmail; }
        public void setCandidateEmail(String candidateEmail) { this.candidateEmail = candidateEmail; }
        public Long getResumeId() { return resumeId; }
        public void setResumeId(Long resumeId) { this.resumeId = resumeId; }
        public String getResumeStorageUrl() { return resumeStorageUrl; }
        public void setResumeStorageUrl(String resumeStorageUrl) { this.resumeStorageUrl = resumeStorageUrl; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public BigDecimal getMatchScore() { return matchScore; }
        public void setMatchScore(BigDecimal matchScore) { this.matchScore = matchScore; }
        public BigDecimal getSkillsMatchScore() { return skillsMatchScore; }
        public void setSkillsMatchScore(BigDecimal skillsMatchScore) { this.skillsMatchScore = skillsMatchScore; }
        public BigDecimal getExperienceMatchScore() { return experienceMatchScore; }
        public void setExperienceMatchScore(BigDecimal experienceMatchScore) { this.experienceMatchScore = experienceMatchScore; }
        public BigDecimal getEducationMatchScore() { return educationMatchScore; }
        public void setEducationMatchScore(BigDecimal educationMatchScore) { this.educationMatchScore = educationMatchScore; }
        public String getMatchExplanation() { return matchExplanation; }
        public void setMatchExplanation(String matchExplanation) { this.matchExplanation = matchExplanation; }
        public List<String> getMatchedSkills() { return matchedSkills; }
        public void setMatchedSkills(List<String> matchedSkills) { this.matchedSkills = matchedSkills; }
        public List<String> getMissingSkills() { return missingSkills; }
        public void setMissingSkills(List<String> missingSkills) { this.missingSkills = missingSkills; }
        public String getAppliedAt() { return appliedAt; }
        public void setAppliedAt(String appliedAt) { this.appliedAt = appliedAt; }
    }
}
