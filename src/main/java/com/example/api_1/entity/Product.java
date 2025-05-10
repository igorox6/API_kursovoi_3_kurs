package com.example.api_1.entity;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import lombok.Data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products", schema = "inf_sys_el_shop")
@Data
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String productName;

    @Column
    private Double cost;

    @Column(name = "id_company")
    private Long idCompany;



    @Column(columnDefinition = "json")
    private String properties;

    @Column
    private String photo;

    @Column
    private Integer remain;

    @Transient
    private JsonNode propertiesNode;

    // Добавляем отношение ManyToMany с Category
    @ManyToMany
    @JoinTable(
            name = "category_product",
            joinColumns = @JoinColumn(name = "id_product"),
            inverseJoinColumns = @JoinColumn(name = "id_category")
    )
    private List<Category> categories = new ArrayList<>();

    @PostLoad
    public void postLoad() throws IOException {
        if (properties != null) {
            this.propertiesNode = new ObjectMapper().readTree(properties);
        }
    }

    @PrePersist
    @PreUpdate
    public void prePersist() throws IOException {
        if (propertiesNode != null) {
            this.properties = new ObjectMapper().writeValueAsString(propertiesNode);
        } else if (properties != null) {
            this.propertiesNode = new ObjectMapper().readTree(properties);
        }
    }
}