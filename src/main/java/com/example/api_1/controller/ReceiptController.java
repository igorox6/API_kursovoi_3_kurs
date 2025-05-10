package com.example.api_1.controller;

import com.example.api_1.pojo.ReceiptBody;
import com.example.api_1.service.ReceiptService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/receipts")
public class ReceiptController {

    private final ReceiptService receiptService;

    public ReceiptController(ReceiptService receiptService) {
        this.receiptService = receiptService;
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getReceipts() {
        return ResponseEntity.ok(receiptService.getAllReceipts());
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> add(@RequestBody ReceiptBody body) {
        Map<String, Object> result = receiptService.addReceipt(body);
        Integer status = ((Number) result.get("status")).intValue();
        if (status == 200) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(status).body(result);
        }
    }

    @GetMapping("/{id}/{date}")
    public ResponseEntity<Map<String, Object>> getReceiptByIdAndDate(
            @PathVariable Long id,
            @PathVariable String date  // Приходит как строка типа "2024-05-09"
    ) {
        Map<String, Object> result = receiptService.getReceiptByIdAndDate(id, date);
        Integer status = result.containsKey("status") ? ((Number) result.get("status")).intValue() : 200;
        return ResponseEntity.status(status).body(result);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteReceipt(@PathVariable Long id) {
        Map<String, Object> result = receiptService.deleteReceipt(id);
        Integer status = ((Number) result.get("status")).intValue();
        return ResponseEntity.status(status).body(result);
    }
    @GetMapping("/byBuyer/{idBuyer}")
    public ResponseEntity<Map<String, Object>> getReceiptsByBuyer(@PathVariable Long idBuyer) {
        Map<String, Object> result = receiptService.getReceiptsByBuyer(idBuyer);
        Integer status = result.containsKey("status") ? ((Number) result.get("status")).intValue() : 200;
        return ResponseEntity.status(status).body(result);
    }
    @PutMapping("/{id}/{date}/purchase")
    public ResponseEntity<Map<String, Object>> makePurchase(
            @PathVariable Long id,
            @PathVariable String date,
            @RequestBody Map<String, Object> payload
    ) {
        Map<String, Object> result = receiptService.makePurchase(id, date, payload);
        Integer status = result.containsKey("status") ? ((Number) result.get("status")).intValue() : 200;
        return ResponseEntity.status(status).body(result);
    }


}