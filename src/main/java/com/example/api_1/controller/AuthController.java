package com.example.api_1.controller;


import com.example.api_1.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
@RestController
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest loginRequest) {
        try {
            Map<String, Object> response = authService.login(loginRequest.getPhoneEmail(), loginRequest.getPassword());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(401).body(errorResponse);
        }
    }
}

class LoginRequest {
    private String phoneEmail;
    private String password;

    public String getPhoneEmail() {
        return phoneEmail;
    }

    public void setPhoneEmail(String phoneEmail) {
        this.phoneEmail = phoneEmail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}