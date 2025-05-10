package com.example.api_1.repo;

import com.example.api_1.entity.Receipt;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ReceiptRepository {

    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;
    private final ReceiptCrudRepository receiptCrudRepository;

    public ReceiptRepository(JdbcTemplate jdbcTemplate, ReceiptCrudRepository receiptCrudRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = new ObjectMapper();
        this.receiptCrudRepository = receiptCrudRepository;
    }

    public Map<String, Object> addReceipt(
            Date date_receipt,
            Integer id_salesman,
            Integer id_buyer,
            Boolean paid,
            List<Map<String, Object>> products
    ) {
        try {
            String productsJson = objectMapper.writeValueAsString(products);
            String sql = "SELECT * FROM inf_sys_el_shop.add_receipt(?, ?, ?, ?, ?::jsonb)";
            return jdbcTemplate.queryForMap(sql, date_receipt, id_salesman, id_buyer, paid, productsJson);
        } catch (Exception e) {
            throw new RuntimeException("Failed to add receipt: " + e.getMessage(), e);
        }
    }

    public List<Map<String, Object>> findAll() {
        String sql = "SELECT r.*, json_agg(row_to_json(rp)) as products " +
                "FROM inf_sys_el_shop.receipts r " +
                "LEFT JOIN inf_sys_el_shop.receipt_product rp ON r.id = rp.id_receipt " +
                "GROUP BY r.id, r.total_sum, r.date_receipt";
        return jdbcTemplate.queryForList(sql);
    }

    public Map<String, Object> findByIdAndDate(Long id, Date dateReceipt) {
        String sql = "SELECT r.id, r.date_receipt, r.total_sum, r.paid, r.id_salesman, r.id_buyer, " +
                "array_agg(row_to_json(rp)) as products " +
                "FROM inf_sys_el_shop.receipts r " +
                "LEFT JOIN inf_sys_el_shop.receipt_product rp ON r.id = rp.id_receipt AND r.date_receipt = rp.date_receipt " +
                "WHERE r.id = ? AND r.date_receipt = ? " +
                "GROUP BY r.id, r.date_receipt, r.total_sum, r.paid, r.id_salesman, r.id_buyer";

        return jdbcTemplate.queryForMap(sql, id, dateReceipt);
    }

    public void deleteById(Long id) {
        String sql = "DELETE FROM inf_sys_el_shop.receipts WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    public List<Receipt> findByIdBuyer(Long idBuyer) {
        return receiptCrudRepository.findByIdBuyer(idBuyer);
    }

    public Map<String, Object> updateReceipt(
            Long id,
            Date originalDate,      // дата из PK, по которой ищем запись
            Integer id_salesman,
            Integer id_buyer,
            Boolean paid,
            Date newDate            // новая дата для обновления
    ) {
        try {
            // В SQL добавляем условие по originalDate
            String sql = "UPDATE inf_sys_el_shop.receipts " +
                    "SET id_salesman = ?, id_buyer = ?, paid = ?, date_receipt = ? " +
                    "WHERE id = ? AND date_receipt = ? " +
                    "RETURNING id, total_sum, date_receipt, paid, id_salesman, id_buyer";

            Map<String, Object> updatedReceipt = jdbcTemplate.queryForMap(
                    sql,
                    id_salesman,
                    id_buyer,
                    paid,
                    newDate,    // новое значение поля date_receipt
                    id,
                    originalDate // старая дата из PK
            );

            Map<String, Object> response = new HashMap<>();
            response.put("status", 200);
            response.put("id_receipt", updatedReceipt.get("id"));
            response.put("total_sum", updatedReceipt.get("total_sum"));
            response.put("date_receipt", updatedReceipt.get("date_receipt"));
            response.put("paid", updatedReceipt.get("paid"));
            response.put("id_salesman", updatedReceipt.get("id_salesman"));
            response.put("id_buyer", updatedReceipt.get("id_buyer"));
            return response;
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", 404);
            response.put("error", "Receipt not found or failed to update: " + e.getMessage());
            return response;
        }
    }

}