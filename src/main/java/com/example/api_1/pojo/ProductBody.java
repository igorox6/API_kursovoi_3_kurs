package com.example.api_1.pojo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductBody {

    private String productName;
    private Double cost;
    private Long idCompany;
    private String properties;
    private String photo;
}