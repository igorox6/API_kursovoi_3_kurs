package com.example.api_1.controller;

import com.example.api_1.service.AuthService;
import com.example.api_1.entity.Receipt;
import com.example.api_1.entity.User;
import com.example.api_1.pojo.UserBody;
import com.example.api_1.service.BuyerRepository;
import com.example.api_1.service.ReceiptRepository;
import com.example.api_1.repo.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserRepository repository;
    private final BuyerRepository buyerRepository;
    private final ReceiptRepository receiptRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;

    public UserController(UserRepository repository, BuyerRepository buyerRepository,
                          ReceiptRepository receiptRepository, PasswordEncoder passwordEncoder,
                          AuthService authService) {
        this.repository = repository;
        this.buyerRepository = buyerRepository;
        this.receiptRepository = receiptRepository;
        this.passwordEncoder = passwordEncoder;
        this.authService = authService;
    }

    @GetMapping
    public ResponseEntity<Iterable<User>> getUsers() {
        return ResponseEntity.ok(repository.findAll());
    }

    @PostMapping
    public ResponseEntity<User> add(@RequestBody UserBody userBody) {
        User user = new User();
        user.setPassword(passwordEncoder.encode(userBody.getPassword()));
        user.setPhone(userBody.getPhone());
        user.setId_role(userBody.getId_role());
        user.setNickname(userBody.getNickname());
        user.setEmail(userBody.getEmail());

        repository.save(user);
        return ResponseEntity.ok(user);
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

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateUser(@PathVariable long id, @RequestBody UserBody userBody) {
        User updateUser = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not exist with id: " + id));

        updateUser.setPassword(passwordEncoder.encode(userBody.getPassword()));
        updateUser.setPhone(userBody.getPhone());
        updateUser.setId_role(userBody.getId_role());
        updateUser.setNickname(userBody.getNickname());
        updateUser.setEmail(userBody.getEmail());

        repository.save(updateUser);
        return ResponseEntity.ok(updateUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable long id) {
        repository.deleteById(id);
        return ResponseEntity.ok("User deleted successfully!");
    }

    @GetMapping("/{userId}/receipts")
    public ResponseEntity<List<Receipt>> getReceiptsByUser(@PathVariable Long userId) {
        Long buyerId = buyerRepository.findBuyerIdByUserId(userId);
        List<Receipt> receipts = receiptRepository.findByIdBuyer(buyerId);
        return ResponseEntity.ok(receipts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getUserById(@PathVariable Long id) {
        User user = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        Map<String, Object> response = new HashMap<>();
        response.put("id", user.getId());
        response.put("email", user.getEmail());
        response.put("phone", user.getPhone());
        response.put("nickname", user.getNickname());
        response.put("idRole", user.getIdRole());
        Long buyerId = buyerRepository.findBuyerIdByUserId(id);
        if (buyerId != null) {
            response.put("id_buyer", buyerId);
        }

        return ResponseEntity.ok(response);
    }
}