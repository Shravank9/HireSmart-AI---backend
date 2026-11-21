package com.hiresmart.service;

import com.hiresmart.config.JwtTokenProvider;
import com.hiresmart.dto.AuthDtos;
import com.hiresmart.entity.*;
import com.hiresmart.repository.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final CandidateRepository candidateRepository;
    private final RecruiterRepository recruiterRepository;
    private final CompanyRepository companyRepository;
    private final EmailVerificationRepository emailVerificationRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final EmailNotificationService emailNotificationService;

    public AuthService(UserRepository userRepository,
                       CandidateRepository candidateRepository,
                       RecruiterRepository recruiterRepository,
                       CompanyRepository companyRepository,
                       EmailVerificationRepository emailVerificationRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider tokenProvider,
                       EmailNotificationService emailNotificationService) {
        this.userRepository = userRepository;
        this.candidateRepository = candidateRepository;
        this.recruiterRepository = recruiterRepository;
        this.companyRepository = companyRepository;
        this.emailVerificationRepository = emailVerificationRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
        this.emailNotificationService = emailNotificationService;
    }

    @Transactional
    public AuthDtos.AuthResponse register(AuthDtos.RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail().toLowerCase().trim())) {
            throw new IllegalArgumentException("An account with email " + request.getEmail() + " already exists.");
        }

        User user = new User();
        user.setEmail(request.getEmail().toLowerCase().trim());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setRole(request.getRole().toUpperCase());
        user.setIsVerified(true);
        user.setStatus("ACTIVE");
        user = userRepository.save(user);

        Long candidateId = null;
        Long recruiterId = null;
        Long companyId = null;
        String companyName = null;

        if ("CANDIDATE".equalsIgnoreCase(request.getRole())) {
            Candidate candidate = new Candidate();
            candidate.setUser(user);
            candidate.setTitle("Software Candidate");
            if (request.getPhone() != null) candidate.setPhone(request.getPhone());
            if (request.getDegree() != null || request.getCollege() != null) {
                String eduJson = String.format("[{\"degree\":\"%s\",\"institution\":\"%s\",\"graduationYear\":\"%s\",\"cgpa\":\"%s\"}]",
                        request.getDegree() != null ? request.getDegree().replace("\"", "\\\"") : "",
                        request.getCollege() != null ? request.getCollege().replace("\"", "\\\"") : "",
                        request.getGraduationYear() != null ? request.getGraduationYear().toString() : "",
                        request.getCgpa() != null ? request.getCgpa().replace("\"", "\\\"") : "");
                candidate.setEducationJson(eduJson);
            }
            candidate = candidateRepository.save(candidate);
            candidateId = candidate.getId();
        } else if ("RECRUITER".equalsIgnoreCase(request.getRole())) {
            String compName = request.getCompanyName() != null ? request.getCompanyName() : "Tech Solutions Inc";
            Company company = companyRepository.findByName(compName)
                    .orElseGet(() -> {
                        Company newComp = new Company();
                        newComp.setName(compName);
                        newComp.setIndustry(request.getIndustry() != null ? request.getIndustry() : "Technology & Software");
                        if (request.getCompanyWebsite() != null) newComp.setWebsite(request.getCompanyWebsite());
                        return companyRepository.save(newComp);
                    });
            companyId = company.getId();
            companyName = company.getName();

            Recruiter recruiter = new Recruiter();
            recruiter.setUser(user);
            recruiter.setCompany(company);
            if (request.getPhone() != null) recruiter.setPhone(request.getPhone());
            recruiter = recruiterRepository.save(recruiter);
            recruiterId = recruiter.getId();
        }

        // Generate & send OTP
        String otp = generateAndSaveOtp(user.getEmail(), "REGISTRATION");
        emailNotificationService.sendRegistrationOtp(user.getEmail(), otp);

        String token = tokenProvider.generateToken(user.getEmail(), user.getId(), user.getRole(), user.getFullName());
        AuthDtos.AuthResponse response = new AuthDtos.AuthResponse(token, user.getId(), user.getEmail(), user.getFullName(), user.getRole());
        response.setCandidateId(candidateId);
        response.setRecruiterId(recruiterId);
        response.setCompanyId(companyId);
        response.setCompanyName(companyName);
        return response;
    }

    public AuthDtos.AuthResponse login(AuthDtos.LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail().toLowerCase().trim())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid email or password.");
        }

        if ("SUSPENDED".equals(user.getStatus())) {
            throw new IllegalArgumentException("Your account has been suspended. Contact admin.");
        }

        String token = tokenProvider.generateToken(user.getEmail(), user.getId(), user.getRole(), user.getFullName());
        AuthDtos.AuthResponse response = new AuthDtos.AuthResponse(token, user.getId(), user.getEmail(), user.getFullName(), user.getRole());

        if ("CANDIDATE".equals(user.getRole())) {
            candidateRepository.findByUserId(user.getId()).ifPresent(c -> response.setCandidateId(c.getId()));
        } else if ("RECRUITER".equals(user.getRole())) {
            recruiterRepository.findByUserId(user.getId()).ifPresent(r -> {
                response.setRecruiterId(r.getId());
                response.setCompanyId(r.getCompany().getId());
                response.setCompanyName(r.getCompany().getName());
            });
        }

        return response;
    }

    public boolean verifyOtp(AuthDtos.OtpVerifyRequest request) {
        return emailVerificationRepository.findTopByEmailAndPurposeAndIsUsedFalseAndExpiresAtAfterOrderByCreatedAtDesc(
                request.getEmail().toLowerCase().trim(), request.getPurpose(), LocalDateTime.now())
                .map(v -> {
                    v.setAttemptsCount(v.getAttemptsCount() + 1);
                    if (v.getAttemptsCount() > 5) {
                        emailVerificationRepository.save(v);
                        return false;
                    }
                    if (v.getOtpCode().equals(request.getOtpCode())) {
                        v.setIsUsed(true);
                        emailVerificationRepository.save(v);
                        userRepository.findByEmail(request.getEmail().toLowerCase().trim()).ifPresent(u -> {
                            u.setIsVerified(true);
                            userRepository.save(u);
                        });
                        return true;
                    }
                    emailVerificationRepository.save(v);
                    return false;
                }).orElse(false);
    }

    @Transactional
    public void forgotPassword(String email) {
        User user = userRepository.findByEmail(email.toLowerCase().trim())
                .orElseThrow(() -> new IllegalArgumentException("No account found with email: " + email));

        String otp = generateAndSaveOtp(user.getEmail(), "PASSWORD_RESET");
        emailNotificationService.sendPasswordResetOtp(user.getEmail(), otp);
    }

    @Transactional
    public void resetPassword(AuthDtos.ResetPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail().toLowerCase().trim())
                .orElseThrow(() -> new IllegalArgumentException("No account found with this email."));

        boolean otpValid = emailVerificationRepository
                .findTopByEmailAndPurposeAndIsUsedFalseAndExpiresAtAfterOrderByCreatedAtDesc(
                        request.getEmail().toLowerCase().trim(), "PASSWORD_RESET", LocalDateTime.now())
                .map(v -> {
                    if (v.getOtpCode().equals(request.getOtpCode())) {
                        v.setIsUsed(true);
                        emailVerificationRepository.save(v);
                        return true;
                    }
                    return false;
                }).orElse(false);

        if (!otpValid) {
            throw new IllegalArgumentException("Invalid or expired OTP code.");
        }

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Transactional
    public void resendOtp(String email, String purpose) {
        String cleanEmail = email.toLowerCase().trim();
        if ("PASSWORD_RESET".equals(purpose)) {
            userRepository.findByEmail(cleanEmail)
                    .orElseThrow(() -> new IllegalArgumentException("No account found with this email."));
            String otp = generateAndSaveOtp(cleanEmail, purpose);
            emailNotificationService.sendPasswordResetOtp(cleanEmail, otp);
        } else {
            String otp = generateAndSaveOtp(cleanEmail, purpose != null ? purpose : "REGISTRATION");
            emailNotificationService.sendRegistrationOtp(cleanEmail, otp);
        }
    }

    private String generateAndSaveOtp(String email, String purpose) {
        // Generate real 6-digit number between 100000 and 999999
        String otp = String.valueOf(100000 + new Random().nextInt(900000));
        EmailVerification verification = new EmailVerification();
        verification.setEmail(email);
        verification.setOtpCode(otp);
        verification.setPurpose(purpose);
        verification.setExpiresAt(LocalDateTime.now().plusMinutes(15));
        emailVerificationRepository.save(verification);
        return otp;
    }
}
