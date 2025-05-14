package com.example.api_1.repo;

import com.example.api_1.entity.Receipt;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ReceiptCrudRepository extends CrudRepository<Receipt, Long> {
    List<Receipt> findByBuyerId(Long idBuyer);
}