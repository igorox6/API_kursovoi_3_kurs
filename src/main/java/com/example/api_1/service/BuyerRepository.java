package com.example.api_1.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class BuyerRepository {

    private final JdbcTemplate jdbcTemplate;

    public BuyerRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Map<String, Object> addBuyer(
            String bName,
            String bLastname,
            String uPassword,
            String uPhoneEmail,
            boolean uBoolPhone
    ) {
        String sql = "SELECT * FROM inf_sys_el_shop.add_buyer(?, ?, ?, ?, ?)";
        return jdbcTemplate.queryForMap(sql, bName, bLastname, uPassword, uPhoneEmail, uBoolPhone);
    }

    public Long findBuyerIdByUserId(Long userId) {
        String sql = "SELECT id FROM inf_sys_el_shop.buyers WHERE id_user = ? LIMIT 1";
        return jdbcTemplate.queryForObject(sql, Long.class, userId);
    }
}