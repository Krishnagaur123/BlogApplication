package com.krishna.BlogApplication.controllers;

import com.krishna.BlogApplication.domain.dtos.AuthResponse;
import com.krishna.BlogApplication.domain.dtos.LoginRequest;
import com.krishna.BlogApplication.domain.entities.User;
import com.krishna.BlogApplication.repositories.UserRepository;
import com.krishna.BlogApplication.services.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest) {
        UserDetails userDetails = authenticationService.authenticate(
                loginRequest.getEmail(),
                loginRequest.getPassword()
        );
        String tokenValue = authenticationService.generateToken(userDetails);
        AuthResponse authResponse = AuthResponse.builder()
                .token(tokenValue)
                .expiresIn(86400)
                .build();
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User newUser) {
        if (userRepository.findByEmail(newUser.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email already exists");
        }

        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        userRepository.save(newUser);

        return ResponseEntity.ok("User registered successfully");
    }
}

