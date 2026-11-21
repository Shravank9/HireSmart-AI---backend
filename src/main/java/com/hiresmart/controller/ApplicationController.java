package com.hiresmart.controller;

import com.hiresmart.dto.ApplicationDtos;
import com.hiresmart.entity.User;
import com.hiresmart.repository.UserRepository;
import com.hiresmart.service.ApplicationService;
import io.jsonwebtoken.Claims;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

    private final ApplicationService applicationService;
    private final UserRepository userRepository;

    public ApplicationController(ApplicationService applicationService, UserRepository userRepository) {
        this.applicationService = applicationService;
        this.userRepository = userRepository;
    }

    @PostMapping
    @PreAuthorize("hasRole('CANDIDATE')")
    public ResponseEntity<ApplicationDtos.ApplicationResponse> applyForJob(@RequestBody ApplicationDtos.ApplyRequest request,
                                                                           Authentication authentication) {
        Claims claims = (Claims) authentication.getDetails();
        Long userId = claims.get("userId", Long.class);
        return ResponseEntity.ok(applicationService.applyForJob(request.getJobId(), request.getResumeId(), userId));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('CANDIDATE')")
    public ResponseEntity<List<ApplicationDtos.ApplicationResponse>> getMyApplications(Authentication authentication) {
        Claims claims = (Claims) authentication.getDetails();
        Long userId = claims.get("userId", Long.class);
        return ResponseEntity.ok(applicationService.getMyApplications(userId));
    }

    @GetMapping("/job/{jobId}")
    @PreAuthorize("hasAnyRole('RECRUITER', 'ADMIN')")
    public ResponseEntity<List<ApplicationDtos.ApplicationResponse>> getApplicationsForJob(@PathVariable Long jobId) {
        return ResponseEntity.ok(applicationService.getApplicationsForJob(jobId));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('RECRUITER', 'ADMIN')")
    public ResponseEntity<ApplicationDtos.ApplicationResponse> updateStatus(@PathVariable Long id,
                                                                            @RequestBody ApplicationDtos.StatusUpdateRequest request,
                                                                            Authentication authentication) {
        Claims claims = (Claims) authentication.getDetails();
        Long userId = claims.get("userId", Long.class);
        User recruiterUser = userRepository.findById(userId).orElse(null);
        return ResponseEntity.ok(applicationService.updateApplicationStatus(id, request, recruiterUser));
    }
}
