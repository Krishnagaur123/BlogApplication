package com.krishna.BlogApplication.services;

public interface EmailService {
    void sendVerificationEmail(String toEmail, String verificationLink);
}
