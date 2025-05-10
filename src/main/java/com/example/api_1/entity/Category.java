package com.example.api_1.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity(name = "categories")
@Getter
@Setter
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "categories_seq")
    @SequenceGenerator(name = "categories_seq", sequenceName = "inf_sys_el_shop.categories_table_id_seq", allocationSize = 1)
    private Long id;

    private Long id_parent_category;
    private String name;

    @ManyToOne
    @JoinColumn(name = "id_parent_category", insertable = false, updatable = false)
    @JsonIgnore
    private Category parentCategory;

    @OneToMany(mappedBy = "parentCategory")
    @JsonIgnore
    private List<Category> subCategories;

    @ManyToMany(mappedBy = "categories")
    @JsonIgnore
    private List<Product> products;
}