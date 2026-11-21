package com.hiresmart.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AuthDtos {

    public static class RegisterRequest {
        @NotBlank @Email
        private String email;
        @NotBlank @Size(min = 6)
        private String password;
        private String fullName;
        private String firstName;
        private String lastName;
        private String phone;
        @NotBlank
        private String role;
        private String companyName;
        private String companyWebsite;
        private String industry;
        private Integer vacanciesCount;
        private String degree;
        private String college;
        private Integer graduationYear;
        private String cgpa;

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getFullName() {
            if (fullName != null && !fullName.isBlank()) return fullName;
            if (firstName != null || lastName != null) {
                return ((firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "")).trim();
            }
            return "User";
        }
        public void setFullName(String fullName) { this.fullName = fullName; }
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        public String getCompanyName() { return companyName; }
        public void setCompanyName(String companyName) { this.companyName = companyName; }
        public String getCompanyWebsite() { return companyWebsite; }
        public void setCompanyWebsite(String companyWebsite) { this.companyWebsite = companyWebsite; }
        public String getIndustry() { return industry; }
        public void setIndustry(String industry) { this.industry = industry; }
        public Integer getVacanciesCount() { return vacanciesCount; }
        public void setVacanciesCount(Integer vacanciesCount) { this.vacanciesCount = vacanciesCount; }
        public String getDegree() { return degree; }
        public void setDegree(String degree) { this.degree = degree; }
        public String getCollege() { return college; }
        public void setCollege(String college) { this.college = college; }
        public Integer getGraduationYear() { return graduationYear; }
        public void setGraduationYear(Integer graduationYear) { this.graduationYear = graduationYear; }
        public String getCgpa() { return cgpa; }
        public void setCgpa(String cgpa) { this.cgpa = cgpa; }
    }

    public static class LoginRequest {
        @NotBlank @Email
        private String email;
        @NotBlank
        private String password;

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class OtpVerifyRequest {
        @NotBlank @Email
        private String email;
        @NotBlank
        private String otpCode;
        @NotBlank
        private String purpose;

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getOtpCode() { return otpCode; }
        public void setOtpCode(String otpCode) { this.otpCode = otpCode; }
        public String getPurpose() { return purpose; }
        public void setPurpose(String purpose) { this.purpose = purpose; }
    }

    public static class ForgotPasswordRequest {
        @NotBlank @Email
        private String email;

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }

    public static class ResetPasswordRequest {
        @NotBlank @Email
        private String email;
        @NotBlank
        private String otpCode;
        @NotBlank @Size(min = 6)
        private String newPassword;

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getOtpCode() { return otpCode; }
        public void setOtpCode(String otpCode) { this.otpCode = otpCode; }
        public String getNewPassword() { return newPassword; }
        public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    }

    public static class ResendOtpRequest {
        @NotBlank @Email
        private String email;
        @NotBlank
        private String purpose;

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPurpose() { return purpose; }
        public void setPurpose(String purpose) { this.purpose = purpose; }
    }

    public static class AuthResponse {
        private String token;
        private Long userId;
        private String email;
        private String fullName;
        private String role;
        private String avatarUrl;
        private Long candidateId;
        private Long recruiterId;
        private Long companyId;
        private String companyName;

        public AuthResponse(String token, Long userId, String email, String fullName, String role) {
            this.token = token;
            this.userId = userId;
            this.email = email;
            this.fullName = fullName;
            this.role = role;
        }

        public String getToken() { return token; }
        public Long getUserId() { return userId; }
        public String getEmail() { return email; }
        public String getFullName() { return fullName; }
        public String getRole() { return role; }
        public String getAvatarUrl() { return avatarUrl; }
        public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
        public Long getCandidateId() { return candidateId; }
        public void setCandidateId(Long candidateId) { this.candidateId = candidateId; }
        public Long getRecruiterId() { return recruiterId; }
        public void setRecruiterId(Long recruiterId) { this.recruiterId = recruiterId; }
        public Long getCompanyId() { return companyId; }
        public void setCompanyId(Long companyId) { this.companyId = companyId; }
        public String getCompanyName() { return companyName; }
        public void setCompanyName(String companyName) { this.companyName = companyName; }
    }
}
