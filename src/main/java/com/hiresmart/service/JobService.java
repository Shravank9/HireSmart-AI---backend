package com.hiresmart.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hiresmart.dto.JobDtos;
import com.hiresmart.entity.*;
import com.hiresmart.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JobService {

    private final JobRepository jobRepository;
    private final RecruiterRepository recruiterRepository;
    private final CompanyRepository companyRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JobService(JobRepository jobRepository,
                      RecruiterRepository recruiterRepository,
                      CompanyRepository companyRepository) {
        this.jobRepository = jobRepository;
        this.recruiterRepository = recruiterRepository;
        this.companyRepository = companyRepository;
    }

    public List<JobDtos.JobResponse> getAllJobs(String search) {
        List<Job> jobs;
        if (search != null && !search.trim().isEmpty()) {
            jobs = jobRepository.searchActiveJobs(search.trim());
        } else {
            jobs = jobRepository.findByStatus("ACTIVE");
        }
        return jobs.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    public JobDtos.JobResponse getJobById(Long id) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Job not found with id: " + id));
        return mapToDto(job);
    }

    @Transactional
    public JobDtos.JobResponse createJob(JobDtos.JobCreateRequest request, Long userId) {
        Recruiter recruiter = recruiterRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalStateException("Only registered recruiters can create jobs."));

        Job job = new Job();
        job.setCompany(recruiter.getCompany());
        job.setRecruiter(recruiter);
        job.setTitle(request.getTitle());
        job.setDepartment(request.getDepartment());
        job.setDescription(request.getDescription());
        job.setLocation(request.getLocation());
        job.setEmploymentType(request.getEmploymentType());
        job.setExperienceLevel(request.getExperienceLevel());
        job.setMinSalary(request.getMinSalary());
        job.setMaxSalary(request.getMaxSalary());
        job.setCurrency(request.getCurrency());
        job.setStatus("ACTIVE");
        job.setApprovalStatus("APPROVED");
        job.setVacanciesCount(request.getVacanciesCount() != null ? request.getVacanciesCount() : 1);

        try {
            job.setRequiredSkills(objectMapper.writeValueAsString(request.getRequiredSkills()));
        } catch (JsonProcessingException e) {
            job.setRequiredSkills("[]");
        }

        job = jobRepository.save(job);
        return mapToDto(job);
    }

    public List<JobDtos.JobResponse> getJobsByCompany(Long companyId) {
        return jobRepository.findByCompanyId(companyId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private JobDtos.JobResponse mapToDto(Job job) {
        JobDtos.JobResponse dto = new JobDtos.JobResponse();
        dto.setId(job.getId());
        dto.setTitle(job.getTitle());
        dto.setDepartment(job.getDepartment());
        dto.setDescription(job.getDescription());
        dto.setLocation(job.getLocation());
        dto.setEmploymentType(job.getEmploymentType());
        dto.setExperienceLevel(job.getExperienceLevel());
        dto.setMinSalary(job.getMinSalary());
        dto.setMaxSalary(job.getMaxSalary());
        dto.setCurrency(job.getCurrency());
        dto.setStatus(job.getStatus());
        dto.setApprovalStatus(job.getApprovalStatus());
        dto.setVacanciesCount(job.getVacanciesCount());
        dto.setCompanyId(job.getCompany().getId());
        dto.setCompanyName(job.getCompany().getName());
        dto.setCompanyLogo(job.getCompany().getLogoUrl());
        dto.setPostedAt(job.getCreatedAt() != null ? job.getCreatedAt().toString() : java.time.LocalDateTime.now().toString());

        try {
            dto.setRequiredSkills(objectMapper.readValue(job.getRequiredSkills(), new TypeReference<List<String>>() {}));
        } catch (Exception e) {
            dto.setRequiredSkills(Collections.emptyList());
        }

        return dto;
    }
}
