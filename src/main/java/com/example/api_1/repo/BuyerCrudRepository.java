package com.example.api_1.repo;

import com.example.api_1.entity.Buyer;
import org.springframework.data.repository.CrudRepository;

public interface BuyerCrudRepository extends CrudRepository<Buyer, Long> {
}