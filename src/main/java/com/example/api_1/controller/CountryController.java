package com.example.api_1.controller;

import com.example.api_1.entity.Country;
import com.example.api_1.repo.CountryRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/countries")
public class CountryController {

    private final CountryRepository countryRepository;

    public CountryController(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    @GetMapping
    public ResponseEntity<Iterable<Country>> getCountries() {
        return ResponseEntity.ok(countryRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<Country> createCountry(@RequestBody Country country) {
        try {
            if (country.getName() == null || country.getName().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(null);
            }
            Country savedCountry = countryRepository.save(country);
            return ResponseEntity.status(201).body(savedCountry);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }
}