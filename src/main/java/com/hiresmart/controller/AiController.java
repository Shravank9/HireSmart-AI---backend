package com.hiresmart.controller;

import com.hiresmart.dto.AiDtos;
import com.hiresmart.entity.Candidate;
import com.hiresmart.entity.Job;
import com.hiresmart.entity.Resume;
import com.hiresmart.repository.CandidateRepository;
import com.hiresmart.repository.JobRepository;
import com.hiresmart.repository.ResumeRepository;
import com.hiresmart.service.AiInterviewService;
import com.hiresmart.service.AiJobMatchingService;
import com.hiresmart.service.AiResumeAnalysisService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    private final AiResumeAnalysisService aiResumeAnalysisService;
    private final AiJobMatchingService aiJobMatchingService;
    private final AiInterviewService aiInterviewService;
    private final ResumeRepository resumeRepository;
    private final JobRepository jobRepository;
    private final CandidateRepository candidateRepository;

    public AiController(AiResumeAnalysisService aiResumeAnalysisService,
                        AiJobMatchingService aiJobMatchingService,
                        AiInterviewService aiInterviewService,
                        ResumeRepository resumeRepository,
                        JobRepository jobRepository,
                        CandidateRepository candidateRepository) {
        this.aiResumeAnalysisService = aiResumeAnalysisService;
        this.aiJobMatchingService = aiJobMatchingService;
        this.aiInterviewService = aiInterviewService;
        this.resumeRepository = resumeRepository;
        this.jobRepository = jobRepository;
        this.candidateRepository = candidateRepository;
    }

    @PostMapping("/analyze-resume")
    public ResponseEntity<AiDtos.ResumeAnalysisDto> analyzeResume(@RequestBody Map<String, Object> payload) {
        String text = (String) payload.get("text");
        if (text == null && payload.containsKey("resumeId")) {
            Long resumeId = Long.valueOf(payload.get("resumeId").toString());
            Resume resume = resumeRepository.findById(resumeId)
                    .orElseThrow(() -> new IllegalArgumentException("Resume not found"));
            text = resume.getExtractedText();
        }
        return ResponseEntity.ok(aiResumeAnalysisService.analyzeResumeText(text != null ? text : ""));
    }

    @PostMapping("/match-job")
    public ResponseEntity<AiDtos.JobMatchDto> matchJob(@RequestBody Map<String, Long> payload) {
        Long candidateId = payload.get("candidateId");
        Long jobId = payload.get("jobId");

        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new IllegalArgumentException("Candidate not found"));
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Job not found"));

        List<Resume> resumes = resumeRepository.findByCandidateIdOrderByUploadTimestampDesc(candidateId);
        if (resumes.isEmpty()) {
            throw new IllegalStateException("Candidate has not uploaded a resume yet.");
        }

        return ResponseEntity.ok(aiJobMatchingService.calculateMatchScore(candidate, resumes.get(0), job));
    }

    @PostMapping("/generate-interview-questions")
    public ResponseEntity<List<AiDtos.QuestionItem>> generateInterviewQuestions(@RequestBody Map<String, Object> payload) {
        Long jobId = Long.valueOf(payload.get("jobId").toString());
        String difficulty = payload.getOrDefault("difficulty", "MEDIUM").toString();

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Job not found"));

        return ResponseEntity.ok(aiInterviewService.generateInterviewQuestions(job, difficulty));
    }

    @PostMapping("/improve-resume")
    public ResponseEntity<?> improveResume(@RequestBody Map<String, Long> payload) {
        Long resumeId = payload.get("resumeId");
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new IllegalArgumentException("Resume not found"));

        AiDtos.ResumeAnalysisDto analysis = aiResumeAnalysisService.analyzeResumeText(resume.getExtractedText());

        return ResponseEntity.ok(Map.of(
                "originalSummary", analysis.getSummary(),
                "suggestedExecutiveSummary", "Impact-driven software engineer specialized in full-stack cloud ecosystems. Proven track record architecting mission-critical REST microservices, optimizing database throughput, and developing intuitive, high-performance web applications.",
                "actionableBulletImprovements", List.of(
                        "Original: 'Worked on Spring Boot microservice.' -> Improved: 'Architected and deployed high-throughput Spring Boot microservices handling 10k req/sec with 99.99% uptime.'",
                        "Original: 'Built React dashboard.' -> Improved: 'Engineered responsive React & Tailwind analytics dashboard, reducing initial bundle size by 35% and improving lighthouse score to 98.'"
                ),
                "atsFormattingRecommendations", List.of(
                        "Adopt a clean single-column structure with standard font hierarchies.",
                        "Group technical capabilities under standard headers: Languages, Frameworks, Cloud & Databases, Tools.",
                        "Ensure skills in job descriptions match verbatim in your technical skills section."
                )
        ));
    }

    @PostMapping("/evaluate-interview")
    public ResponseEntity<AiDtos.InterviewEvaluationDto> evaluateInterview(@RequestBody Map<String, String> payload) {
        String transcript = payload.getOrDefault("transcript", "");
        return ResponseEntity.ok(aiInterviewService.evaluateInterviewResponses(transcript));
    }
}
