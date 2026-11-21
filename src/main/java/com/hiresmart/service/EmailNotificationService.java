package com.hiresmart.service;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class EmailNotificationService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.from:${spring.mail.username:konakalla.akash1@gmail.com}}")
    private String fromEmail;

    @Value("${spring.mail.from-name:HireSmart AI}")
    private String fromName;

    public EmailNotificationService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async
    public void sendRegistrationOtp(String toEmail, String otpCode) {
        String subject = "HireSmart AI — Verification Code";
        String content = "Welcome to HireSmart AI!\n\nYour one-time verification code is: " + otpCode +
                         "\n\nThis code will expire in 15 minutes. Please enter it to activate your account." +
                         "\n\nIf you did not request this, please ignore this email.";
        System.out.println("==================================================");
        System.out.println("[OTP CODE GENERATED] Code: " + otpCode + " for: " + toEmail);
        System.out.println("==================================================");
        sendEmail(toEmail, subject, content);
    }

    @Async
    public void sendPasswordResetOtp(String toEmail, String otpCode) {
        String subject = "HireSmart AI — Password Reset Code";
        String content = "You requested a password reset for your HireSmart AI account.\n\n" +
                         "Your password reset code is: " + otpCode +
                         "\n\nThis code will expire in 15 minutes." +
                         "\n\nIf you did not request this, please ignore this email.";
        System.out.println("==================================================");
        System.out.println("[PASSWORD RESET OTP] Code: " + otpCode + " for: " + toEmail);
        System.out.println("==================================================");
        sendEmail(toEmail, subject, content);
    }

    @Async
    public void sendInterviewInvitation(String toEmail, String candidateName, String jobTitle, String scheduledTime, String meetingLink) {
        String subject = "Interview Scheduled — " + jobTitle + " at HireSmart AI";
        String content = "Hello " + candidateName + ",\n\nCongratulations! You have been selected for an interview for the position: " +
                         jobTitle + ".\n\nDate & Time: " + scheduledTime +
                         "\nMeeting Room Link: " + meetingLink +
                         "\n\nPlease ensure your camera and microphone are ready 5 minutes prior to the scheduled time.\n\nBest regards,\nHireSmart AI Recruitment Team";

        String htmlContent = "<div style=\"font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif; max-width: 540px; margin: 0 auto; padding: 32px 24px; border: 1px solid #e2e8f0; border-radius: 16px; background-color: #ffffff;\">"
                + "<div style=\"text-align: center; margin-bottom: 24px;\">"
                + "<h2 style=\"color: #2563eb; margin: 0; font-size: 24px; font-weight: 800; letter-spacing: -0.5px;\">🎯 HireSmart AI</h2>"
                + "<p style=\"color: #64748b; font-size: 13px; margin: 4px 0 0;\">Smart Recruitment & Talent Management Platform</p>"
                + "</div>"
                + "<div style=\"background-color: #eff6ff; border: 1px solid #bfdbfe; border-radius: 12px; padding: 20px; text-align: center; margin-bottom: 20px;\">"
                + "<span style=\"background-color: #2563eb; color: #ffffff; font-size: 11px; font-weight: 700; text-transform: uppercase; letter-spacing: 0.5px; padding: 4px 12px; border-radius: 9999px;\">Interview Invitation</span>"
                + "<h3 style=\"color: #0f172a; margin: 12px 0 6px; font-size: 18px; font-weight: 700;\">" + jobTitle + "</h3>"
                + "<p style=\"color: #475569; font-size: 13px; margin: 0;\">Candidate: <strong>" + candidateName + "</strong></p>"
                + "</div>"
                + "<div style=\"background-color: #f8fafc; border: 1px solid #e2e8f0; border-radius: 12px; padding: 18px; margin-bottom: 24px;\">"
                + "<table style=\"width: 100%; border-collapse: collapse; font-size: 13px;\">"
                + "<tr><td style=\"color: #64748b; padding: 6px 0; font-weight: 600;\">📅 Scheduled Date & Time:</td><td style=\"color: #0f172a; font-weight: 700; text-align: right;\">" + scheduledTime.replace("T", " ") + "</td></tr>"
                + "<tr><td style=\"color: #64748b; padding: 6px 0; font-weight: 600;\">🏢 Platform:</td><td style=\"color: #0f172a; font-weight: 700; text-align: right;\">HireSmart AI Talent Network</td></tr>"
                + "</table>"
                + "</div>"
                + "<div style=\"text-align: center; margin-bottom: 24px;\">"
                + "<a href=\"" + meetingLink + "\" style=\"display: inline-block; background-color: #2563eb; color: #ffffff; font-weight: 700; font-size: 14px; padding: 12px 28px; border-radius: 10px; text-decoration: none; box-shadow: 0 2px 4px rgba(37,99,235,0.2);\">"
                + "🔗 Join Interview Room"
                + "</a>"
                + "<p style=\"color: #94a3b8; font-size: 11px; margin-top: 8px;\">Meeting link: " + meetingLink + "</p>"
                + "</div>"
                + "<p style=\"color: #64748b; font-size: 12px; line-height: 1.6; margin: 0 0 16px;\">Please ensure your camera and microphone are tested and join 5 minutes before the scheduled time.</p>"
                + "<hr style=\"border: none; border-top: 1px solid #f1f5f9; margin: 20px 0;\" />"
                + "<p style=\"color: #94a3b8; font-size: 11px; text-align: center; margin: 0;\">© 2026 HireSmart AI. All rights reserved.</p>"
                + "</div>";

        System.out.println("==================================================");
        System.out.println("[INTERVIEW INVITATION DISPATCH] Sending to: " + toEmail + " for: " + candidateName);
        System.out.println("Job: " + jobTitle + " | Time: " + scheduledTime + " | Link: " + meetingLink);
        System.out.println("==================================================");

        sendEmailDirect(toEmail, subject, content, htmlContent);
    }

    private void sendEmailDirect(String toEmail, String subject, String body, String htmlContent) {
        try {
            if (mailSender != null && fromEmail != null && !fromEmail.trim().isEmpty()) {
                MimeMessage mimeMessage = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "utf-8");
                String senderDisplayName = (fromName != null && !fromName.trim().isEmpty()) ? fromName.trim() : "HireSmart AI";
                jakarta.mail.internet.InternetAddress fromAddr = new jakarta.mail.internet.InternetAddress(fromEmail, senderDisplayName, "UTF-8");
                helper.setFrom(fromAddr);
                helper.setReplyTo(fromAddr);
                mimeMessage.setHeader("From", senderDisplayName + " <" + fromEmail + ">");
                mimeMessage.setHeader("Sender", senderDisplayName + " <" + fromEmail + ">");
                mimeMessage.setHeader("X-Mailer", "HireSmart AI Platform");
                helper.setTo(toEmail);
                helper.setSubject(subject);
                helper.setText(body, htmlContent);
                mailSender.send(mimeMessage);
                System.out.println("[Email Sent] Successfully dispatched interview invitation from HireSmart AI to: " + toEmail);
            }
        } catch (Exception e) {
            System.err.println("[Email Fallback] SMTP failed: " + e.getMessage());
            System.out.println("--------------------------------------------------");
            System.out.println("TO: " + toEmail);
            System.out.println("SUBJECT: " + subject);
            System.out.println("BODY:\n" + body);
            System.out.println("--------------------------------------------------");
        }
    }

    @Async
    public void sendApplicationStatusUpdate(String toEmail, String candidateName, String jobTitle, String newStatus) {
        String subject = "Application Status Update — " + jobTitle;
        String content = "Hello " + candidateName + ",\n\nYour application status for '" + jobTitle +
                         "' has been updated to: " + newStatus +
                         ".\n\nLog in to your candidate dashboard to view detailed feedback and next steps.\n\nBest regards,\nHireSmart AI";
        sendEmail(toEmail, subject, content);
    }

    private void sendEmail(String toEmail, String subject, String body) {
        try {
            if (mailSender != null && fromEmail != null && !fromEmail.trim().isEmpty()) {
                MimeMessage mimeMessage = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "utf-8");
                String senderDisplayName = (fromName != null && !fromName.trim().isEmpty()) ? fromName.trim() : "HireSmart AI";
                jakarta.mail.internet.InternetAddress fromAddr = new jakarta.mail.internet.InternetAddress(fromEmail, senderDisplayName, "UTF-8");
                helper.setFrom(fromAddr);
                helper.setReplyTo(fromAddr);
                mimeMessage.setHeader("From", senderDisplayName + " <" + fromEmail + ">");
                mimeMessage.setHeader("Sender", senderDisplayName + " <" + fromEmail + ">");
                mimeMessage.setHeader("X-Mailer", "HireSmart AI Platform");
                helper.setTo(toEmail);
                helper.setSubject(subject);

                String code = extractCode(body);
                String htmlContent = "<div style=\"font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif; max-width: 520px; margin: 0 auto; padding: 28px; border: 1px solid #e2e8f0; border-radius: 16px; background-color: #ffffff;\">"
                    + "<div style=\"text-align: center; margin-bottom: 24px;\">"
                    + "<h2 style=\"color: #2563eb; margin: 0; font-size: 24px; font-weight: 800; letter-spacing: -0.5px;\">🎯 HireSmart AI</h2>"
                    + "<p style=\"color: #64748b; font-size: 13px; margin: 4px 0 0;\">Smart Recruitment & Talent Management Platform</p>"
                    + "</div>"
                    + "<div style=\"background-color: #f8fafc; border: 1px solid #e2e8f0; border-radius: 12px; padding: 24px; text-align: center; margin-bottom: 20px;\">"
                    + "<p style=\"color: #475569; font-size: 14px; margin: 0 0 14px; font-weight: 500;\">Your 6-Digit Verification Code:</p>"
                    + "<div style=\"font-size: 32px; font-weight: 800; letter-spacing: 8px; color: #2563eb; font-family: monospace; background: #ffffff; display: inline-block; padding: 10px 24px; border-radius: 10px; border: 2px dashed #93c5fd;\">"
                    + code
                    + "</div>"
                    + "<p style=\"color: #94a3b8; font-size: 12px; margin: 14px 0 0;\">Code valid for 15 minutes.</p>"
                    + "</div>"
                    + "<p style=\"color: #64748b; font-size: 13px; line-height: 1.6; margin: 0;\">Enter this code on the verification screen to activate your account and access your dashboard.</p>"
                    + "<hr style=\"border: none; border-top: 1px solid #f1f5f9; margin: 20px 0;\" />"
                    + "<p style=\"color: #94a3b8; font-size: 11px; text-align: center; margin: 0;\">© 2026 HireSmart AI. All rights reserved.</p>"
                    + "</div>";

                helper.setText(body, htmlContent);
                mailSender.send(mimeMessage);
                System.out.println("[Email Sent] Successfully dispatched email from HireSmart AI to: " + toEmail);
            }
        } catch (Exception e) {
            System.err.println("[Email Fallback] SMTP failed: " + e.getMessage());
            System.out.println("--------------------------------------------------");
            System.out.println("TO: " + toEmail);
            System.out.println("SUBJECT: " + subject);
            System.out.println("BODY:\n" + body);
            System.out.println("--------------------------------------------------");
        }
    }

    private String extractCode(String body) {
        Matcher m = Pattern.compile("\\b\\d{6}\\b").matcher(body);
        if (m.find()) return m.group();
        return "------";
    }
}
