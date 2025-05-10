package com.example.api_1.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "user_roles", schema = "inf_sys_el_shop")
@Getter
@Setter
public class Role {

    @Id
    @Column(name = "id")
    private Integer id;

    @Column(name = "name")
    private String name;

    public Role() {}

    public Role(Integer id, String name) {
        this.id = id;
        this.name = name;
    }
}