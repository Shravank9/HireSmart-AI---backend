package com.hiresmart.controller;

import com.hiresmart.dto.ApplicationDtos;
import com.hiresmart.entity.Interview;
import com.hiresmart.entity.Recruiter;
import com.hiresmart.repository.InterviewRepository;
import com.hiresmart.repository.RecruiterRepository;
import com.hiresmart.service.ApplicationService;
import com.hiresmart.service.DashboardAnalyticsService;
import com.hiresmart.service.EmailNotificationService;
import io.jsonwebtoken.Claims;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/recruiter")
@PreAuthorize("hasAnyRole('RECRUITER', 'ADMIN')")
public class RecruiterController {

    private final DashboardAnalyticsService analyticsService;
    private final ApplicationService applicationService;
    private final RecruiterRepository recruiterRepository;
    private final InterviewRepository interviewRepository;
    private final EmailNotificationService emailNotificationService;

    public RecruiterController(DashboardAnalyticsService analyticsService,
                               ApplicationService applicationService,
                               RecruiterRepository recruiterRepository,
                               InterviewRepository interviewRepository,
                               EmailNotificationService emailNotificationService) {
        this.analyticsService = analyticsService;
        this.applicationService = applicationService;
        this.recruiterRepository = recruiterRepository;
        this.interviewRepository = interviewRepository;
        this.emailNotificationService = emailNotificationService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard(Authentication authentication) {
        Claims claims = (Claims) authentication.getDetails();
        Long userId = claims.get("userId", Long.class);
        Recruiter recruiter = recruiterRepository.findByUserId(userId).orElse(null);
        Long companyId = recruiter != null ? recruiter.getCompany().getId() : 1L;
        return ResponseEntity.ok(analyticsService.getRecruiterDashboardStats(companyId));
    }

    @GetMapping("/candidates")
    public ResponseEntity<List<ApplicationDtos.ApplicationResponse>> getCandidates(Authentication authentication) {
        Claims claims = (Claims) authentication.getDetails();
        Long userId = claims.get("userId", Long.class);
        Recruiter recruiter = recruiterRepository.findByUserId(userId).orElse(null);
        Long companyId = recruiter != null ? recruiter.getCompany().getId() : 1L;
        return ResponseEntity.ok(applicationService.getApplicationsForCompany(companyId));
    }

    @PostMapping("/interviews")
    public ResponseEntity<?> scheduleInterview(@RequestBody Map<String, Object> payload, Authentication authentication) {
        String scheduledTime = payload.getOrDefault("scheduledTime", LocalDateTime.now().plusDays(2).toString()).toString();
        String meetingLink = payload.getOrDefault("meetingLink", "https://meet.google.com/hms-" + System.currentTimeMillis()).toString();
        String candidateEmail = (String) payload.get("candidateEmail");
        String candidateName = (String) payload.getOrDefault("candidateName", "Candidate");
        String jobTitle = (String) payload.getOrDefault("jobTitle", "Software Engineer");

        if (candidateEmail != null && !candidateEmail.trim().isEmpty()) {
            emailNotificationService.sendInterviewInvitation(candidateEmail.trim(), candidateName, jobTitle, scheduledTime, meetingLink);
        }

        return ResponseEntity.ok(Map.of(
                "message", "Interview scheduled and email invitation dispatched to " + candidateEmail + "!",
                "meetingLink", meetingLink,
                "scheduledTime", scheduledTime
        ));
    }
}
