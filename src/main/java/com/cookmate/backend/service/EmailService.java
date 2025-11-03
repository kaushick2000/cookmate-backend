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
     * Send password reset email to user
     */
    public void sendPasswordResetEmail(String toEmail, String resetLink) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Cook Mate - Password Reset Request");
        message.setText("Hello,\n\n" +
                "You requested to reset your password. Please click the link below to reset your password:\n\n" +
                resetLink + "\n\n" +
                "This link will expire in 24 hours.\n\n" +
                "If you didn't request this, please ignore this email.\n\n" +
                "Best regards,\n" +
                "Cook Mate Team");
        
        mailSender.send(message);
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