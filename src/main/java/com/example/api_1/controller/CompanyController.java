package com.example.api_1.controller;

import com.example.api_1.entity.Company;
import com.example.api_1.pojo.CompanyBody;
import com.example.api_1.repo.CompanyRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping
    public ResponseEntity<Company> createCompany(@RequestBody CompanyBody request) {
        Company company = new Company();
        company.setName(request.getName());
        company.setIdCountry(request.getIdCountry());

        Company savedCompany = companyRepository.save(company);
        return ResponseEntity.status(201).body(savedCompany);
    }

}