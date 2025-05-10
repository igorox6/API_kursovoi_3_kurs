package com.example.api_1.pojo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class CompanyBody {
    private Long id;
    private String name;
    private Long idCountry;
}
