package com.hiresmart.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hiresmart.dto.AiDtos;
import com.hiresmart.entity.Candidate;
import com.hiresmart.entity.Resume;
import com.hiresmart.entity.ResumeAnalysis;
import com.hiresmart.repository.CandidateRepository;
import com.hiresmart.repository.ResumeAnalysisRepository;
import com.hiresmart.repository.ResumeRepository;
import com.hiresmart.service.AiResumeAnalysisService;
import com.hiresmart.service.PdfExtractionService;
import com.hiresmart.service.ResumeStorageService;
import io.jsonwebtoken.Claims;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/resumes")
public class ResumeController {

    private final ResumeStorageService resumeStorageService;
    private final PdfExtractionService pdfExtractionService;
    private final AiResumeAnalysisService aiResumeAnalysisService;
    private final ResumeRepository resumeRepository;
    private final ResumeAnalysisRepository resumeAnalysisRepository;
    private final CandidateRepository candidateRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ResumeController(ResumeStorageService resumeStorageService,
                            PdfExtractionService pdfExtractionService,
                            AiResumeAnalysisService aiResumeAnalysisService,
                            ResumeRepository resumeRepository,
                            ResumeAnalysisRepository resumeAnalysisRepository,
                            CandidateRepository candidateRepository) {
        this.resumeStorageService = resumeStorageService;
        this.pdfExtractionService = pdfExtractionService;
        this.aiResumeAnalysisService = aiResumeAnalysisService;
        this.resumeRepository = resumeRepository;
        this.resumeAnalysisRepository = resumeAnalysisRepository;
        this.candidateRepository = candidateRepository;
    }

    /**
     * Upload resume:
     * 1. Uploads PDF to Cloudflare R2 object storage.
     * 2. Extracts text using Apache PDFBox.
     * 3. Stores metadata, Cloudflare R2 URL, and extracted text in SQL database.
     * 4. Asynchronously triggers AI Resume Analysis.
     */
    @PostMapping("/upload")
    @PreAuthorize("hasRole('CANDIDATE')")
    public ResponseEntity<?> uploadResume(@RequestParam("file") MultipartFile file,
                                          Authentication authentication) throws IOException {
        Claims claims = (Claims) authentication.getDetails();
        Long userId = claims.get("userId", Long.class);
        Candidate candidate = candidateRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalStateException("Candidate profile not found."));

        // 1. Upload PDF to Cloudflare R2 (Returns Cloudflare R2 storage key and CDN URL)
        ResumeStorageService.UploadResult uploadResult = resumeStorageService.uploadResume(file, candidate.getId());

        // 2. Extract plain text using Apache PDFBox
        String extractedText = pdfExtractionService.extractTextFromPdf(file);

        // 3. Save resume metadata and Cloudflare R2 link to MySQL
        Resume resume = new Resume();
        resume.setCandidate(candidate);
        resume.setOriginalFilename(file.getOriginalFilename());
        resume.setStorageKey(uploadResult.getStorageKey());
        resume.setStorageUrl(uploadResult.getStorageUrl());
        resume.setFileSize(file.getSize());
        resume.setContentType(file.getContentType());
        resume.setExtractedText(extractedText);
        resume = resumeRepository.save(resume);

        // 4. Trigger AI Resume Analyzer
        AiDtos.ResumeAnalysisDto analysisDto = aiResumeAnalysisService.analyzeResumeText(extractedText);
        ResumeAnalysis analysis = new ResumeAnalysis();
        analysis.setResume(resume);
        analysis.setSummary(analysisDto.getSummary());
        analysis.setEducationSummary(analysisDto.getEducationSummary());
        analysis.setExperienceSummary(analysisDto.getExperienceSummary());

        try {
            analysis.setTechnicalSkills(objectMapper.writeValueAsString(analysisDto.getTechnicalSkills()));
            analysis.setSoftSkills(objectMapper.writeValueAsString(analysisDto.getSoftSkills()));
            analysis.setStrengths(objectMapper.writeValueAsString(analysisDto.getStrengths()));
            analysis.setGapsMissingInfo(objectMapper.writeValueAsString(analysisDto.getGapsMissingInfo()));
            analysis.setSuggestedImprovements(objectMapper.writeValueAsString(analysisDto.getSuggestedImprovements()));
            analysis.setPotentialJobRoles(objectMapper.writeValueAsString(analysisDto.getPotentialJobRoles()));
            analysis.setRawAiResponse(objectMapper.writeValueAsString(analysisDto));
        } catch (JsonProcessingException ignored) {}

        resumeAnalysisRepository.save(analysis);

        return ResponseEntity.ok(Map.of(
                "message", "Resume uploaded to Cloudflare R2 & analyzed successfully!",
                "resumeId", resume.getId(),
                "originalFilename", resume.getOriginalFilename(),
                "storageKey", resume.getStorageKey(),
                "storageUrl", resume.getStorageUrl(),
                "analysis", analysisDto
        ));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('CANDIDATE')")
    public ResponseEntity<List<Resume>> getMyResumes(Authentication authentication) {
        Claims claims = (Claims) authentication.getDetails();
        Long userId = claims.get("userId", Long.class);
        Candidate candidate = candidateRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalStateException("Candidate profile not found."));

        return ResponseEntity.ok(resumeRepository.findByCandidateIdOrderByUploadTimestampDesc(candidate.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getResumeById(@PathVariable Long id) {
        Resume resume = resumeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Resume not found."));
        return ResponseEntity.ok(resume);
    }

    @GetMapping("/download/{filename:.+}")
    public ResponseEntity<?> downloadResume(@PathVariable String filename) {
        try {
            java.nio.file.Path filePath = java.nio.file.Paths.get("uploads", "resumes").resolve(filename).normalize();
            if (!java.nio.file.Files.exists(filePath)) {
                return ResponseEntity.notFound().build();
            }
            org.springframework.core.io.Resource resource = new org.springframework.core.io.UrlResource(filePath.toUri());
            return ResponseEntity.ok()
                    .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                    .contentType(org.springframework.http.MediaType.APPLICATION_PDF)
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
