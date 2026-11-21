package com.hiresmart.controller;

import com.hiresmart.entity.Company;
import com.hiresmart.entity.Job;
import com.hiresmart.entity.User;
import com.hiresmart.repository.CompanyRepository;
import com.hiresmart.repository.JobRepository;
import com.hiresmart.repository.UserRepository;
import com.hiresmart.service.DashboardAnalyticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final DashboardAnalyticsService analyticsService;
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final JobRepository jobRepository;

    public AdminController(DashboardAnalyticsService analyticsService,
                           UserRepository userRepository,
                           CompanyRepository companyRepository,
                           JobRepository jobRepository) {
        this.analyticsService = analyticsService;
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
        this.jobRepository = jobRepository;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getAdminDashboard() {
        return ResponseEntity.ok(analyticsService.getAdminDashboardStats());
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    @GetMapping("/companies")
    public ResponseEntity<List<Company>> getAllCompanies() {
        return ResponseEntity.ok(companyRepository.findAll());
    }

    @GetMapping("/jobs/pending")
    public ResponseEntity<List<Job>> getPendingJobs() {
        return ResponseEntity.ok(jobRepository.findByApprovalStatus("PENDING_APPROVAL"));
    }

    @PutMapping("/jobs/{id}/approve")
    public ResponseEntity<?> approveJob(@PathVariable Long id) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Job not found with id: " + id));
        job.setApprovalStatus("APPROVED");
        jobRepository.save(job);
        return ResponseEntity.ok(Map.of("message", "Job vacancy '" + job.getTitle() + "' has been approved and published."));
    }

    @PutMapping("/jobs/{id}/reject")
    public ResponseEntity<?> rejectJob(@PathVariable Long id, @RequestBody(required = false) Map<String, String> payload) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Job not found with id: " + id));
        job.setApprovalStatus("REJECTED");
        jobRepository.save(job);
        String reason = payload != null && payload.containsKey("reason") ? payload.get("reason") : "Does not meet guidelines";
        return ResponseEntity.ok(Map.of("message", "Job vacancy rejected.", "reason", reason));
    }

    @PutMapping("/users/{id}/status")
    public ResponseEntity<?> updateUserStatus(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        String status = payload.get("status");
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setStatus(status.toUpperCase());
        userRepository.save(user);
        return ResponseEntity.ok(Map.of("message", "User status updated to " + status));
    }
}
