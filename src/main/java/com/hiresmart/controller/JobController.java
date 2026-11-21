package com.hiresmart.controller;

import com.hiresmart.dto.JobDtos;
import com.hiresmart.service.JobService;
import io.jsonwebtoken.Claims;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @GetMapping
    public ResponseEntity<List<JobDtos.JobResponse>> getAllJobs(@RequestParam(required = false) String search) {
        return ResponseEntity.ok(jobService.getAllJobs(search));
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobDtos.JobResponse> getJobById(@PathVariable Long id) {
        return ResponseEntity.ok(jobService.getJobById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('RECRUITER', 'ADMIN')")
    public ResponseEntity<JobDtos.JobResponse> createJob(@Valid @RequestBody JobDtos.JobCreateRequest request,
                                                         Authentication authentication) {
        Claims claims = (Claims) authentication.getDetails();
        Long userId = claims.get("userId", Long.class);
        return ResponseEntity.ok(jobService.createJob(request, userId));
    }

    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<JobDtos.JobResponse>> getJobsByCompany(@PathVariable Long companyId) {
        return ResponseEntity.ok(jobService.getJobsByCompany(companyId));
    }
}
