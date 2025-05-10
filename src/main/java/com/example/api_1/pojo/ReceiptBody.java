package com.example.api_1.pojo;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class ReceiptBody {
    private Double total_sum;
    private Date date_receipt;
    private Integer id_salesman;
    private Integer id_buyer;
    private Boolean paid;
    private List<Map<String, Object>> products;
}