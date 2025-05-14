package com.example.api_1.service;

import com.example.api_1.pojo.BuyerBody;
import com.example.api_1.repo.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@Service
public class BuyerService {

    private static final Logger logger = LoggerFactory.getLogger(BuyerService.class);

    private final BuyerRepository buyerRepository;
    private final UserRepository userRepository;
    private final PasswordHashingService passwordHashingService;

    public BuyerService(BuyerRepository buyerRepository, UserRepository userRepository,
                        PasswordHashingService passwordHashingService) {
        this.buyerRepository = buyerRepository;
        this.userRepository = userRepository;
        this.passwordHashingService = passwordHashingService;
    }

    public Map<String, Object> register(BuyerBody request) {
        logger.info("Starting registration for phone/email: {}", request.getUPhoneEmail());

        if (request.getBName() == null || request.getBName().isEmpty() ||
                request.getBLastname() == null || request.getBLastname().isEmpty() ||
                request.getUPhoneEmail() == null || request.getUPhoneEmail().isEmpty() ||
                request.getUPassword() == null || request.getUPassword().isEmpty()) {
            logger.error("Registration failed: All fields are required");
            Map<String, Object> response = new HashMap<>();
            response.put("status", 400);
            response.put("error", "All fields are required");
            return response;
        }

        if (!isEmail(request.getUPhoneEmail()) && !isPhone(request.getUPhoneEmail())) {
            logger.error("Registration failed: Invalid email or phone format: {}", request.getUPhoneEmail());
            Map<String, Object> response = new HashMap<>();
            response.put("status", 400);
            response.put("error", "Invalid email or phone format");
            return response;
        }

        if (isEmail(request.getUPhoneEmail())) {
            if (userRepository.findByEmail(request.getUPhoneEmail()).isPresent()) {
                logger.error("Registration failed: Email already exists: {}", request.getUPhoneEmail());
                Map<String, Object> response = new HashMap<>();
                response.put("status", 400);
                response.put("error", "Email already exists");
                return response;
            }
        } else if (isPhone(request.getUPhoneEmail())) {
            if (userRepository.findByPhone(request.getUPhoneEmail()).isPresent()) {
                logger.error("Registration failed: Phone already exists: {}", request.getUPhoneEmail());
                Map<String, Object> response = new HashMap<>();
                response.put("status", 400);
                response.put("error", "Phone already exists");
                return response;
            }
        }

        String hashedPassword = passwordHashingService.hashPassword(request.getUPassword());
        logger.debug("Password hashed successfully for user: {}", request.getUPhoneEmail());

        Map<String, Object> response = buyerRepository.addBuyer(
                request.getBName(),
                request.getBLastname(),
                hashedPassword,
                request.getUPhoneEmail(),
                request.isUBoolPhone()
        );

        if (((Number) response.get("status")).intValue() == 200) {
            logger.info("Registration successful for phone/email: {}, ID: {}",
                    request.getUPhoneEmail(), response.get("id_buyer"));
        } else {
            logger.error("Registration failed: {}", response.get("error"));
        }

        return response;
    }

    private boolean isEmail(String input) {
        return Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$").matcher(input).matches();
    }

    private boolean isPhone(String input) {
        return Pattern.compile("^\\+?\\d{1,3}[-.\\s]?\\(?\\d{3}\\)?[-.\\s]?\\d{3}[-.\\s]?\\d{4}$|^\\d{10}$").matcher(input).matches();
    }
}