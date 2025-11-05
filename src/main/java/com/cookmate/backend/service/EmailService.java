package com.cookmate.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Value("${spring.mail.username}")
    private String fromEmail;
    
    /**
     * Send password reset email with 6-digit verification code
     */
    public void sendPasswordResetCode(String toEmail, String verificationCode) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Cook Mate - Password Reset Verification Code");
        message.setText("Hello,\n\n" +
                "You requested to reset your password. Your verification code is:\n\n" +
                "**" + verificationCode + "**\n\n" +
                "This code will expire in 15 minutes for security reasons.\n\n" +
                "If you didn't request this, please ignore this email and consider changing your password.\n\n" +
                "Best regards,\n" +
                "Cook Mate Team");
        
        mailSender.send(message);
    }
    
    /**
     * Send password reset email with reset link
     */
    public void sendPasswordResetLink(String toEmail, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Cook Mate - Password Reset Request");
        
        String resetLink = "http://localhost:3000/reset-password?token=" + token;
        message.setText("Hello,\n\n" +
                "You requested to reset your password. Click the link below to reset your password:\n\n" +
                resetLink + "\n\n" +
                "Or copy and paste this link into your browser:\n" +
                resetLink + "\n\n" +
                "This link will expire in 1 hour for security reasons.\n\n" +
                "If you didn't request this, please ignore this email and consider changing your password.\n\n" +
                "Best regards,\n" +
                "Cook Mate Team");
        
        mailSender.send(message);
    }

    /**
     * Send password reset email to user (deprecated - keeping for compatibility)
     */
    @Deprecated
    public void sendPasswordResetEmail(String toEmail, String resetLink) {
        sendPasswordResetCode(toEmail, resetLink);
    }
    
    /**
     * Send welcome email to new user
     */
    public void sendWelcomeEmail(String toEmail, String username) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Welcome to Cook Mate!");
        message.setText("Hello " + username + ",\n\n" +
                "Welcome to Cook Mate - Your Recipe Finder!\n\n" +
                "We're excited to have you on board. Start exploring delicious recipes and plan your meals.\n\n" +
                "Happy Cooking!\n\n" +
                "Best regards,\n" +
                "Cook Mate Team");
        
        mailSender.send(message);
    }
}