package com.hiresmart.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hiresmart.dto.AiDtos;
import com.hiresmart.dto.ApplicationDtos;
import com.hiresmart.entity.*;
import com.hiresmart.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final JobRepository jobRepository;
    private final CandidateRepository candidateRepository;
    private final ResumeRepository resumeRepository;
    private final ApplicationStatusHistoryRepository statusHistoryRepository;
    private final AuditLogRepository auditLogRepository;
    private final AiJobMatchingService aiJobMatchingService;
    private final EmailNotificationService emailNotificationService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ApplicationService(ApplicationRepository applicationRepository,
                              JobRepository jobRepository,
                              CandidateRepository candidateRepository,
                              ResumeRepository resumeRepository,
                              ApplicationStatusHistoryRepository statusHistoryRepository,
                              AuditLogRepository auditLogRepository,
                              AiJobMatchingService aiJobMatchingService,
                              EmailNotificationService emailNotificationService) {
        this.applicationRepository = applicationRepository;
        this.jobRepository = jobRepository;
        this.candidateRepository = candidateRepository;
        this.resumeRepository = resumeRepository;
        this.statusHistoryRepository = statusHistoryRepository;
        this.auditLogRepository = auditLogRepository;
        this.aiJobMatchingService = aiJobMatchingService;
        this.emailNotificationService = emailNotificationService;
    }

    @Transactional
    public ApplicationDtos.ApplicationResponse applyForJob(Long jobId, Long resumeId, Long userId) {
        Candidate candidate = candidateRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalStateException("Candidate profile not found."));

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Job not found."));

        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new IllegalArgumentException("Resume not found."));

        if (applicationRepository.findByJobIdAndCandidateId(jobId, candidate.getId()).isPresent()) {
            throw new IllegalStateException("You have already applied for this job.");
        }

        Application application = new Application();
        application.setJob(job);
        application.setCandidate(candidate);
        application.setResume(resume);
        application.setStatus("APPLIED");

        // Run Explainable AI Job Matching Engine immediately upon application
        AiDtos.JobMatchDto match = aiJobMatchingService.calculateMatchScore(candidate, resume, job);
        application.setMatchScore(match.getOverallScore());
        application.setSkillsMatchScore(match.getSkillsScore());
        application.setExperienceMatchScore(match.getExperienceScore());
        application.setEducationMatchScore(match.getEducationScore());
        application.setMatchExplanation(match.getExplanation());

        try {
            application.setMatchedSkills(objectMapper.writeValueAsString(match.getMatchedSkills()));
            application.setMissingSkills(objectMapper.writeValueAsString(match.getMissingSkills()));
        } catch (JsonProcessingException e) {
            application.setMatchedSkills("[]");
            application.setMissingSkills("[]");
        }

        application = applicationRepository.save(application);

        // Record Status History
        ApplicationStatusHistory history = new ApplicationStatusHistory();
        history.setApplication(application);
        history.setPreviousStatus(null);
        history.setNewStatus("APPLIED");
        history.setNotes("Candidate submitted application with resume: " + resume.getOriginalFilename());
        statusHistoryRepository.save(history);

        // Audit Log
        AuditLog log = new AuditLog();
        log.setUser(candidate.getUser());
        log.setCompany(job.getCompany());
        log.setAction("APPLICATION_SUBMITTED");
        log.setEntityType("APPLICATION");
        log.setEntityId(application.getId());
        auditLogRepository.save(log);

        return mapToDto(application);
    }

    public List<ApplicationDtos.ApplicationResponse> getMyApplications(Long userId) {
        Candidate candidate = candidateRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalStateException("Candidate profile not found."));
        return applicationRepository.findByCandidateId(candidate.getId()).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public List<ApplicationDtos.ApplicationResponse> getApplicationsForJob(Long jobId) {
        return applicationRepository.findByJobId(jobId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public List<ApplicationDtos.ApplicationResponse> getApplicationsForCompany(Long companyId) {
        return applicationRepository.findByJobCompanyId(companyId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ApplicationDtos.ApplicationResponse updateApplicationStatus(Long applicationId, ApplicationDtos.StatusUpdateRequest request, User recruiterUser) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("Application not found."));

        String oldStatus = application.getStatus();
        application.setStatus(request.getStatus().toUpperCase());
        if (request.getRejectionReason() != null) {
            application.setRejectionReason(request.getRejectionReason());
        }

        application = applicationRepository.save(application);

        // Record Status History
        ApplicationStatusHistory history = new ApplicationStatusHistory();
        history.setApplication(application);
        history.setPreviousStatus(oldStatus);
        history.setNewStatus(request.getStatus().toUpperCase());
        history.setChangedByUser(recruiterUser);
        history.setNotes(request.getNotes() != null ? request.getNotes() : "Recruiter updated status to " + request.getStatus());
        statusHistoryRepository.save(history);

        // Send Email notification to candidate
        emailNotificationService.sendApplicationStatusUpdate(
                application.getCandidate().getUser().getEmail(),
                application.getCandidate().getUser().getFullName(),
                application.getJob().getTitle(),
                application.getStatus()
        );

        return mapToDto(application);
    }

    private ApplicationDtos.ApplicationResponse mapToDto(Application app) {
        ApplicationDtos.ApplicationResponse dto = new ApplicationDtos.ApplicationResponse();
        dto.setId(app.getId());
        dto.setJobId(app.getJob().getId());
        dto.setJobTitle(app.getJob().getTitle());
        dto.setCompanyName(app.getJob().getCompany().getName());
        dto.setCandidateId(app.getCandidate().getId());
        dto.setCandidateName(app.getCandidate().getUser().getFullName());
        dto.setCandidateEmail(app.getCandidate().getUser().getEmail());
        dto.setResumeId(app.getResume().getId());
        dto.setResumeStorageUrl(app.getResume().getStorageUrl());
        dto.setStatus(app.getStatus());
        dto.setMatchScore(app.getMatchScore());
        dto.setSkillsMatchScore(app.getSkillsMatchScore());
        dto.setExperienceMatchScore(app.getExperienceMatchScore());
        dto.setEducationMatchScore(app.getEducationMatchScore());
        dto.setMatchExplanation(app.getMatchExplanation());
        dto.setAppliedAt(app.getAppliedAt().toString());

        try {
            dto.setMatchedSkills(objectMapper.readValue(app.getMatchedSkills(), new TypeReference<List<String>>() {}));
        } catch (Exception e) {
            dto.setMatchedSkills(Collections.emptyList());
        }

        try {
            dto.setMissingSkills(objectMapper.readValue(app.getMissingSkills(), new TypeReference<List<String>>() {}));
        } catch (Exception e) {
            dto.setMissingSkills(Collections.emptyList());
        }

        return dto;
    }
}
