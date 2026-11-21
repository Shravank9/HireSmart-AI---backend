package com.hiresmart.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class ResumeStorageService {

    @Nullable
    private final S3Client s3Client;

    @Value("${cloudflare.r2.bucket-name:hiresmart-resumes}")
    private String bucketName;

    @Value("${cloudflare.r2.public-url:https://pub-hiresmart.r2.dev}")
    private String publicUrl;

    public ResumeStorageService(@Autowired(required = false) @Nullable S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public static class UploadResult {
        private final String storageKey;
        private final String storageUrl;

        public UploadResult(String storageKey, String storageUrl) {
            this.storageKey = storageKey;
            this.storageUrl = storageUrl;
        }
        public String getStorageKey() { return storageKey; }
        public String getStorageUrl() { return storageUrl; }
    }

    /**
     * Uploads the resume PDF to Cloudflare R2 object storage (or local fallback).
     */
    public UploadResult uploadResume(MultipartFile file, Long candidateId) throws IOException {
        String originalFilename = file.getOriginalFilename() != null ? file.getOriginalFilename() : "resume.pdf";
        String cleanFilename = originalFilename.replaceAll("[^a-zA-Z0-9.-]", "_");
        String storageKey = "resumes/cand_" + candidateId + "_" + UUID.randomUUID().toString().substring(0, 8) + "_" + cleanFilename;

        // If Cloudflare R2 S3Client is configured, upload directly to Cloudflare R2
        if (s3Client != null) {
            try {
                PutObjectRequest putRequest = PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(storageKey)
                        .contentType(file.getContentType() != null ? file.getContentType() : "application/pdf")
                        .contentLength(file.getSize())
                        .build();

                s3Client.putObject(putRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
                
                String storageUrl = publicUrl.endsWith("/") ? publicUrl + storageKey : publicUrl + "/" + storageKey;
                System.out.println("☁️ [Cloudflare R2] Successfully uploaded to: " + storageUrl);
                return new UploadResult(storageKey, storageUrl);
            } catch (Exception e) {
                System.err.println("⚠️ Cloudflare R2 upload error, falling back to local file storage: " + e.getMessage());
            }
        }

        // Local Storage Fallback (for hackathon demo if R2 keys are not configured)
        Path uploadDir = Paths.get("uploads", "resumes");
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }
        Path targetPath = uploadDir.resolve(cleanFilename);
        Files.copy(file.getInputStream(), targetPath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);

        String localUrl = "http://localhost:8080/api/resumes/download/" + cleanFilename;
        System.out.println("💾 [Local Storage Fallback] Saved resume to: " + targetPath.toAbsolutePath());
        return new UploadResult(storageKey, localUrl);
    }
}
