package com.hiresmart.dto;

import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.util.List;

public class JobDtos {

    public static class JobCreateRequest {
        @NotBlank
        private String title;
        private String department;
        @NotBlank
        private String description;
        @NotBlank
        private String location;
        private String employmentType = "FULL_TIME";
        private String experienceLevel = "Mid-Senior";
        private BigDecimal minSalary;
        private BigDecimal maxSalary;
        private String currency = "INR";
        private Integer vacanciesCount = 1;
        private List<String> requiredSkills;

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getDepartment() { return department; }
        public void setDepartment(String department) { this.department = department; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }
        public String getEmploymentType() { return employmentType; }
        public void setEmploymentType(String employmentType) { this.employmentType = employmentType; }
        public String getExperienceLevel() { return experienceLevel; }
        public void setExperienceLevel(String experienceLevel) { this.experienceLevel = experienceLevel; }
        public BigDecimal getMinSalary() { return minSalary; }
        public void setMinSalary(BigDecimal minSalary) { this.minSalary = minSalary; }
        public BigDecimal getMaxSalary() { return maxSalary; }
        public void setMaxSalary(BigDecimal maxSalary) { this.maxSalary = maxSalary; }
        public String getCurrency() { return currency; }
        public void setCurrency(String currency) { this.currency = currency; }
        public Integer getVacanciesCount() { return vacanciesCount; }
        public void setVacanciesCount(Integer vacanciesCount) { this.vacanciesCount = vacanciesCount; }
        public List<String> getRequiredSkills() { return requiredSkills; }
        public void setRequiredSkills(List<String> requiredSkills) { this.requiredSkills = requiredSkills; }
    }

    public static class JobResponse {
        private Long id;
        private String title;
        private String department;
        private String description;
        private String location;
        private String employmentType;
        private String experienceLevel;
        private BigDecimal minSalary;
        private BigDecimal maxSalary;
        private String currency;
        private List<String> requiredSkills;
        private String status;
        private String approvalStatus;
        private Integer vacanciesCount;
        private Long companyId;
        private String companyName;
        private String companyLogo;
        private String postedAt;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getDepartment() { return department; }
        public void setDepartment(String department) { this.department = department; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }
        public String getEmploymentType() { return employmentType; }
        public void setEmploymentType(String employmentType) { this.employmentType = employmentType; }
        public String getExperienceLevel() { return experienceLevel; }
        public void setExperienceLevel(String experienceLevel) { this.experienceLevel = experienceLevel; }
        public BigDecimal getMinSalary() { return minSalary; }
        public void setMinSalary(BigDecimal minSalary) { this.minSalary = minSalary; }
        public BigDecimal getMaxSalary() { return maxSalary; }
        public void setMaxSalary(BigDecimal maxSalary) { this.maxSalary = maxSalary; }
        public String getCurrency() { return currency; }
        public void setCurrency(String currency) { this.currency = currency; }
        public List<String> getRequiredSkills() { return requiredSkills; }
        public void setRequiredSkills(List<String> requiredSkills) { this.requiredSkills = requiredSkills; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getApprovalStatus() { return approvalStatus; }
        public void setApprovalStatus(String approvalStatus) { this.approvalStatus = approvalStatus; }
        public Integer getVacanciesCount() { return vacanciesCount; }
        public void setVacanciesCount(Integer vacanciesCount) { this.vacanciesCount = vacanciesCount; }
        public Long getCompanyId() { return companyId; }
        public void setCompanyId(Long companyId) { this.companyId = companyId; }
        public String getCompanyName() { return companyName; }
        public void setCompanyName(String companyName) { this.companyName = companyName; }
        public String getCompanyLogo() { return companyLogo; }
        public void setCompanyLogo(String companyLogo) { this.companyLogo = companyLogo; }
        public String getPostedAt() { return postedAt; }
        public void setPostedAt(String postedAt) { this.postedAt = postedAt; }
    }
}
