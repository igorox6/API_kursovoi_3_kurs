package com.example.api_1.controller;

import com.example.api_1.pojo.BuyerDTO;
import com.example.api_1.service.BuyerService; // Заменили AuthService на BuyerService
import com.example.api_1.entity.Buyer;
import com.example.api_1.pojo.BuyerBody;
import com.example.api_1.repo.BuyerCrudRepository;
import com.example.api_1.repo.BuyerRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/buyers")
public class BuyerController {
    private final BuyerService buyerService; // Изменили на BuyerService
    private final BuyerRepository buyerRepository;
    private final BuyerCrudRepository buyerCrudRepository;

    public BuyerController(BuyerService buyerService, BuyerRepository buyerRepository, BuyerCrudRepository buyerCrudRepository) {
        this.buyerService = buyerService; // Изменили на buyerService
        this.buyerRepository = buyerRepository;
        this.buyerCrudRepository = buyerCrudRepository;
    }

    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addBuyer(@RequestBody BuyerBody request) {
        try {
            Map<String, Object> response = buyerService.register(request); // Используем buyerService
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", 400);
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping
    public ResponseEntity<Iterable<Buyer>> getBuyers() {
        return ResponseEntity.ok(buyerCrudRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Buyer> getBuyerById(@PathVariable Long id) {
        Buyer buyer = buyerCrudRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Buyer not found with id: " + id));
        return ResponseEntity.ok(buyer);
    }
    @GetMapping("/list")
    public ResponseEntity<List<BuyerDTO>> getBuyersList() {
        List<BuyerDTO> buyerDTOs = ((List<Buyer>) buyerCrudRepository.findAll())
                .stream()
                .map(buyer -> new BuyerDTO(buyer.getId(), buyer.getName(), buyer.getLastname()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(buyerDTOs);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Buyer> updateBuyer(@PathVariable Long id, @RequestBody BuyerBody buyerBody) {
        Buyer updateBuyer = buyerCrudRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Buyer not found with id: " + id));

        updateBuyer.setName(buyerBody.getBName());
        updateBuyer.setLastname(buyerBody.getBLastname());

        buyerCrudRepository.save(updateBuyer);
        return ResponseEntity.ok(updateBuyer);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBuyer(@PathVariable Long id) {
        buyerCrudRepository.deleteById(id);
        return ResponseEntity.ok("Buyer deleted successfully!");
    }
}