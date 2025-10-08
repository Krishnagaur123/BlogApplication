package com.krishna.BlogApplication.controllers;

import com.krishna.BlogApplication.domain.dtos.AuthResponse;
import com.krishna.BlogApplication.domain.dtos.LoginRequest;
import com.krishna.BlogApplication.domain.entities.User;
import com.krishna.BlogApplication.repositories.UserRepository;
import com.krishna.BlogApplication.services.AuthenticationService;
import com.krishna.BlogApplication.services.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(path = "/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!user.isVerified()) {
            return ResponseEntity.status(403).body("Please verify your email before logging in.");
        }

        UserDetails userDetails = authenticationService.authenticate(
                loginRequest.getEmail(),
                loginRequest.getPassword()
        );
        String tokenValue = authenticationService.generateToken(userDetails);

        return ResponseEntity.ok(AuthResponse.builder()
                .token(tokenValue)
                .expiresIn(86400)
                .build());
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User newUser) {
        if (userRepository.findByEmail(newUser.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email already exists");
        }

        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        newUser.setVerified(false);
        newUser.setVerificationToken(UUID.randomUUID().toString());

        userRepository.save(newUser);

        // Use the injected instance, not static
        emailService.sendVerificationEmail(
                newUser.getEmail(),
                "http://localhost:8080/api/v1/auth/verify?token=" + newUser.getVerificationToken()
        );

        return ResponseEntity.ok("Registration successful! Please check your email to verify your account.");
    }


    @GetMapping("/verify")
    public ResponseEntity<String> verifyEmail(@RequestParam("token") String token) {
        var userOpt = userRepository.findByVerificationToken(token);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid or expired verification token");
        }

        User user = userOpt.get();
        user.setVerified(true);
        user.setVerificationToken(null);
        userRepository.save(user);

        return ResponseEntity.ok("Email verified successfully! You can now log in.");
    }
}

