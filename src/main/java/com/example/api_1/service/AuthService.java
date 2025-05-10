package com.example.api_1.service;

import com.example.api_1.component.JwtUtil;
import com.example.api_1.entity.User;
import com.example.api_1.repo.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    private static final Pattern EMAIL_REGEX = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    private static final Pattern PHONE_REGEX = Pattern.compile("^\\+?\\d{1,3}[-.\\s]?\\(?\\d{3}\\)?[-.\\s]?\\d{3}[-.\\s]?\\d{4}$|^\\d{10}$");

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    public Map<String, Object> login(String phoneEmail, String password) {
        if (phoneEmail == null || phoneEmail.isEmpty() || password == null || password.isEmpty()) {
            throw new RuntimeException("Phone/email and password are required");
        }

        User user = isEmail(phoneEmail) ? userRepository.findByEmail(phoneEmail).orElse(null)
                : isPhone(phoneEmail) ? userRepository.findByPhone(phoneEmail).orElse(null)
                : null;

        if (user == null) {
            throw new RuntimeException("User not found with email or phone: " + phoneEmail);
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String username = user.getEmail() != null ? user.getEmail() : user.getPhone();
        String role = user.getRole() != null ? "ROLE_" + user.getRole().getName() : "ROLE_USER";
        String token = jwtUtil.generateToken(username, role);

        logger.info("Login successful for: {}", phoneEmail);

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("idRole", user.getIdRole());
        response.put("id", user.getId());
        return response;
    }

    private boolean isEmail(String input) {
        return EMAIL_REGEX.matcher(input).matches();
    }

    private boolean isPhone(String input) {
        return PHONE_REGEX.matcher(input).matches();
    }
}