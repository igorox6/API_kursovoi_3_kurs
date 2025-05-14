package com.example.api_1.service;

import com.example.api_1.entity.Receipt;
import com.example.api_1.pojo.ReceiptBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReceiptService {

    private static final Logger logger = LoggerFactory.getLogger(ReceiptService.class);

    private final ReceiptRepository receiptRepository;

    public ReceiptService(ReceiptRepository receiptRepository) {
        this.receiptRepository = receiptRepository;
    }

    public Map<String, Object> addReceipt(ReceiptBody body) {
        logger.info("Adding receipt for buyer ID: {}", body.getId_buyer());

        if (body.getId_buyer() == null || body.getProducts() == null || body.getProducts().isEmpty()) {
            logger.error("Failed to add receipt: id_buyer or products are missing");
            Map<String, Object> response = new HashMap<>();
            response.put("status", 400);
            response.put("error", "id_buyer and products are required");
            return response;
        }

        Map<String, Object> result = receiptRepository.addReceipt(
                new Date(body.getDate_receipt().getTime()),
                body.getId_salesman(),
                body.getId_buyer(),
                body.getPaid(),
                body.getProducts()
        );

        Integer status = ((Number) result.get("status")).intValue();
        if (status == 200) {
            logger.info("Receipt added successfully, receipt ID: {}", result.get("id_receipt"));
        } else {
            logger.error("Failed to add receipt: {}", result.get("error"));
        }

        return result;
    }

    public List<Map<String, Object>> getAllReceipts() {
        return receiptRepository.findAll();
    }

    public Map<String, Object> getReceiptByIdAndDate(Long id, String dateStr) {
        try {
            Date dateReceipt = Date.valueOf(dateStr);
            return receiptRepository.findByIdAndDate(id, dateReceipt);
        } catch (Exception e) {
            logger.error("Failed to find receipt with ID {} and date {}: {}", id, dateStr, e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("status", 404);
            response.put("error", "Receipt not found");
            return response;
        }
    }


    public Map<String, Object> deleteReceipt(Long id) {
        try {
            receiptRepository.deleteById(id);
            logger.info("Receipt deleted successfully, ID: {}", id);
            Map<String, Object> response = new HashMap<>();
            response.put("status", 200);
            response.put("message", "Receipt deleted successfully");
            return response;
        } catch (Exception e) {
            logger.error("Failed to delete receipt with ID {}: {}", id, e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("status", 500);
            response.put("error", "Failed to delete receipt: " + e.getMessage());
            return response;
        }
    }

    public Map<String, Object> getReceiptsByBuyer(Long idBuyer) {
        Map<String, Object> result = new HashMap<>();
        result.put("status", 200);

        List<Receipt> receipts = receiptRepository.findByIdBuyer(idBuyer);

        List<Map<String, Object>> unpaidReceipts = receipts.stream()
                .filter(receipt -> !receipt.isPaid())
                .map(this::convertToMap)
                .collect(Collectors.toList());
        List<Map<String, Object>> paidReceipts = receipts.stream()
                .filter(Receipt::isPaid)
                .map(this::convertToMap)
                .collect(Collectors.toList());

        List<Map<String, Object>> allReceipts = new java.util.ArrayList<>();
        allReceipts.addAll(unpaidReceipts);
        allReceipts.addAll(paidReceipts);

        result.put("data", allReceipts);
        return result;
    }

    public Map<String, Object> updateReceipt(Long id, Map<String, Object> body) {
        logger.info("Updating receipt with ID: {}", id);

        try {
            if (body.get("original_date_receipt") == null) {
                logger.error("Missing original_date_receipt for update");
                Map<String, Object> response = new HashMap<>();
                response.put("status", 400);
                response.put("error", "original_date_receipt is required");
                return response;
            }

            Date originalDate = Date.valueOf(body.get("original_date_receipt").toString());

            Integer id_salesman = body.get("id_salesman") != null ? Integer.valueOf(body.get("id_salesman").toString()) : null;
            Integer id_buyer = body.get("id_buyer") != null ? Integer.valueOf(body.get("id_buyer").toString()) : null;
            Boolean paid = body.get("paid") != null ? Boolean.valueOf(body.get("paid").toString()) : false;

            Date newDate = body.get("new_date_receipt") != null ?
                    Date.valueOf(body.get("new_date_receipt").toString()) :
                    originalDate;

            if (id_salesman == null || id_buyer == null) {
                logger.error("Missing required fields for update: id_salesman or id_buyer");
                Map<String, Object> response = new HashMap<>();
                response.put("status", 400);
                response.put("error", "id_salesman and id_buyer are required");
                return response;
            }

            Map<String, Object> result = receiptRepository.updateReceipt(
                    id,
                    originalDate, // старая дата по которой ищем
                    id_salesman,
                    id_buyer,
                    paid,
                    newDate       // новая дата для обновления
            );

            Integer status = ((Number) result.get("status")).intValue();
            if (status == 200) {
                logger.info("Receipt updated successfully, ID: {}, new date: {}", id, newDate);
            } else {
                logger.error("Failed to update receipt: {}", result.get("error"));
            }

            return result;
        } catch (Exception e) {
            logger.error("Error updating receipt with ID {}: {}", id, e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("status", 500);
            response.put("error", "Error updating receipt: " + e.getMessage());
            return response;
        }
    }


    private Map<String, Object> convertToMap(Receipt receipt) {
        if (receipt == null) return new HashMap<>();
        Map<String, Object> map = new HashMap<>();
        map.put("id", receipt.getId());
        map.put("date_receipt", receipt.getDateReceipt());
        map.put("total_sum", receipt.getTotalSum());
        map.put("paid", receipt.isPaid());
        return map;
    }

    public Map<String, Object> makePurchase(Long id, String dateStr, Map<String, Object> payload) {
        logger.info("Processing purchase for receipt ID: {} and date: {}", id, dateStr);

        try {
            Date dateReceipt = Date.valueOf(dateStr);
            Map<String, Object> existingReceipt = receiptRepository.findByIdAndDate(id, dateReceipt);
            if (existingReceipt == null || existingReceipt.isEmpty()) {
                logger.error("Receipt with ID {} and date {} not found", id, dateStr);
                Map<String, Object> response = new HashMap<>();
                response.put("status", 404);
                response.put("error", "Receipt not found");
                return response;
            }

            Date originalDate = dateReceipt;

            Integer id_salesman = payload.get("idSalesman") != null ? Integer.valueOf(payload.get("idSalesman").toString()) : null;
            Boolean paid = payload.get("paid") != null ? Boolean.valueOf(payload.get("paid").toString()) : false;
            Date newDate = payload.get("dateReceipt") != null ? Date.valueOf(payload.get("dateReceipt").toString()) : null;
            Integer id_buyer = Integer.valueOf(existingReceipt.get("id_buyer").toString());

            if (id_salesman == null || newDate == null) {
                logger.error("Missing required fields in payload");
                Map<String, Object> response = new HashMap<>();
                response.put("status", 400);
                response.put("error", "idSalesman and dateReceipt are required");
                return response;
            }

            Map<String, Object> result = receiptRepository.updateReceipt(
                    id,
                    originalDate,
                    id_salesman,
                    id_buyer,
                    paid,
                    newDate
            );

            Integer status = ((Number) result.get("status")).intValue();
            if (status == 200) {
                logger.info("Receipt purchase processed successfully, ID: {}, new date: {}", id, newDate);
            } else {
                logger.error("Failed to process purchase: {}", result.get("error"));
            }

            return result;

        } catch (Exception e) {
            logger.error("Error processing purchase with ID {} and date {}: {}", id, dateStr, e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("status", 500);
            response.put("error", "Error processing purchase: " + e.getMessage());
            return response;
        }
    }



}