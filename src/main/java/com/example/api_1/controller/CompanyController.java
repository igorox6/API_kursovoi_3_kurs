package com.example.api_1.controller;

import com.example.api_1.entity.Company;
import com.example.api_1.repo.CompanyRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/companies")
public class CompanyController {

    private final CompanyRepository companyRepository;

    public CompanyController(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    @GetMapping
    public ResponseEntity<Iterable<Company>> getCompanies() {
        return ResponseEntity.ok(companyRepository.findAll());
    }
}