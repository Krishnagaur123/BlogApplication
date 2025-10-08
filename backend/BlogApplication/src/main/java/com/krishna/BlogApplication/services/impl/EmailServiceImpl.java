package com.krishna.BlogApplication.services.impl;

import com.krishna.BlogApplication.services.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    public void sendVerificationEmail(String toEmail, String verificationLink) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Verify your email address");
        message.setFrom(fromEmail);
        message.setText("Hello,\n\nPlease verify your email by clicking the link below:\n" + verificationLink +
                "\n\nThank you,\nBlog Application Team");

        mailSender.send(message);
    }
}
